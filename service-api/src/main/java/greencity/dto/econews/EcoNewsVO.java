package greencity.dto.econews;

import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"ecoNewsComments", "tags"})
public class EcoNewsVO {
    private Long id;

    private ZonedDateTime creationDate;

    private String imagePath;

    private String source;

    private UserVO author;

    private String title;

    private String text;

    private List<EcoNewsCommentVO> ecoNewsComments = new ArrayList<>();

    private Set<UserVO> usersLikedNews = new HashSet<>();

    private List<TagVO> tags;

    private Set<UserVO> usersDislikedNews = new HashSet<>();
}
