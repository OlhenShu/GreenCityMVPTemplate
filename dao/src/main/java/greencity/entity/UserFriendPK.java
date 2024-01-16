package greencity.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.*;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserFriendPK implements Serializable {
    private Long userId;
    private Long friendId;
}
