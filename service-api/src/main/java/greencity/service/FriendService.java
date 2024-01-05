package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {FriendsDto} entity.
 *
 * @author Denys Liubchenko
 * @version 1.0
 */
public interface FriendService {
    /**
     * Method returns all user's friends {@code User} by userId.
     *
     * @param userId        {@code User} id.
     * @param pageable - instance of {@link Pageable}.
     * @return {@link UserFriendDto}.
     */
    PageableDto<UserFriendDto> findAllUsersFriends(Long userId, Pageable pageable);
}
