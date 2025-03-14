package greencity.service;

import com.google.maps.model.LatLng;
import greencity.annotations.RatingCalculationEnum;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.event.*;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.search.SearchEventDto;
import greencity.dto.tag.TagUaEnDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImages;
import greencity.enums.NotificationSourceType;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.rating.RatingCalculation;
import greencity.repository.EventRepo;
import greencity.repository.EventSearchRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static greencity.constant.AppConstant.AUTHORIZATION;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final TagsService tagsService;
    private final GoogleApiService googleApiService;
    private final EventSearchRepo eventSearchRepo;
    private final UserRepo userRepo;
    private final FileService fileService;
    private final NotificationService notificationService;
    private static final String DEFAULT_TITLE_IMAGE_PATH = AppConstant.DEFAULT_EVENT_IMAGES;
    private final HttpServletRequest httpServletRequest;
    private final RatingCalculation ratingCalculation;

    @Override
    @Transactional
    public EventDto save(AddEventDtoRequest addEventDtoRequest, UserVO userVO, MultipartFile[] images) {
        addAddressToLocation(addEventDtoRequest.getDatesLocations());
        Event event = modelMapper.map(addEventDtoRequest, Event.class);
        List<EventDateLocation> eventDateLocations = addEventDtoRequest.getDatesLocations()
                .stream()
                .map(date -> modelMapper.map(date, EventDateLocation.class))
                .map(date -> date.setEvent(event))
                .collect(Collectors.toList());
        event.setDates(eventDateLocations);

        event.setCreationDate(LocalDate.now());
        event.setOrganizer(modelMapper.map(userVO, User.class));

        List<TagVO> tagsVO = tagsService.findAllTranslationsByNamesAndType(addEventDtoRequest.getTags(), TagType.EVENT);
        event.setTags(modelMapper.map(tagsVO, TypeUtils.parameterize(List.class, Tag.class)));

        saveImages(images, event);
        EventDto eventDto = modelMapper.map(eventRepo.save(event), EventDto.class);
        tagConvertor(event, eventDto);

        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(() -> ratingCalculation
                .ratingCalculation(RatingCalculationEnum.ADD_EVENT, userVO, accessToken));
        return eventDto;
    }

    @Override
    public EventDto getById(Long eventId) {
        Event event =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        return buildEventDto(event);
    }

    @Override
    @Transactional
    public EventDto update(@NotNull UpdateEventDto eventDto, String email, MultipartFile[] images) {
        Event eventToUpdate = eventRepo.findById(eventDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);

        if (organizer.getRole() != Role.ROLE_ADMIN && !organizer.getId().equals(eventToUpdate.getOrganizer().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        if (findLastEventDateTime(eventToUpdate).isBefore(ZonedDateTime.now())) {
            throw new BadRequestException(ErrorMessage.EVENT_IS_FINISHED);
        }
        updatedEventWithNewData(eventToUpdate, eventDto, images);

        Event updatedEvent = eventRepo.save(eventToUpdate);

        return buildEventDto(updatedEvent);
    }

    @Override
    @Transactional
    public void delete(Long eventId, String email) {
        UserVO userVO = restClient.findByEmail(email);
        Event toDelete =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        List<String> eventImages = new ArrayList<>();
        eventImages.add(toDelete.getTitleImage());

        if (toDelete.getOrganizer().getId().equals(userVO.getId()) || userVO.getRole() == Role.ROLE_ADMIN) {
            eventRepo.delete(toDelete);
        } else {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        String accessToken = httpServletRequest.getHeader(AUTHORIZATION);
        CompletableFuture.runAsync(() -> ratingCalculation
                .ratingCalculation(RatingCalculationEnum.DELETE_EVENT, userVO, accessToken));
    }

    /**
     * Retrieves an EventVO by its ID.
     *
     * @param eventId The ID of the event to be retrieved.
     * @return The corresponding EventVO if found.
     * @throws NotFoundException If no event is found with the specified ID.
     */
    @Override
    public EventVO findById(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return modelMapper.map(event, EventVO.class);
    }

    @Override
    public PageableAdvancedDto<EventDto> getAll(Pageable page, Principal principal) {
        Page<Event> events = eventRepo.findAllByOrderByIdDesc(page);

        return buildPageableAdvancedDto(events);
    }

    /**
     * Handles the like functionality for an event.
     *
     * @param id          The ID of the event to be liked.
     * @param userVO      The UserVO who is performing the like action.
     * @param sourceType  The type of the notification source.
     */
    @Override
    @Transactional
    public void like(Long id, UserVO userVO, NotificationSourceType sourceType) {
        EventVO event = findById(id);
        boolean alreadyLiked = event.getUsersLikedEvents().stream()
                .anyMatch(user -> user.getId().equals(userVO.getId()));
        if (alreadyLiked) {
            event.getUsersLikedEvents().removeIf(user -> user.getId().equals(userVO.getId()));
        } else {
            event.getUsersLikedEvents().add(userVO);
            notificationService.createNotificationForEvent(userVO, event, sourceType);
        }
        eventRepo.save(modelMapper.map(event, Event.class));
    }

    @Override
    @Transactional
    public List<EventDtoForSubscribedUser> getAllSubscribedEvents(Long id) {
        return eventRepo.findByUsersLikedEvents_id(id).stream()
                .map(this::toSubscriberDto)
                .collect(Collectors.toList());
    }

    private EventDtoForSubscribedUser toSubscriberDto(Event event) {
        return EventDtoForSubscribedUser.builder()
                .eventTitle(event.getTitle())
                .creationDate(event.getCreationDate())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchEventDto> search(Pageable pageable, String searchQuery, String languageCode) {
        Page<Event> eventPage = eventSearchRepo.find(pageable, searchQuery, languageCode);
        return getSearchEventDtoPageableDto(eventPage);
    }

    private PageableDto<SearchEventDto> getSearchEventDtoPageableDto(Page<Event> page) {
        List<SearchEventDto> searchEventDtos = page.stream()
                .map(event -> modelMapper.map(event, SearchEventDto.class))
                .collect(Collectors.toList());

        return new PageableDto<>(
                searchEventDtos,
                page.getTotalElements(),
                page.getPageable().getPageNumber(),
                page.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getAmountOfEvents(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
        return eventRepo.countByOrganizerId(userId);
    }

    private EventDto buildEventDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }

    private ZonedDateTime findLastEventDateTime(Event event) {
        return Collections
                .max(event.getDates().stream().map(EventDateLocation::getFinishDate).collect(Collectors.toList()));
    }

    private void updatedEventWithNewData(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
        if (updateEventDto.getTitle() != null) {
            toUpdate.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getDescription() != null) {
            toUpdate.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getOpen() != null) {
            toUpdate.setOpen(updateEventDto.getOpen());
        }
        if (updateEventDto.getTags() != null) {
            toUpdate.setTags(modelMapper.map(tagsService
                            .findAllTranslationsByNamesAndType(updateEventDto.getTags(), TagType.EVENT),
                    new TypeToken<List<Tag>>() {
                    }.getType()));
        }
        updateImages(toUpdate, updateEventDto, images);

        if (updateEventDto.getDatesLocations() != null) {
            addAddressToLocation(updateEventDto.getDatesLocations());

            List<EventDateLocation> updatedDatesLocations = updateEventDto.getDatesLocations().stream()
                    .map(d -> modelMapper.map(d, EventDateLocation.class))
                    .collect(Collectors.toList());

            updatedDatesLocations.forEach(d -> d.setEvent(toUpdate));
            toUpdate.setDates(updatedDatesLocations);
        }
    }

    private void addAddressToLocation(List<EventDateLocationDto> eventDateLocationDtos) {
        eventDateLocationDtos
                .stream()
                .filter(eventDateLocationDto -> Objects.nonNull(eventDateLocationDto.getCoordinates()))
                .forEach(eventDateLocationDto -> {
                    AddressDto addressDto = eventDateLocationDto.getCoordinates();
                    AddressLatLngResponse response = googleApiService.getResultFromGeoCodeByCoordinates(
                            new LatLng(addressDto.getLatitude(), addressDto.getLongitude()));
                    eventDateLocationDto.setCoordinates(modelMapper.map(response, AddressDto.class));
                });
    }

    private void updateImages(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
        eventRepo.deleteEventAdditionalImagesByEventId(updateEventDto.getId());
        if (ArrayUtils.isEmpty(images) && updateEventDto.getImagesToDelete() == null) {
            changeOldImagesWithoutRemovingAndAdding(toUpdate, updateEventDto);
        } else if (images == null || images.length == 0) {
            deleteOldImages(toUpdate, updateEventDto);
        } else if (updateEventDto.getImagesToDelete() == null) {
            addNewImages(toUpdate, updateEventDto, images);
        } else {
            deleteImagesFromServer(updateEventDto.getImagesToDelete());
            addNewImages(toUpdate, updateEventDto, images);
        }
    }

    private void changeOldImagesWithoutRemovingAndAdding(Event toUpdate, UpdateEventDto updateEventDto) {
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
        } else {
            toUpdate.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }
        if (updateEventDto.getAdditionalImages() != null) {
            updateEventDto.getAdditionalImages().forEach(img -> toUpdate
                    .setAdditionalImages(List.of(EventImages.builder().link(img).event(toUpdate).build())));
        } else {
            toUpdate.setAdditionalImages(null);
        }
    }

    private void deleteOldImages(Event toUpdate, UpdateEventDto updateEventDto) {
        deleteImagesFromServer(updateEventDto.getImagesToDelete());
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
            if (updateEventDto.getAdditionalImages() != null) {
                toUpdate.setAdditionalImages(updateEventDto.getAdditionalImages().stream()
                        .map(url -> EventImages.builder().event(toUpdate).link(url).build())
                        .collect(Collectors.toList()));
            } else {
                toUpdate.setAdditionalImages(null);
            }
        } else {
            toUpdate.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }
    }

    private void deleteImagesFromServer(List<String> images) {
        images.stream().filter(img -> !img.equals(DEFAULT_TITLE_IMAGE_PATH)).forEach(fileService::delete);
    }

    private void addNewImages(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
        int imagesCounter = 0;
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
        } else {
            toUpdate.setTitleImage(fileService.upload(images[imagesCounter++]));
        }
        List<String> additionalImagesStr = new ArrayList<>();
        if (updateEventDto.getAdditionalImages() != null) {
            additionalImagesStr.addAll(updateEventDto.getAdditionalImages());
        }
        for (int i = imagesCounter; i < images.length; i++) {
            additionalImagesStr.add(fileService.upload(images[i]));
        }
        if (!additionalImagesStr.isEmpty()) {
            toUpdate.setAdditionalImages(additionalImagesStr.stream().map(url -> EventImages.builder()
                    .event(toUpdate).link(url).build()).collect(Collectors.toList()));
        } else {
            toUpdate.setAdditionalImages(null);
        }
    }

    private static void tagConvertor(Event event, EventDto eventDto) {
        eventDto.setTags(event.getTags()
                .stream()
                .map(tag -> {
                    TagUaEnDto tagUaEnDto = new TagUaEnDto().setId(tag.getId());
                    tagUaEnDto.setId(tag.getId());
                    tag.getTagTranslations()
                            .forEach(tagTranslation -> {
                                if (tagTranslation.getLanguage().getCode().equals("ua")) {
                                    tagUaEnDto.setNameUa(tagTranslation.getName());
                                } else if (tagTranslation.getLanguage().getCode().equals("en")) {
                                    tagUaEnDto.setNameEn(tagTranslation.getName());
                                }
                            });
                    return tagUaEnDto;
                })
                .collect(Collectors.toList()));
    }

    private void saveImages(MultipartFile[] images, Event event) {
        if (images != null && images.length > 0) {
            List<EventImages> imagesUrl = Arrays.stream(images)
                    .filter(Objects::nonNull)
                    .map(fileService::upload)
                    .map(image -> {
                        EventImages eventImages = new EventImages();
                        eventImages.setLink(image);
                        eventImages.setEvent(event);
                        return eventImages;
                    })
                    .collect(Collectors.toList());
            if (!imagesUrl.isEmpty()) {
                event.setTitleImage(imagesUrl.get(0).getLink());
                event.setAdditionalImages(imagesUrl);
            } else {
                event.setTitleImage(AppConstant.DEFAULT_EVENT_IMAGES);
            }
        } else {
            event.setTitleImage(AppConstant.DEFAULT_EVENT_IMAGES);
        }
    }

    private PageableAdvancedDto<EventDto> buildPageableAdvancedDto(Page<Event> eventsPage) {
        List<EventDto> eventDtos = modelMapper.map(eventsPage.getContent(),
                new TypeToken<List<EventDto>>() {
                }.getType());

        return new PageableAdvancedDto<>(
                eventDtos,
                eventsPage.getTotalElements(),
                eventsPage.getPageable().getPageNumber(),
                eventsPage.getTotalPages(),
                eventsPage.getNumber(),
                eventsPage.hasPrevious(),
                eventsPage.hasNext(),
                eventsPage.isFirst(),
                eventsPage.isLast());
    }
}
