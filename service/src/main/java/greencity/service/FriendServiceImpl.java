package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FriendService}.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
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
}
