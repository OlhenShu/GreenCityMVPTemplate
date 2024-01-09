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
}
