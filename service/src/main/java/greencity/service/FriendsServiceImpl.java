package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.filter.FriendsFilterDto;
import greencity.dto.friends.FriendsDto;
import greencity.repository.UserRepo;
import greencity.repository.options.FriendsFilter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FriendsServiceImpl implements FriendsService {
    private final UserRepo userRepo;

    @Override
    public PageableDto<FriendsDto> searchFriends(Pageable pageable, String searchQuery, Long userId,
                                                 Boolean hasMutualFriends) {
        FriendsFilterDto friendsFilterDto = new FriendsFilterDto(searchQuery, null, hasMutualFriends);

        Page<FriendsDto> listOfUsers = userRepo.findAllFriendsDto(new FriendsFilter(friendsFilterDto), userId,
            pageable);

        return new PageableDto<>(
            listOfUsers.getContent(),
            listOfUsers.getTotalElements(),
            listOfUsers.getPageable().getPageNumber(),
            listOfUsers.getTotalPages());
    }
}
