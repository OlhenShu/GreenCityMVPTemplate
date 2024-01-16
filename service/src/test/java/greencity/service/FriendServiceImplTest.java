package greencity.service;

import greencity.ModelUtils;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserFriend;
import greencity.entity.UserFriendPK;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private FriendServiceImpl friendService;

    private final UserVO userVO = ModelUtils.getUserVO();

    @Test
    void testGetUserFriendsByUserId() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<User> userFriendsList = new ArrayList<>();
        userFriendsList.add(ModelUtils.getUser());
        Page<User> userFriendsPage = new PageImpl<>(userFriendsList, pageable, userFriendsList.size());
        when(userRepo.getAllUserFriendsPage(pageable, userId)).thenReturn(userFriendsPage);
        when(userRepo.existsById(anyLong())).thenReturn(true);

        when(modelMapper.map(Mockito.any(User.class), Mockito.eq(UserFriendDto.class))).thenReturn(new UserFriendDto());

        friendService.getUserFriendsByUserId(userId, pageable);
        Mockito.verify(userRepo, Mockito.times(1)).getAllUserFriendsPage(pageable, userId);
        Mockito.verify(modelMapper, Mockito.times(userFriendsList.size())).map(Mockito.any(User.class), Mockito.eq(UserFriendDto.class));
    }

    @Test
    void searchFriendsTest() {
        User user = ModelUtils.getUser();
        User friend1 = ModelUtils.getUser();
        friend1.setId(2L);
        User friend2 = ModelUtils.getUser();
        friend2.setId(4L);
        Set<UserFriend> userFriends = new HashSet<>();
        UserFriend userFriend1 = UserFriend.builder()
            .primaryKey(new UserFriendPK(user.getId(), friend1.getId()))
            .user(user)
            .friend(friend1)
            .status("REQUEST")
            .build();
        UserFriend userFriend2 = UserFriend.builder()
            .primaryKey(new UserFriendPK(user.getId(), friend2.getId()))
            .user(user)
            .friend(friend2)
            .status("FRIEND")
            .build();
        userFriends.add(userFriend1);
        userFriends.add(userFriend2);
        user.setConnections(userFriends);

        List<UserFriendDto> userNotYetFriends = new ArrayList<>();
        userNotYetFriends.add(new UserFriendDto(friend1.getId(), friend1.getCity(), 1L, friend1.getName(),
            friend1.getProfilePicturePath(), friend1.getRating()));
        userNotYetFriends.add(new UserFriendDto(3L, "Odesa", 0L, "Friend3",
            friend1.getProfilePicturePath(), 25D));

        Page<UserFriendDto> userFriendDtos = new PageImpl<>(userNotYetFriends, PageRequest.of(0, 10), 2L);

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findAllUserFriendDtoByFriendFilter(anyString(), any(), anyBoolean(), any(Pageable.class), anyLong()))
            .thenReturn(userFriendDtos);

        var response = friendService.searchFriends(
            PageRequest.of(0, 10), "t_e%s\\t'", userVO, false, false);
        var responsePage = response.getPage();

        verify(userRepo).findAllUserFriendDtoByFriendFilter(replaceCriteria("t_e%s\\t'"), null,
            false, PageRequest.of(0, 10), userVO.getId());
        assertEquals(2, response.getTotalElements());
        assertEquals(userNotYetFriends.size(), responsePage.size());
        assertEquals(userNotYetFriends.get(0), responsePage.get(0));
        assertEquals("NOT_FRIEND", responsePage.get(1).getFriendStatus());
    }

    @Test
    void searchFriendsWithHasSameCityFlagTest() {
        userVO.setCity("Lviv");
        User user = ModelUtils.getUser();
        User friend1 = ModelUtils.getUser();
        friend1.setId(2L);
        User friend2 = ModelUtils.getUser();
        friend2.setId(4L);
        Set<UserFriend> userFriends = new HashSet<>();
        UserFriend userFriend1 = UserFriend.builder()
            .primaryKey(new UserFriendPK(user.getId(), friend1.getId()))
            .user(user)
            .friend(friend1)
            .status("REQUEST")
            .build();
        UserFriend userFriend2 = UserFriend.builder()
            .primaryKey(new UserFriendPK(user.getId(), friend2.getId()))
            .user(user)
            .friend(friend2)
            .status("FRIEND")
            .build();
        userFriends.add(userFriend1);
        userFriends.add(userFriend2);
        user.setConnections(userFriends);

        List<UserFriendDto> userNotYetFriends = new ArrayList<>();
        userNotYetFriends.add(new UserFriendDto(friend1.getId(), friend1.getCity(), 1L, friend1.getName(),
            friend1.getProfilePicturePath(), friend1.getRating()));

        Page<UserFriendDto> userFriendDtos = new PageImpl<>(userNotYetFriends, PageRequest.of(0, 10), 1L);

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findAllUserFriendDtoByFriendFilter(anyString(), any(), anyBoolean(), any(Pageable.class), anyLong()))
            .thenReturn(userFriendDtos);

        var response = friendService.searchFriends(
            PageRequest.of(0, 10), "t_e%s\\t'", userVO, true, false);
        var responsePage = response.getPage();

        verify(userRepo).findAllUserFriendDtoByFriendFilter(replaceCriteria("t_e%s\\t'"), "Lviv",
            false, PageRequest.of(0, 10), userVO.getId());
        assertEquals(1, response.getTotalElements());
        assertEquals(userNotYetFriends.size(), responsePage.size());
        assertEquals(userNotYetFriends.get(0), responsePage.get(0));
    }

    @Test
    void searchFriendsWhenNameIsOutOfBoundsThrowsBadRequestExceptionTest() {
        when(userRepo.existsById(anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> friendService.searchFriends(
            PageRequest.of(0, 10), "", userVO, false, false));
        assertThrows(BadRequestException.class, () -> friendService.searchFriends(
            PageRequest.of(0, 10), "1111111111111111111111111111111", userVO, false, false));
    }

    @Test
    void searchFriendsWhenUserNotFoundThrowsNotFoundExceptionTest() {
        when(userRepo.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> friendService.searchFriends(
                PageRequest.of(0, 10), "t_e%s\\t'", userVO, false, false));
    }

    @Test
    void addFriendTest() {
        User user = ModelUtils.getUser();
        User friend = ModelUtils.getUser();
        friend.setId(3L);
        Set<UserFriend> userFriends = new HashSet<>();
        UserFriend userFriend = UserFriend.builder()
            .primaryKey(new UserFriendPK(user.getId(), friend.getId()))
            .user(user)
            .friend(friend)
            .status("REQUEST")
            .build();
        userFriends.add(userFriend);
        user.setConnections(userFriends);

        when(userRepo.existsById(anyLong())).thenReturn(true);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepo).addFriend(anyLong(), anyLong());
        doNothing().when(notificationService).friendRequestNotification(anyLong(), anyLong());
        friendService.addFriend(1L, 2L);

        verify(userRepo).addFriend(1L, 2L);
    }

    @Test
    void addFriendWhenUserNotFoundThrowsNotFoundExceptionTest() {
        when(userRepo.existsById(anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> friendService.addFriend(1L, 2L));
    }

    @Test
    void addFriendWhenUserAlreadyHasConnectionThrowsBadRequestExceptionTest() {
        User user = ModelUtils.getUser();
        User friend2 = ModelUtils.getUser();
        friend2.setId(2L);
        User friend3 = ModelUtils.getUser();
        friend3.setId(3L);
        Set<UserFriend> userFriends = new HashSet<>();

        userFriends.add(UserFriend.builder()
            .primaryKey(new UserFriendPK(user.getId(), friend2.getId()))
            .user(user)
            .friend(friend2)
            .status("REQUEST")
            .build());
        userFriends.add(UserFriend.builder()
            .primaryKey(new UserFriendPK(user.getId(), friend3.getId()))
            .user(user)
            .friend(friend3)
            .status("FRIEND")
            .build());
        user.setConnections(userFriends);

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.existsById(anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> friendService.addFriend(1L, 2L));
        assertThrows(BadRequestException.class, () -> friendService.addFriend(1L, 3L));
    }

    @Test
    void addFriendWhenUserTryingToConnectToHimselfThrowsBadRequestExceptionTest() {
        when(userRepo.existsById(anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> friendService.addFriend(1L, 1L));
    }


    @Test
    void testAcceptFriendRequest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepo).acceptFriendRequest(userId, friendId);

        friendService.acceptFriendRequest(userId, friendId);

        verify(userRepo, times(1)).acceptFriendRequest(userId, friendId);
    }

    @Test
    void testDeclineFriendRequest() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepo).declineFriendRequest(userId, friendId);

        friendService.declineFriendRequest(userId, friendId);

        verify(userRepo, times(1)).declineFriendRequest(userId, friendId);
    }

    @Test
    void testAcceptFriendRequestWithNonExistingUser () {
        Long userId = 1L;
        Long friendId = 2L;

        assertThrows(NotFoundException.class, () ->
                friendService.acceptFriendRequest(userId, friendId));
    }

    @Test
    void testRejectFriendRequestWithNonExistingUser () {
        Long userId = 1L;
        Long friendId = 2L;

        assertThrows(NotFoundException.class, () ->
                friendService.declineFriendRequest(userId, friendId));
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
