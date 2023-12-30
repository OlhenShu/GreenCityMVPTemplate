package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.filter.FriendFilterDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.repository.UserRepo;
import greencity.repository.options.FriendFilter;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;
    @Override
    public PageableDto<UserFriendDto> searchFriends(Pageable pageable, String name, UserVO userVO,
                                                    Boolean hasSameCity, Boolean hasMutualFriends) {
        String city = null;
        if (hasSameCity) {
            city = userVO.getCity();
        }
        Page<UserFriendDto> listOfUsers = userRepo.findAllUserFriendDtoByFriendFilter(
            replaceCriteria(name), city, hasMutualFriends, pageable, userVO.getId());

        return new PageableDto<>(
            listOfUsers.getContent(),
            listOfUsers.getTotalElements(),
            listOfUsers.getPageable().getPageNumber(),
            listOfUsers.getTotalPages());
    }

    private String replaceCriteria(String criteria) {
        criteria = Optional.ofNullable(criteria).orElseGet(() -> "");
        criteria = criteria.trim();
        criteria = criteria.replace("_", "\\_");
        criteria = criteria.replace("%", "\\%");
        criteria = criteria.replace("\\", "\\\\");
        criteria = criteria.replace("'", "\\'");
        criteria = "%" + criteria + "%";
        return criteria;
    }
}
