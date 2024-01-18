package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.user.RecommendFriendDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserFriend;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FriendService}.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */

    @Override
    public PageableDto<UserFriendDto> getUserFriendsByUserId(Long userId, Pageable pageable) {
        validateUserExist(userId);
        Page<User> allUserFriends = userRepo.getAllUserFriendsPage(pageable, userId);
        List<UserFriendDto> userFriendDtoList =
                allUserFriends.stream().map(e -> modelMapper.map(e, UserFriendDto.class))
                        .collect(Collectors.toList());
        return new PageableDto<>(
                userFriendDtoList,
                allUserFriends.getTotalElements(),
                allUserFriends.getPageable().getPageNumber(),
                allUserFriends.getTotalPages()
        );

    public PageableDto<UserFriendDto> findAllUsersFriends(Long userId, Pageable pageable) {
        List<User> friends = userRepo.getAllUserFriends(userId);
        if (friends.isEmpty()) {
            throw new NotFoundException(ErrorMessage.NOT_FOUND_ANY_FRIENDS + userId);
        }
        List<UserFriendDto> friendsDto = friends.stream()
            .map(user -> modelMapper.map(user, UserFriendDto.class))
            .collect(Collectors.toList());

        Page<UserFriendDto> friendDtoPage = new PageImpl<>(friendsDto, pageable, friendsDto.size());
        return new PageableDto<>(
            friendDtoPage.getContent(),
            friendDtoPage.getTotalElements(),
            friendDtoPage.getPageable().getPageNumber(),
            friendDtoPage.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserFriendDto> searchFriends(Pageable pageable, String name, UserVO userVO,
                                                    Boolean hasSameCity, Boolean hasMutualFriends) {
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
        validateUserExist(friendId);
        validateIsNotSameUsers(userId, friendId);
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));
        String status = user.getConnections().stream().filter(c -> Objects.equals(c.getFriend().getId(), friendId))
            .findAny().map(UserFriend::getStatus).orElse(null);
        if (status != null) {
            throw new BadRequestException(String.format(ErrorMessage.USER_ALREADY_HAS_CONNECTION, status));
        }
        notificationService.friendRequestNotification(userId, friendId);
        userRepo.addFriend(userId, friendId);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void acceptFriendRequest(Long userId, Long friendId) {
        validateUserExist(userId);
        validateUserExist(friendId);
        validateIsNotSameUsers(userId, friendId);
        User user = userRepo.findById(friendId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId));
        String status = user.getConnections().stream().filter(c -> Objects.equals(c.getFriend().getId(), userId))
                .findAny().map(UserFriend::getStatus).orElse(null);
        if (!status.equals("REQUEST")) {
            throw new BadRequestException(String.format(ErrorMessage.USER_ALREADY_HAS_CONNECTION, status));
        }
        userRepo.acceptFriendRequest(userId, friendId);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void declineFriendRequest(Long userId, Long friendId) {
        validateUserExist(userId);
        validateUserExist(friendId);
        validateIsNotSameUsers(userId, friendId);
        User user = userRepo.findById(friendId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId));
        String status = user.getConnections().stream().filter(c -> Objects.equals(c.getFriend().getId(), userId))
                .findAny().map(UserFriend::getStatus).orElse(null);
        if (!status.equals("REQUEST")) {
            throw new BadRequestException(String.format(ErrorMessage.USER_ALREADY_HAS_CONNECTION, status));
        }
        userRepo.declineFriendRequest(userId, friendId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUserFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new BadRequestException(ErrorMessage.OWN_USER_ID + friendId);
        }
        if (!userRepo.existsById(friendId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + friendId);
        }
        checkIfFriends(userId, friendId);
        userRepo.deleteUserFriend(userId, friendId);
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<UserFriendDto> getAllFriendsByDifferentParameters(
        Pageable pageable, String name, UserVO userVO, Boolean hasSameCity,
        Double highestPersonalRate, ZonedDateTime dateTimeOfAddingFriend) {
        if (name.isEmpty() || name.length() >= 30) {
            throw new BadRequestException(ErrorMessage.INVALID_LENGTH_OF_QUERY_NAME);
        }
        String city = null;
        if (hasSameCity) {
            city = userVO.getCity();
        }
        if (dateTimeOfAddingFriend == null) {
            dateTimeOfAddingFriend = ZonedDateTime.now().minusWeeks(1);
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<UserFriendDto> listOfUsers = userRepo.findUserFriendDtoByFriendFilterOfUser(
                replaceCriteria(name), city, highestPersonalRate, dateTimeOfAddingFriend, pageable, userVO.getId())
            .map(filterDto -> new UserFriendDto(filterDto.getId(), filterDto.getCity(), filterDto.getName(),
                filterDto.getProfilePicturePath(), filterDto.getRating()));

        return new PageableDto<>(
            listOfUsers.getContent(),
            listOfUsers.getTotalElements(),
            listOfUsers.getPageable().getPageNumber(),
            listOfUsers.getTotalPages());
    }

    private void checkIfFriends(Long userId, Long friendId) {
        if (!userRepo.isFriend(userId, friendId)) {
            throw new NotDeletedException(ErrorMessage.NOT_FOUND_ANY_FRIENDS + friendId);
        }
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
