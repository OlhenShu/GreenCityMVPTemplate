package greencity.service;

import com.google.maps.model.LatLng;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.Event;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.EventImages;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final TagsService tagsService;
    private final GoogleApiService googleApiService;
    private final FileService fileService;
    private static final String DEFAULT_TITLE_IMAGE_PATH = AppConstant.DEFAULT_EVENT_IMAGES;

    @Override
    public EventDto getById(Long eventId) {
        Event event =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        return buildEventDto(event);
    }

    @Override
    @Transactional
    public EventDto update(UpdateEventDto eventDto, String email, MultipartFile[] images) {
        Event eventToUpdate = eventRepo.findById(eventDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);

        if (organizer.getRole() != Role.ROLE_ADMIN && organizer.getRole() != Role.ROLE_MODERATOR
                && !organizer.getId().equals(eventToUpdate.getOrganizer().getId())) {
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
    public String uploadImage(MultipartFile image) {
        if (image != null) {
            return fileService.upload(image);
        }
        return null;
    }

    @Override
    public String[] uploadImages(MultipartFile[] images) {
        return Arrays.stream(images).map(fileService::upload).toArray(String[]::new);
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
                            .findTagsWithAllTranslationsByNamesAndType(updateEventDto.getTags(), TagType.EVENT),
                    new TypeToken<List<Tag>>() {
                    }.getType()));
        }

        updateImages(toUpdate, updateEventDto, images);

        if (updateEventDto.getDatesLocations() != null) {
            addAddressToLocation(updateEventDto.getDatesLocations());
            toUpdate.setDates(updateEventDto.getDatesLocations().stream()
                    .map(d -> modelMapper.map(d, EventDateLocation.class))
                    .peek(d -> d.setEvent(toUpdate))
                    .collect(Collectors.toList()));
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

//    private void addNewImages(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
//        int imagesCounter = 0;
//        if (updateEventDto.getTitleImage() != null) {
//            toUpdate.setTitleImage(updateEventDto.getTitleImage());
//        } else {
//            toUpdate.setTitleImage(fileService.upload(images[imagesCounter++]));
//        }
//        List<String> additionalImagesStr = new ArrayList<>();
//        if (updateEventDto.getAdditionalImages() != null) {
//            additionalImagesStr.addAll(updateEventDto.getAdditionalImages());
//        }
//        for (int i = imagesCounter; i < images.length; i++) {
//            additionalImagesStr.add(fileService.upload(images[imagesCounter++]));
//        }
//        if (!additionalImagesStr.isEmpty()) {
//            toUpdate.setAdditionalImages(additionalImagesStr.stream().map(url -> EventImages.builder()
//                    .event(toUpdate).link(url).build()).collect(Collectors.toList()));
//        } else {
//            toUpdate.setAdditionalImages(null);
//        }
//    }
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
            additionalImagesStr.add(fileService.upload(images[i]));  // Use the fileService.upload method here
        }
        if (!additionalImagesStr.isEmpty()) {
            toUpdate.setAdditionalImages(additionalImagesStr.stream().map(url -> EventImages.builder()
                    .event(toUpdate).link(url).build()).collect(Collectors.toList()));
        } else {
            toUpdate.setAdditionalImages(null);
        }
    }
}
