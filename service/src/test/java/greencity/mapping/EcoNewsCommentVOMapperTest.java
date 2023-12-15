package greencity.mapping;

import static greencity.ModelUtils.getEcoNewsComment;
import static greencity.ModelUtils.getUser;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNewsComment;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EcoNewsCommentVOMapperTest {

    @InjectMocks
    private EcoNewsCommentVOMapper ecoNewsCommentVOMapper;

    @Test
    public void convertWithParentCommentTest() {
        EcoNewsComment parentComment = getEcoNewsComment()
            .setUsersLiked(new HashSet<>(List.of(getUser())));
        EcoNewsComment ecoNewsComment = getEcoNewsComment()
            .setUsersLiked(new HashSet<>(List.of(getUser())))
            .setParentComment(parentComment);

        EcoNewsCommentVO expected = convertWithNullParentComment(ecoNewsComment);
        expected.setParentComment(convertWithNullParentComment(parentComment));


        assertEquals(expected, ecoNewsCommentVOMapper.convert(ecoNewsComment));
    }

    @Test
    public void convertWithoutParentCommentTest() {
        EcoNewsComment ecoNewsComment = getEcoNewsComment()
            .setUsersLiked(new HashSet<>(List.of(getUser())));

        EcoNewsCommentVO expected = convertWithNullParentComment(ecoNewsComment);

        assertEquals(expected, ecoNewsCommentVOMapper.convert(ecoNewsComment));
    }

    private EcoNewsCommentVO convertWithNullParentComment(EcoNewsComment ecoNewsComment) {
        return EcoNewsCommentVO.builder()
            .id(ecoNewsComment.getId())
            .user(UserVO.builder()
                .id(ecoNewsComment.getUser().getId())
                .role(ecoNewsComment.getUser().getRole())
                .name(ecoNewsComment.getUser().getName())
                .build())
            .modifiedDate(ecoNewsComment.getModifiedDate())
            .parentComment(null)
            .text(ecoNewsComment.getText())
            .deleted(ecoNewsComment.isDeleted())
            .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
            .createdDate(ecoNewsComment.getCreatedDate())
            .usersLiked(ecoNewsComment.getUsersLiked().stream().map(
                user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .ecoNews(EcoNewsVO.builder()
                .id(ecoNewsComment.getEcoNews().getId())
                .build())
            .build();
    }
}
