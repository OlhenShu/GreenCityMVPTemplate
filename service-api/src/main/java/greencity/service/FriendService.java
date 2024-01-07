package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserFriendFilterDto;
import greencity.dto.user.UserVO;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {FriendsDto} entity.
 *
 * @author Denys Liubchenko
 * @version 1.0
 */
public interface FriendService {
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
}
