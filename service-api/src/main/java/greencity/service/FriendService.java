package greencity.service;

/**
 * Provides the interface to manage {FriendsDto} entity.
 *
 * @author Denys Liubchenko
 * @version 1.0
 */
public interface FriendService {
    /**
     * Deletes the friendship between two users identified by their user IDs.
     *
     * @param userId   The ID of the user initiating the friendship deletion.
     * @param friendId The ID of the user who is the friend to be removed.
     */
    void deleteUserFriend(Long userId, Long friendId);
}
