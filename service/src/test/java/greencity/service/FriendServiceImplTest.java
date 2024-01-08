package greencity.service;

import greencity.ModelUtils;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserFriendFilterDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.time.ZonedDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private FriendServiceImpl friendService;

    private final UserVO userVO = ModelUtils.getUserVO();

    @Test
    void getAllFriendsByDifferentParametersTest() {
        List<UserFriendFilterDto> friends = new ArrayList<>();
        friends.add(new UserFriendFilterDto(2L, "Lviv", "Friend2",
            "picturePath", 21D, 1L));
        friends.add(new UserFriendFilterDto(3L, "Odesa", "Friend3",
            "picturePath", 25D, 0L));
        Page<UserFriendFilterDto> userFriendDtos = new PageImpl<>(friends, PageRequest.of(0, 10), 2L);
        ZonedDateTime dateTimeOfAddingFriend = ZonedDateTime.now().minusWeeks(1);

        when(userRepo.findUserFriendDtoByFriendFilterOfUser(anyString(), any(), anyDouble(), any(
            ZonedDateTime.class), any(Pageable.class), anyLong()))
            .thenReturn(userFriendDtos);

        var response = friendService.getAllFriendsByDifferentParameters(
            PageRequest.of(0, 10),
            "t_e%s\\t'", userVO, false, 210D, dateTimeOfAddingFriend);
        var responsePage = response.getPage();

        verify(userRepo).findUserFriendDtoByFriendFilterOfUser(replaceCriteria("t_e%s\\t'"), null,
            210D, dateTimeOfAddingFriend, PageRequest.of(0, 10), userVO.getId());
        assertEquals(2, response.getTotalElements());
        assertEquals(friends.size(), responsePage.size());
        UserFriendDto userFriendDto0 = new UserFriendDto(friends.get(0).getId(), friends.get(0).getCity(),
            friends.get(0).getName(), friends.get(0).getProfilePicturePath(), friends.get(0).getRating());
        UserFriendDto userFriendDto1 = new UserFriendDto(friends.get(1).getId(), friends.get(1).getCity(),
            friends.get(1).getName(), friends.get(1).getProfilePicturePath(), friends.get(1).getRating());
        assertEquals(userFriendDto0, responsePage.get(0));
        assertEquals(userFriendDto1, responsePage.get(1));
    }

    @Test
    void getAllFriendsByDifferentParametersWithHasSameCityFlagTest() {
        userVO.setCity("Lviv");
        List<UserFriendFilterDto> friends = new ArrayList<>();
        friends.add(new UserFriendFilterDto(2L, "Lviv", "Friend2",
            "picturePath", 21D, 1L));
        Page<UserFriendFilterDto> userFriendDtos = new PageImpl<>(friends, PageRequest.of(0, 10), 1L);
        ZonedDateTime dateTimeOfAddingFriend = ZonedDateTime.now().minusWeeks(1);

        when(userRepo.findUserFriendDtoByFriendFilterOfUser(anyString(), any(), anyDouble(), any(
            ZonedDateTime.class), any(Pageable.class), anyLong()))
            .thenReturn(userFriendDtos);

        var response = friendService.getAllFriendsByDifferentParameters(
            PageRequest.of(0, 10),
            "t_e%s\\t'", userVO, true, 210D, dateTimeOfAddingFriend);
        var responsePage = response.getPage();

        verify(userRepo).findUserFriendDtoByFriendFilterOfUser(replaceCriteria("t_e%s\\t'"), userVO.getCity(),
            210D, dateTimeOfAddingFriend, PageRequest.of(0, 10), userVO.getId());
        assertEquals(1, response.getTotalElements());
        assertEquals(friends.size(), responsePage.size());
        UserFriendDto userFriendDto0 = new UserFriendDto(friends.get(0).getId(), friends.get(0).getCity(),
            friends.get(0).getName(), friends.get(0).getProfilePicturePath(), friends.get(0).getRating());
        assertEquals(userFriendDto0, responsePage.get(0));
    }

    @Test
    void getAllFriendsByDifferentParametersWithNullDateTimeOfAddingFriendTest() {
        userVO.setCity("Lviv");
        List<UserFriendFilterDto> friends = new ArrayList<>();
        friends.add(new UserFriendFilterDto(2L, "Lviv", "Friend2",
            "picturePath", 21D, 1L));
        Page<UserFriendFilterDto> userFriendDtos = new PageImpl<>(friends, PageRequest.of(0, 10), 1L);

        when(userRepo.findUserFriendDtoByFriendFilterOfUser(anyString(), any(), anyDouble(), any(
            ZonedDateTime.class), any(Pageable.class), anyLong()))
            .thenReturn(userFriendDtos);

        var response = friendService.getAllFriendsByDifferentParameters(
            PageRequest.of(0, 10),
            "t_e%s\\t'", userVO, true, 210D, null);
        var responsePage = response.getPage();

        verify(userRepo).findUserFriendDtoByFriendFilterOfUser(eq(replaceCriteria("t_e%s\\t'")), eq(userVO.getCity()),
            eq(210D), any(ZonedDateTime.class), eq(PageRequest.of(0, 10)), eq(userVO.getId()));
        assertEquals(1, response.getTotalElements());
        assertEquals(friends.size(), responsePage.size());
        UserFriendDto userFriendDto0 = new UserFriendDto(friends.get(0).getId(), friends.get(0).getCity(),
            friends.get(0).getName(), friends.get(0).getProfilePicturePath(), friends.get(0).getRating());
        assertEquals(userFriendDto0, responsePage.get(0));
    }

    @Test
    void getAllFriendsByDifferentParametersWhenNameIsOutOfBoundsThrowsBadRequestExceptionTest() {
        assertThrows(BadRequestException.class, () -> friendService.getAllFriendsByDifferentParameters(
            PageRequest.of(0, 10), "",
            userVO, false, 210D, ZonedDateTime.now()));
        assertThrows(BadRequestException.class, () -> friendService.getAllFriendsByDifferentParameters(
            PageRequest.of(0, 10), "1111111111111111111111111111111",
            userVO, false, 210D, ZonedDateTime.now()));
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
