package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

/**
 * Provides the interface to manage {FriendsDto} entity.
 *
 * @author Denys Liubchenko
 * @version 1.0
 */
public interface FriendService {
    PageableDto<UserFriendDto> searchFriends(Pageable pageable, String name, UserVO userVO,
                                             Boolean hasSameCity, Boolean hasMutualFriends);
}
