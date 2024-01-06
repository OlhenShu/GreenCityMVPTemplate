package greencity.mapping;

import static greencity.ModelUtils.*;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.language.LanguageVO;
import greencity.dto.tag.TagTranslationVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EcoNewsVOMapperTest {

    @InjectMocks
    private EcoNewsVOMapper ecoNewsVOMapper;

    @Test
    public void convertTest(){
        EcoNews ecoNews = getEcoNews()
            .setUsersLikedNews(new HashSet<>(List.of(getUser())))
            .setUsersDislikedNews(new HashSet<>(List.of(getUser())))
            .setEcoNewsComments(List.of(getEcoNewsComment()));

        EcoNewsVO expected = EcoNewsVO.builder()
            .id(ecoNews.getId())
            .creationDate(ecoNews.getCreationDate())
            .imagePath(ecoNews.getImagePath())
            .source(ecoNews.getSource())
            .author(UserVO.builder()
                .id(ecoNews.getAuthor().getId())
                .name(ecoNews.getAuthor().getName())
                .userStatus(ecoNews.getAuthor().getUserStatus())
                .role(ecoNews.getAuthor().getRole())
                .build())
            .title(ecoNews.getTitle())
            .text(ecoNews.getText())
            .ecoNewsComments(ecoNews.getEcoNewsComments().stream()
                .map(ecoNewsComment -> EcoNewsCommentVO.builder()
                    .id(ecoNewsComment.getId())
                    .text(ecoNewsComment.getText())
                    .createdDate(ecoNewsComment.getCreatedDate())
                    .modifiedDate(ecoNewsComment.getModifiedDate())
                    .user(UserVO.builder()
                        .id(ecoNewsComment.getUser().getId())
                        .name(ecoNewsComment.getUser().getName())
                        .userStatus(ecoNewsComment.getUser().getUserStatus())
                        .role(ecoNewsComment.getUser().getRole())
                        .build())
                    .deleted(ecoNewsComment.isDeleted())
                    .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
                    .build())
                .collect(Collectors.toList()))
            .usersLikedNews(ecoNews.getUsersLikedNews().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .tags(ecoNews.getTags().stream()
                .map(tag -> TagVO.builder()
                    .id(tag.getId())
                    .tagTranslations(tag.getTagTranslations().stream()
                        .map(tagTranslation -> TagTranslationVO.builder()
                            .id(tagTranslation.getId())
                            .name(tagTranslation.getName())
                            .languageVO(LanguageVO.builder()
                                .id(tagTranslation.getId())
                                .code(tagTranslation.getLanguage().getCode())
                                .build())
                            .build())
                        .collect(Collectors.toList()))
                    .build())
                .collect(Collectors.toList()))
            .usersDislikedNews(ecoNews.getUsersDislikedNews().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .build();

        assertEquals(expected,ecoNewsVOMapper.convert(ecoNews));
    }
}
