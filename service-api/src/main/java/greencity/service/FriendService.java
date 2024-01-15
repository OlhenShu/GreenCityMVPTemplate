package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {@link UserFriendDto} entity.
 *
 * @author Denys Liubchenko
 * @version 1.0
 */
public interface FriendService {
    /**
     * Retrieves a pageable list of user friends for the specified user.
     * This method returns a PageableDto containing a paginated list of UserFriendDto objects representing
     * the friends of the user identified by the provided userId. The list is pageable based on the given Pageable
     * parameters.
     *
     * @param userId   The ID of the user for whom to retrieve friends.
     * @param pageable The Pageable object specifying the page, size, and sorting criteria.
     * @return {@link PageableDto} of {@link UserFriendDto} containing the paginated list of user friends.
     */
    PageableDto<UserFriendDto> getUserFriendsByUserId(Long userId, Pageable pageable);

    /**
     * Method that returns PageableDto of UserFriendDto by name, city, mutual friends.
     *
     * @param pageable         {@link Pageable}
     * @param name             {@link String} name
     * @param userVO           {@link UserVO}
     * @param hasSameCity      {@link Boolean} hasSameCity
     * @param hasMutualFriends {@link Boolean} hasMutualFriends
     * @return {@link PageableDto} of {@link UserFriendDto}
     * @author Denys Liubchenko
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
     * Accepts a friend request from another user, establishing a mutual friendship.
     *
     * @param userId   The ID of the user accepting the friend request.
     * @param friendId The ID of the user who sent the friend request.
     * @author Dmytro Klopov
     */
    void acceptFriendRequest(Long userId, Long friendId);
    /**
     * Declines a friend request from another user, rejecting the establishment of friendship.
     *
     * @param userId   The ID of the user declining the friend request.
     * @param friendId The ID of the user who sent the friend request.
     * @author Dmytro Klopov
     */
    void declineFriendRequest(Long userId, Long friendId);
}
