package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.search.SearchEventDto;
import greencity.entity.event.Event;
import greencity.entity.localization.TagTranslation;
import java.time.LocalDate;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class SearchEventDtoMapperTest {
    @InjectMocks
    private SearchEventDtoMapper searchEventDtoMapper;

    @Test
    void convert() {
        String language = LocaleContextHolder.getLocale().getLanguage();
        Event event = Event.builder()
            .id(1L)
            .title("title1")
            .description("description1")
            .titleImage("img")
            .open(false)
            .organizer(ModelUtils.getUser())
            .creationDate(LocalDate.now())
            .tags(ModelUtils.getTags())
            .build();
        SearchEventDto searchEventDto = SearchEventDto.builder()
            .id(event.getId())
            .title(event.getTitle())
            .creationDate(event.getCreationDate())
            .tags(event.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .filter(tagTranslation -> tagTranslation.getLanguage().getCode().equals(language))
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .organizer(new EventAuthorDto(event.getOrganizer().getId(), event.getOrganizer().getName(),
                event.getOrganizer().getEventOrganizerRating()))
            .build();

        assertEquals(searchEventDto, searchEventDtoMapper.convert(event));
    }
}
