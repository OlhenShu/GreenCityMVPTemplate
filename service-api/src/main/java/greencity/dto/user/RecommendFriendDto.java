package greencity.dto.user;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RecommendFriendDto {
    private Long id;
    private String city;
    private String name;
    private String profilePicturePath;
    private Double rating;
    private Long mutualHabits;
    private Long mutualFriends;
}
