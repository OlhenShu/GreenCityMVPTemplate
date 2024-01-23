package greencity.mapping;

import greencity.dto.event.EventAuthorDto;
import greencity.dto.search.SearchEventDto;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.localization.TagTranslation;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SearchEventDtoMapper extends AbstractConverter<Event, SearchEventDto> {
    @Override
    protected SearchEventDto convert(Event event) {
        User author = event.getOrganizer();
        String language = LocaleContextHolder.getLocale().getLanguage();

        return SearchEventDto.builder()
            .id(event.getId())
            .title(event.getTitle())
            .organizer(new EventAuthorDto(author.getId(),
                author.getName(), author.getEventOrganizerRating()))
            .creationDate(event.getCreationDate())
            .tags(event.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .filter(tagTranslation -> tagTranslation.getLanguage().getCode().equals(language))
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .build();
    }
}