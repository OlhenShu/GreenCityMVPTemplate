package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.user.RecommendFriendDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserFriend;
import greencity.entity.UserFriendPK;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import java.time.ZonedDateTime;
import java.util.*;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotDeletedException;
import greencity.dto.user.UserFriendFilterDto;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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

import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserVO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepo userRepo;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private FriendServiceImpl friendService;

    private final UserVO userVO = getUserVO();

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

//    @Test
//    void searchFriendsTest() {
//        User user = getUser();
//        User friend1 = getUser();
//        friend1.setId(2L);
//        User friend2 = getUser();
//        friend2.setId(4L);
//        Set<UserFriend> userFriends = new HashSet<>();
//        UserFriend userFriend1 = UserFriend.builder()
//            .primaryKey(new UserFriendPK(user.getId(), friend1.getId()))
//            .user(user)
//            .friend(friend1)
//            .status("REQUEST")
//            .build();
//        UserFriend userFriend2 = UserFriend.builder()
//            .primaryKey(new UserFriendPK(user.getId(), friend2.getId()))
//            .user(user)
//            .friend(friend2)
//            .status("FRIEND")
//            .build();
//        userFriends.add(userFriend1);
//        userFriends.add(userFriend2);
//        user.setConnections(userFriends);
//
//        List<UserFriendDto> userNotYetFriends = new ArrayList<>();
//        userNotYetFriends.add(new UserFriendDto(friend1.getId(), friend1.getCity(), 1L, friend1.getName(),
//            friend1.getProfilePicturePath(), friend1.getRating()));
//        userNotYetFriends.add(new UserFriendDto(3L, "Odesa", 0L, "Friend3",
//            friend1.getProfilePicturePath(), 25D));
//
//        Page<UserFriendDto> userFriendDtos = new PageImpl<>(userNotYetFriends, PageRequest.of(0, 10), 2L);
//
//        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
//        when(userRepo.existsById(anyLong())).thenReturn(true);
//        when(userRepo.findAllUserFriendDtoByFriendFilter(anyString(), any(), anyBoolean(), any(Pageable.class), anyLong()))
//            .thenReturn(userFriendDtos);
//
//        var response = friendService.searchFriends(
//            PageRequest.of(0, 10), "t_e%s\\t'", userVO, false, false);
//        var responsePage = response.getPage();
//
//        verify(userRepo).findAllUserFriendDtoByFriendFilter(replaceCriteria("t_e%s\\t'"), null,
//            false, PageRequest.of(0, 10), userVO.getId());
//        assertEquals(2, response.getTotalElements());
//        assertEquals(userNotYetFriends.size(), responsePage.size());
//        assertEquals(userNotYetFriends.get(0), responsePage.get(0));
//        assertEquals("NOT_FRIEND", responsePage.get(1).getFriendStatus());
//    }

    @Test
    void searchFriendsWithHasSameCityFlagTest() {
        userVO.setCity("Lviv");
        User user = getUser();
        User friend1 = getUser();
        friend1.setId(2L);
        User friend2 = getUser();
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
        assertThrows(BadRequestException.class, () -> friendService.searchFriends(
            PageRequest.of(0, 10), "", userVO, false, false));
        assertThrows(BadRequestException.class, () -> friendService.searchFriends(
            PageRequest.of(0, 10), "1111111111111111111111111111111", userVO, false, false));
    }

    @Test
    void addFriendTest() {
        User user = getUser();
        User friend = getUser();
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
        User user = getUser();
        User friend2 = getUser();
        friend2.setId(2L);
        User friend3 = getUser();
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

        User user = ModelUtils.getUser();

        UserFriend userFriend = new UserFriend();
        userFriend.setFriend(user);
        userFriend.setStatus("REQUEST");

        Set<UserFriend> userFriends = new HashSet<>();
        userFriends.add(userFriend);

        user.setConnections(userFriends);

        doNothing().when(userRepo).acceptFriendRequest(userId, friendId);

        assertDoesNotThrow(() -> userRepo.acceptFriendRequest(userId, friendId));


        verify(userRepo, times(1)).acceptFriendRequest(userId, friendId);
    }

    @Test
    void testDeclineFriendRequest() {
        Long userId = 1L;
        Long friendId = 2L;

        User user = ModelUtils.getUser();

        UserFriend userFriend = new UserFriend();
        userFriend.setFriend(user);
        userFriend.setStatus("REQUEST");

        Set<UserFriend> userFriends = new HashSet<>();
        userFriends.add(userFriend);

        user.setConnections(userFriends);

        doNothing().when(userRepo).declineFriendRequest(userId, friendId);

        assertDoesNotThrow(() -> userRepo.declineFriendRequest(userId, friendId));


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

    void deleteUserFriend_SuccessfulDeletion() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.isFriend(userId, friendId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);

        friendService.deleteUserFriend(userId, friendId);

        verify(userRepo, times(1)).deleteUserFriend(userId, friendId);
    }

    @Test
    void deleteUserFriend_self() {
        Long userId = 1L;

        assertThrows(BadRequestException.class, () -> friendService.deleteUserFriend(userId, userId));
    }

    @Test
    void deleteUserFriend_notFriends() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(friendId)).thenReturn(true);
        when(userRepo.isFriend(userId, friendId)).thenReturn(false);

        assertThrows(NotDeletedException.class, () -> friendService.deleteUserFriend(userId, friendId));
    }

    @Test
    void deleteUserFriend_UserNotFoundException() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.existsById(friendId)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> friendService.deleteUserFriend(userId, friendId),
                ErrorMessage.USER_NOT_FOUND_BY_ID + friendId);

        verify(userRepo, never()).deleteUserFriend(userId, friendId);
    }

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

    @Test
    void getRecommendedFriends(){
        UserVO userVO = getUserVO();
        User user = getUser();
        PageRequest pageable = PageRequest.of(0, 5);
        RecommendFriendDto recommendFriendDto =
            new RecommendFriendDto(user.getId(), user.getCity(), user.getName(), user.getProfilePicturePath()
                , user.getRating(), 0L, 0L);

        when(userRepo.findAllRecommendedFriends(userVO.getId(), pageable,userVO.getCity())).
            thenReturn(new PageImpl<>(List.of(recommendFriendDto),pageable,1));

        var actual = friendService.getRecommendedFriends(userVO,pageable);
        verify(userRepo).findAllRecommendedFriends(userVO.getId(), pageable,userVO.getCity());
        var expected = new PageableDto<>(List.of(recommendFriendDto), 1, 0, 1);
        assertEquals(expected,actual);
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

    @Test
    void findAllUsersFriends() {
        User user = new User();
        user.setId(1L);
        UserFriendDto userFriendDto = new UserFriendDto();

        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<UserFriendDto> friendDtoList = Collections.singletonList(new UserFriendDto());
        Page<UserFriendDto> friendDtoPage = new PageImpl<>(friendDtoList, pageable, friendDtoList.size());
        PageableDto<UserFriendDto> userFriendPageableDto = new PageableDto<>(
                friendDtoPage.getContent(),
                friendDtoPage.getTotalElements(),
                friendDtoPage.getPageable().getPageNumber(),
                friendDtoPage.getTotalPages());
        List<User> users = new ArrayList<>(List.of(user));

        when(userRepo.getAllUserFriends(userVO.getId())).thenReturn(users);
        when(modelMapper.map(user, UserFriendDto.class)).thenReturn(userFriendDto);
        assertEquals(userFriendPageableDto, friendService.findAllUsersFriends(userVO.getId(), pageable));
        verify(userRepo, times(1)).getAllUserFriends(userVO.getId());
    }

    @Test
    void findAllUsersFriendsByUserWithoutFriends() {
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(userRepo.getAllUserFriends(userVO.getId())).thenReturn(new ArrayList<>());
        assertThrows(NotFoundException.class, () -> friendService.findAllUsersFriends(userVO.getId(), pageable));
        verify(userRepo, times(1)).getAllUserFriends(userVO.getId());
    }
}
