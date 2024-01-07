package greencity.dto.user;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserFriendFilterDto extends UserFriendDto {
    private Long mutualCommentedEcoNews;

    /**
     * Constructor.
     *
     * @param id                     {@link Long} id
     * @param city                   {@link String} city
     * @param name                   {@link String} name
     * @param profilePicturePath     {@link String} profilePicturePath
     * @param rating                 {@link Double} rating
     * @param mutualCommentedEcoNews {@link Long} mutualCommentedEcoNews
     */
    public UserFriendFilterDto(
        Long id, String city,  String name, String profilePicturePath, Double rating, Long mutualCommentedEcoNews) {
        super(id, city, name, profilePicturePath, rating);
        this.mutualCommentedEcoNews = mutualCommentedEcoNews;
    }
}
