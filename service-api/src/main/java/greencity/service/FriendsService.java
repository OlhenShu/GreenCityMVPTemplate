package greencity.service;

import greencity.dto.PageableDto;

import greencity.dto.friends.FriendsDto;

import org.springframework.data.domain.Pageable;

/**
 * The class implements . Constructor takes a {@code DTO}
 * class the type of which determines the further creation of a new
 * .
 *
 * @author Rostyslav Khasanov
 */
public interface FriendsService {
    /**
     * Forms a list of based on type of the classes initialized in
     * the constructors.
     */
    PageableDto<FriendsDto> searchFriends(
        Pageable pageable, String searchQuery, Long userId, Boolean hasMutualFriends);
}
