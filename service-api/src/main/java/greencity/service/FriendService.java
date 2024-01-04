package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitAssignManagementDto;
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
     * Method to find {@code User} by habitAssignId, userId and specific.
     *
     * @param userId        {@code User} id.
     * @param pageable  {@code HabitAssign} id.
     * @return {@link PageableDto}.
     */
    PageableDto<UserFriendDto> findAllUsersFriends(Long userId, Pageable pageable);
}
