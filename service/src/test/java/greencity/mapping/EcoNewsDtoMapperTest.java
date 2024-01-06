package greencity.mapping;

import static greencity.ModelUtils.getEcoNews;
import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.TagTranslation;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EcoNewsDtoMapperTest {

    @InjectMocks
    private EcoNewsDtoMapper ecoNewsDtoMapper;

    @Test
    public void covertTest() {
        EcoNews ecoNews = getEcoNews();

        String languageCodeUA = "ua";

        EcoNewsDto expected =
            EcoNewsDto.builder()
                .id(ecoNews.getId())
                .title(ecoNews.getTitle())
                .content(ecoNews.getText())
                .shortInfo(ecoNews.getShortInfo())
                    .author(EcoNewsAuthorDto.builder()
                    .id(ecoNews.getAuthor().getId())
                    .name(ecoNews.getAuthor().getName())
                    .build())
                .creationDate(ecoNews.getCreationDate())
                .imagePath(ecoNews.getImagePath())
                .likes(ecoNews.getUsersLikedNews().size())
                .dislikes(ecoNews.getUsersDislikedNews().size())
                .countComments((int)ecoNews.getEcoNewsComments()
                    .stream().filter(ecoNewsComment -> !ecoNewsComment.isDeleted()).count())
                .tags(ecoNews.getTags().stream()
                    .flatMap(tag -> tag.getTagTranslations().stream())
                    .filter(tagTranslation -> tagTranslation.getLanguage().getCode()
                        .equals(AppConstant.DEFAULT_LANGUAGE_CODE))
                    .map(TagTranslation::getName).collect(Collectors.toList()))
                .tagsUa(ecoNews.getTags().stream()
                    .flatMap(tag -> tag.getTagTranslations().stream())
                    .filter(tagTranslation -> tagTranslation.getLanguage().getCode().equals(languageCodeUA))
                    .map(TagTranslation::getName).collect(Collectors.toList()))
                .build();

        assertEquals(expected,ecoNewsDtoMapper.convert(ecoNews));
    }
}
