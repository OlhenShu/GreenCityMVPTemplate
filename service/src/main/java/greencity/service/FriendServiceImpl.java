package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
