package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.entity.EventDateLocation;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;

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
        enhanceWithNewData(eventToUpdate, eventDto);

        Event updatedEvent = eventRepo.save(eventToUpdate);

        return buildEventDto(updatedEvent);
    }

    private EventDto buildEventDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }

    private ZonedDateTime findLastEventDateTime(Event event) {
        return Collections
                .max(event.getDates().stream().map(EventDateLocation::getFinishDate).collect(Collectors.toList()));
    }

    private void enhanceWithNewData(Event toUpdate, UpdateEventDto updateEventDto) {
        if (updateEventDto.getTitle() != null) {
            toUpdate.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getDescription() != null) {
            toUpdate.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getIsOpen() != null) {
            toUpdate.setOpen(updateEventDto.getIsOpen());
        }
    }
}
