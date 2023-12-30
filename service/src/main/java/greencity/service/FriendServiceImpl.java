package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserFriend;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserFriendDto> searchFriends(Pageable pageable, String name, UserVO userVO,
                                                    Boolean hasSameCity, Boolean hasMutualFriends) {
        validateUserExist(userVO.getId());
        if (name.isEmpty() || name.length() >= 30) {
            throw new BadRequestException(ErrorMessage.INVALID_LENGTH_OF_QUERY_NAME);
        }
        String city = null;
        if (hasSameCity) {
            city = userVO.getCity();
        }
        Page<UserFriendDto> listOfUsers = userRepo.findAllUserFriendDtoByFriendFilter(
            replaceCriteria(name), city, hasMutualFriends, pageable, userVO.getId());

        User user = userRepo.findById(userVO.getId()).get();
        Map<Long, String> connectionStatuses = user.getConnections().stream()
            .collect(Collectors.toMap(
                connection -> connection.getFriend().getId(),
                UserFriend::getStatus));
        listOfUsers.forEach(userFriendDto -> userFriendDto.setFriendStatus(
            Optional.ofNullable(connectionStatuses.get(userFriendDto.getId())).orElse("NOT_FRIEND")));

        return new PageableDto<>(
            listOfUsers.getContent(),
            listOfUsers.getTotalElements(),
            listOfUsers.getPageable().getPageNumber(),
            listOfUsers.getTotalPages());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        validateUserExist(userId);
        validateUserExist(friendId);
        User user = userRepo.findById(userId).get();
        if (user.getConnections().stream().anyMatch(c -> Objects.equals(c.getFriend().getId(), friendId))) {
            throw new BadRequestException(ErrorMessage.USER_ALREADY_HAS_CONNECTION);
        }
        userRepo.addFriend(userId, friendId, LocalDateTime.now());
    }

    private void validateUserExist(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
    }

    /**
     * Returns a String criteria for search.
     *
     * @param criteria String for search.
     * @return String criteria not be {@literal null}.
     */
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
