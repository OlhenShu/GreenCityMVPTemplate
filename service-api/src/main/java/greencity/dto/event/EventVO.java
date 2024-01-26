package greencity.dto.event;

import greencity.dto.user.UserVO;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class EventVO {
    private Long id;
    private String title;
    private String titleImage;
    private UserVO organizer;
    private String description;
    private Set<UserVO> usersLikedEvents = new HashSet<>();
}