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
     * Method to retrieve a paginated list of recommended friends for the specified user.
     *
     * @param user     the user object containing information about the user for whom recommended friends are to be fetched
     * @param pageable object specifying pagination information
     * @return a {@link PageableDto} containing {@link UserFriendDto} objects representing recommended friends
     * @author Nikita Malov
     */
    PageableDto<UserFriendDto> getRecommendedFriends(UserVO user, Pageable pageable);
}
