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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        User user = userRepo.findById(userVO.getId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userVO.getId()));
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void addFriend(Long userId, Long friendId) {
        validateUserExist(userId);
        validateUserExist(friendId);
        validateIsNotSameUsers(userId, friendId);
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        String status = user.getConnections().stream().filter(c -> Objects.equals(c.getFriend().getId(), friendId))
            .findAny().map(UserFriend::getStatus).orElse(null);
        if (status != null) {
            throw new BadRequestException(String.format(ErrorMessage.USER_ALREADY_HAS_CONNECTION, status));
        }
        userRepo.addFriend(userId, friendId);
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Long userId, Long friendId) {
        validateUserExist(userId);
        validateUserExist(friendId);
        validateIsNotSameUsers(userId, friendId);
        userRepo.acceptFriendRequest(userId, friendId);
    }
    @Override
    @Transactional
    public void declineFriendRequest(Long userId, Long friendId) {
        validateUserExist(userId);
        validateUserExist(friendId);
        validateIsNotSameUsers(userId, friendId);
        userRepo.declineFriendRequest(userId, friendId);
    }


    private void validateUserExist(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
    }

    private void validateIsNotSameUsers(Long userId, Long friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new BadRequestException(ErrorMessage.OWN_USER_ID);
        }
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
