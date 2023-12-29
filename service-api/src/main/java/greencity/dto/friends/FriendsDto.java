package greencity.dto.friends;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FriendsDto {
    private String profilePicturePath;
    private String name;
    private Double rating;
    private String city;
    private Long mutualFriends;
}
