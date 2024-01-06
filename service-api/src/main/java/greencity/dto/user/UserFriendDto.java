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

    /**
     * Constructor.
     *
     * @param id                  {@link Long} id
     * @param city                {@link String} city
     * @param name                {@link String} name
     * @param profilePicturePath  {@link String} profilePicturePath
     * @param rating              {@link Double} rating
     */
    public UserFriendDto(
        Long id, String city,  String name, String profilePicturePath, Double rating) {
        this.id = id;
        this.city = city;
        this.name = name;
        this.profilePicturePath = profilePicturePath;
        this.rating = rating;
    }
}
