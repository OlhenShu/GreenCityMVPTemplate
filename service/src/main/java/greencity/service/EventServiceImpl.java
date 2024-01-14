package greencity.service;

import greencity.constant.AppConstant;
import greencity.dto.event.RequestAddEventDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.Event;
import greencity.entity.EventDateLocation;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.enums.TagType;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final FileService fileService;
    private final TagsService tagsService;

    @Override
    public EventDto save(RequestAddEventDto requestAddEventDto, UserVO userVO, List<MultipartFile> images) {
        Event event = modelMapper.map(requestAddEventDto, Event.class);
        List<EventDateLocation> eventDateLocations = requestAddEventDto.getDatesLocations()
                .stream()
                .map(date -> modelMapper.map(date, EventDateLocation.class))
                .map(date->date.setEvent(event))
                .collect(Collectors.toList());
        event.setDates(eventDateLocations);

        event.setCreationDate(LocalDate.now());
        event.setOrganizer(modelMapper.map(userVO, User.class));

        List<TagVO> tagsVO = tagsService.findTagsByNamesAndType(requestAddEventDto.getTags(), TagType.EVENT);
        event.setTags(modelMapper.map(tagsVO, TypeUtils.parameterize(List.class, Tag.class)));

        saveImages(images, event);
        return modelMapper.map(eventRepo.save(event), EventDto.class);
    }

    private void saveImages(List<MultipartFile> images, Event event) {
        if (images == null || images.isEmpty()) {
            event.setTitleImage(AppConstant.DEFAULT_HABIT_IMAGE);
        } else {
            List<String> imagesUrl = images
                    .stream()
                    .filter(Objects::nonNull)
                    .map(fileService::upload)
                    .collect(Collectors.toList());
            event.setTitleImage(imagesUrl.get(0));
            event.setImages(imagesUrl);
        }
    }
}
