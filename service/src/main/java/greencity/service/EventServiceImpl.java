package greencity.service;

import com.google.maps.model.LatLng;
import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.search.SearchEventDto;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import greencity.repository.EventSearchRepo;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final EventSearchRepo eventSearchRepo;

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
        updatedEventWithNewData(eventToUpdate, eventDto);

        Event updatedEvent = eventRepo.save(eventToUpdate);

        return buildEventDto(updatedEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchEventDto> search(Pageable pageable, String searchQuery, String languageCode) {
        Page<Event> eventPage = eventSearchRepo.find(pageable,searchQuery,languageCode);
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

    private EventDto buildEventDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }

    private ZonedDateTime findLastEventDateTime(Event event) {
        return Collections
                .max(event.getDates().stream().map(EventDateLocation::getFinishDate).collect(Collectors.toList()));
    }

    private void updatedEventWithNewData(Event toUpdate, UpdateEventDto updateEventDto) {
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
}
