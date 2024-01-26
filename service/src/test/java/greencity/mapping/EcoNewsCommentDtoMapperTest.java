package greencity.mapping;

import greencity.ModelUtils;
import static greencity.ModelUtils.getEcoNewsComment;
import greencity.dto.econewscomment.EcoNewsCommentAuthorDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNewsComment;
import greencity.enums.CommentStatus;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class EcoNewsCommentDtoMapperTest {

    @InjectMocks
    private EcoNewsCommentDtoMapper ecoNewsCommentDtoMapper;

    @Test
    public void convertDeletedStatusTest() {
        EcoNewsComment ecoNewsComment = getEcoNewsComment()
            .setDeleted(true);

        EcoNewsCommentDto expected = EcoNewsCommentDto.builder()
            .id(ecoNewsComment.getId())
            .modifiedDate(ecoNewsComment.getModifiedDate())
            .status(CommentStatus.DELETED)
            .build();

        assertEquals(expected, ecoNewsCommentDtoMapper.convert(ecoNewsComment));
    }

    @Test
    public void convertOriginalStatusTest() {
        EcoNewsComment ecoNewsComment = getEcoNewsComment()
            .setModifiedDate(ModelUtils.localDateTime)
            .setCreatedDate(ModelUtils.localDateTime)
            .setUsersLiked(new HashSet<>());

        EcoNewsCommentDto expected = getEcoNewsCommentDto(ecoNewsComment,CommentStatus.ORIGINAL);

        assertEquals(expected, ecoNewsCommentDtoMapper.convert(ecoNewsComment));
    }

    @Test
    public void convertEditStatusTest() {
        EcoNewsComment ecoNewsComment = getEcoNewsComment()
            .setCreatedDate(ModelUtils.localDateTime)
            .setModifiedDate(ModelUtils.localDateTime.plusMinutes(1))
            .setUsersLiked(new HashSet<>());

        EcoNewsCommentDto expected = getEcoNewsCommentDto(ecoNewsComment,CommentStatus.EDITED);

        assertEquals(expected, ecoNewsCommentDtoMapper.convert(ecoNewsComment));
    }

    private static EcoNewsCommentDto getEcoNewsCommentDto(EcoNewsComment ecoNewsComment,CommentStatus commentStatus) {
        return EcoNewsCommentDto.builder()
            .id(ecoNewsComment.getId())
            .modifiedDate(ecoNewsComment.getModifiedDate())
            .status(commentStatus)
            .text(ecoNewsComment.getText())
            .author(EcoNewsCommentAuthorDto.builder()
                .id(ecoNewsComment.getUser().getId())
                .name(ecoNewsComment.getUser().getName())
                .userProfilePicturePath(ecoNewsComment.getUser().getProfilePicturePath()).build()
            )
            .likes(ecoNewsComment.getUsersLiked().size())
            .currentUserLiked(ecoNewsComment.isCurrentUserLiked())
            .build();
    }
}
