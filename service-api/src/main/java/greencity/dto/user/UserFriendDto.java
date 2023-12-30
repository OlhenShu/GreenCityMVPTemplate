package greencity.dto.user;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserFriendDto {
    private Long chatId;
    private String city;
    private String email;
    private String friendStatus;
    private Long id;
    private Long mutualFriends;
    private String name;
    private String profilePicturePath;
    private Double rating;

    public UserFriendDto(String city, Long mutualFriends, String name, String profilePicturePath, Double rating) {
        this.city = city;
        this.mutualFriends = mutualFriends;
        this.name = name;
        this.profilePicturePath = profilePicturePath;
        this.rating = rating;
    }
}
