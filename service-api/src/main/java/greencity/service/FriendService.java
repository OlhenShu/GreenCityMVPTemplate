package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;
import greencity.dto.user.RecommendFriendDto;
import greencity.dto.user.UserFriendFilterDto;
import java.time.ZonedDateTime;

public interface FriendService {
    /**
     * Method that returns PageableDto of UserFriendDto by name, city, mutual friends.
     *
     * @param pageable         {@link Pageable}
     * @param name             {@link String} name
     * @param userVO           {@link UserVO}
     * @param hasSameCity      {@link Boolean} hasSameCity
     * @param hasMutualFriends {@link Boolean} hasMutualFriends
     * @return {@link PageableDto} of {@link UserFriendDto}
     */
    PageableDto<UserFriendDto> searchFriends(Pageable pageable, String name, UserVO userVO,
                                             Boolean hasSameCity, Boolean hasMutualFriends);

    /**
     * Establishes a friendship relationship between a user and another user identified by their IDs.
     *
     * @param userId       The unique identifier of the user initiating the friendship.
     * @param friendId The unique identifier of the user to be added as a friend.
     */
    void addFriend(Long userId, Long friendId);

    /**
     * Deletes a friendship relationship between a user and another user identified by their IDs.
     *
     * @param userId       The unique identifier of the user initiating the friendship.
     * @param friendId The unique identifier of the user to be added as a friend.
     */
    void deleteUserFriend(Long userId, Long friendId);

    /**
     * Retrieves a paginated list of recommended friends for a given user within a specified paging configuration.
     *
     * @param user     The UserVO object representing the user for whom friend recommendations are sought.
     * @param pageable Pageable object defining the page size, page number, and sorting criteria for the result set.
     * @return A {@link PageableDto} with {@link RecommendFriendDto} objects representing recommended friends
     * @author Nikita Malov
     */
    PageableDto<RecommendFriendDto> getRecommendedFriends(UserVO user, Pageable pageable);

    /**
     * Retrieves a paginated list of friends' details with specific filtering criteria.
     *
     * @param pageable               Pagination information for the resulting list.
     * @param name                   The criteria for filtering friend names (can be null).
     * @param userVO                 User, which friends are being fetched.
     * @param hasSameCity            Whether to filter friends by the same city as the user.
     * @param highestPersonalRate    The maximum rating allowed for friends in the result set.
     * @param dateTimeOfAddingFriend The date when friends were added, filtering based on this timestamp.
     * @return                       A paginated list of {@link UserFriendFilterDto} containing friend details.
     */
    PageableDto<UserFriendDto> getAllFriendsByDifferentParameters(
        Pageable pageable, String name, UserVO userVO, Boolean hasSameCity, Double highestPersonalRate,
        ZonedDateTime dateTimeOfAddingFriend);

    /**
     * Method returns all user's friends {@code User} by userId.
     *
     * @param userId        {@code User} id.
     * @param pageable - instance of {@link Pageable}.
     * @return {@link UserFriendDto}.
     */
    PageableDto<UserFriendDto> findAllUsersFriends(Long userId, Pageable pageable);
}
