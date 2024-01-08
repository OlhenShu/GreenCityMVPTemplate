package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.RecommendFriendDto;
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
     * Retrieves a paginated list of recommended friends for a given user within a specified paging configuration.
     *
     * @param user     The UserVO object representing the user for whom friend recommendations are sought.
     * @param pageable Pageable object defining the page size, page number, and sorting criteria for the result set.
     * @return A {@link PageableDto} with {@link RecommendFriendDto} objects representing recommended friends
     * @author Nikita Malov
     */
    PageableDto<RecommendFriendDto> getRecommendedFriends(UserVO user, Pageable pageable);
}
