package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.user.RecommendFriendDto;
import greencity.dto.user.UserVO;
import greencity.repository.UserRepo;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<RecommendFriendDto> getRecommendedFriends(UserVO user, Pageable pageable) {
        var recommendedFriends = userRepo.findAllRecommendedFriends(user.getId(),
            pageable,user.getCity());
        return new PageableDto<>(recommendedFriends.stream().collect(Collectors.toList()),
            recommendedFriends.getNumberOfElements(),
            recommendedFriends.getNumber(),
            recommendedFriends.getTotalPages());
    }
}
