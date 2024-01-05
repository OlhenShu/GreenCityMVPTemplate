package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.util.Map;
import greencity.entity.UserFriend;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;

    @Override
    public PageableDto<UserFriendDto> getAllFriendsByDifferentParameters(
        Pageable pageable, String name, UserVO userVO, Boolean hasSameCity, Boolean hasMutualFriends) {
        validateUserExist(userVO.getId());
        if (name.isEmpty() || name.length() >= 30) {
            throw new BadRequestException(ErrorMessage.INVALID_LENGTH_OF_QUERY_NAME);
        }
        String city = null;
        if (hasSameCity) {
            city = userVO.getCity();
        }
        Page<UserFriendDto> listOfUsers = userRepo.findUserFriendDtoByFriendFilterOfUser(
            replaceCriteria(name), city, pageable, userVO.getId());

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

    private void validateUserExist(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
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
