package greencity.service;

import greencity.constant.AppConstant;
import greencity.dto.event.AddEventDto;
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
    public EventDto save(AddEventDto addEventDto, UserVO userVO, List<MultipartFile> images) {
        Event event = modelMapper.map(addEventDto, Event.class);
        List<EventDateLocation> eventDateLocations = addEventDto.getDatesLocations()
                .stream()
                .map(date -> modelMapper.map(date, EventDateLocation.class))
                .collect(Collectors.toList());
        event.setDates(eventDateLocations);
        event.setCreationDate(LocalDate.now());
        event.setOrganizer(modelMapper.map(userVO, User.class));

        event.setDates(event.getDates()
                .stream()
                .map(date->date.setEvent(event))
                .collect(Collectors.toList()));

        List<TagVO> tagsVO = tagsService.findTagsByNamesAndType(addEventDto.getTags(), TagType.EVENT);
        event.setTags(modelMapper.map(tagsVO, TypeUtils.parameterize(List.class, Tag.class)));

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
        return modelMapper.map(eventRepo.save(event), EventDto.class);
    }
}
