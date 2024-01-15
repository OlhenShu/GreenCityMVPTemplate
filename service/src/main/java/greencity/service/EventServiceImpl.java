package greencity.service;

import com.google.maps.model.LatLng;
import greencity.constant.AppConstant;
import greencity.dto.geocoding.AddressDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.RequestAddEventDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagUaEnDto;
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
    private final GoogleApiService googleApiService;

    @Override
    public EventDto save(RequestAddEventDto requestAddEventDto, UserVO userVO, List<MultipartFile> images) {
        Event event = modelMapper.map(requestAddEventDto, Event.class);
        List<EventDateLocation> eventDateLocations = requestAddEventDto.getDatesLocations()
                .stream()
                .map(date -> modelMapper.map(date, EventDateLocation.class))
                .map(date -> date.setEvent(event))
                .collect(Collectors.toList());
        event.setDates(eventDateLocations);

        event.setCreationDate(LocalDate.now());
        event.setOrganizer(modelMapper.map(userVO, User.class));

        List<TagVO> tagsVO = tagsService.findTagsWithAllTranslationsByNamesAndType(requestAddEventDto.getTags(), TagType.EVENT);
        event.setTags(modelMapper.map(tagsVO, TypeUtils.parameterize(List.class, Tag.class)));

        saveImages(images, event);
        EventDto eventDto = modelMapper.map(eventRepo.save(event), EventDto.class);
        tagConvertor(event, eventDto);
        addAddressToLocation(eventDto.getDates());
        return eventDto;
    }

    private static void tagConvertor(Event event, EventDto eventDto) {
        eventDto.setTags(event.getTags()
                .stream()
                .map(tag -> {
                    TagUaEnDto tagUaEnDto = new TagUaEnDto().setId(tag.getId());
                    tagUaEnDto.setId(tag.getId());
                    tag.getTagTranslations()
                            .forEach(tagTranslation -> {
                                if (tagTranslation.getLanguage().getCode().equals("ua")){
                                    tagUaEnDto.setNameUa(tagTranslation.getName());
                                }else if (tagTranslation.getLanguage().getCode().equals("en")){
                                    tagUaEnDto.setNameEn(tagTranslation.getName());
                                }
                            });
                return tagUaEnDto;})
                .collect(Collectors.toList()));
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

    private void addAddressToLocation(List<EventDateLocationDto> eventDateLocationDtos) {
        eventDateLocationDtos
                .stream()
                .filter(eventDateLocationDto -> Objects.nonNull(eventDateLocationDto.getCoordinates()))
                .forEach(eventDateLocationDto -> {
                    AddressDto addressDto = eventDateLocationDto.getCoordinates();
                    AddressDto response = googleApiService.getResultFromGeoCodeByCoordinates(
                            new LatLng(addressDto.getLatitude(), addressDto.getLongitude()));
                    eventDateLocationDto.setCoordinates(response);
                });
    }
}
