package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.search.SearchEventDto;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.search.SearchResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final EcoNewsService ecoNewsService;
    private final EventService eventService;

    /**
     * Method that allow you to search {@link SearchResponseDto}.
     *
     * @param searchQuery query to search
     * @return list of {@link SearchResponseDto}
     */
    @Override
    public SearchResponseDto search(String searchQuery, String languageCode) {
        PageableDto<SearchNewsDto> ecoNews = ecoNewsService.search(searchQuery, languageCode);
        PageableDto<SearchEventDto> events = eventService.search(PageRequest.of(0,3), searchQuery,languageCode);

        return SearchResponseDto.builder()
            .ecoNews(ecoNews.getPage())
            .events(events.getPage())
            .countOfEcoNews(ecoNews.getTotalElements())
            .countOfEvents(events.getTotalElements())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchNewsDto> searchAllNews(Pageable pageable, String searchQuery, String languageCode) {
        return ecoNewsService.search(pageable, searchQuery, languageCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchEventDto> searchAllEvents(Pageable pageable, String searchQuery, String languageCode) {
        return eventService.search(pageable, searchQuery, languageCode);
    }
}
