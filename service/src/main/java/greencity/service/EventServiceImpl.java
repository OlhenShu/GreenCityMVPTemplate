package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.event.EventVO;
import greencity.dto.user.UserVO;
import greencity.entity.event.Event;
import greencity.entity.event.EventImages;
import greencity.enums.Role;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;


    @Override
    public void delete(Long eventId, String email) {
        UserVO userVO = restClient.findByEmail(email);
        Event toDelete =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        List<String> eventImages = new ArrayList<>();
        eventImages.add(toDelete.getTitleImage());
        if (toDelete.getAdditionalImages() != null) {
            eventImages.addAll(toDelete.getAdditionalImages().stream().map(EventImages::getLink)
                    .collect(Collectors.toList()));
        }

        if (toDelete.getOrganizer().getId().equals(userVO.getId()) || userVO.getRole() == Role.ROLE_ADMIN) {
            eventRepo.delete(toDelete);
        } else {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
    }

    @Override
    public EventVO findById(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return modelMapper.map(event, EventVO.class);
    }

}