package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserFriendFilterDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import greencity.service.UserService;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static greencity.ModelUtils.getUserVO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class FriendControllerTest {
    private static final String link = "/friends";
    @InjectMocks
    private FriendController friendController;
    @Mock
    private FriendService friendService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    private MockMvc mockMvc;
    private final UserVO userVO = getUserVO();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(friendController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    void searchFriendsWithAllCriteriaTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "test";
        Boolean hasSameCity = true;
        Boolean hasMutualFriends = true;

        List<UserFriendDto> userNotYetFriends = new ArrayList<>();
        userNotYetFriends.add(new UserFriendDto(1L, "Lviv", 1L, "Friend1",
            "testProfilePicturePath", 1D));
        userNotYetFriends.add(new UserFriendDto(2L, "Odesa", 0L, "Friend2",
            "testProfilePicturePath", 1D));
        var userFriendDtoPage = new PageImpl<>(userNotYetFriends, PageRequest.of(0, 10), 2L);
        var pageableDto = new PageableDto<>(
            userFriendDtoPage.getContent(),
            userFriendDtoPage.getTotalElements(),
            userFriendDtoPage.getPageable().getPageNumber(),
            userFriendDtoPage.getTotalPages());

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(friendService.searchFriends(any(Pageable.class), anyString(), any(UserVO.class), anyBoolean(), anyBoolean()))
            .thenReturn(pageableDto);

        mockMvc.perform(get(link + "/not-friends-yet")
                .principal(userVO::getEmail)
                .param("name", name)
                .param("hasSameCity", String.valueOf(hasSameCity))
                .param("hasMutualFriends", String.valueOf(hasMutualFriends))
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(friendService).searchFriends(pageable, name, userVO, hasSameCity, hasMutualFriends);
    }

    @Test
    void addFriendTest() throws Exception {
        Long friendId = 1L;

        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(link + "/{friendId}", friendId)
                .principal(userVO::getEmail))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(friendService).addFriend(userVO.getId(), friendId);
    }

    @Test
    public void testDeleteUserFriend() {
        UserVO userVO = new UserVO();
        userVO.setId(1L);

        ResponseEntity<ResponseEntity.BodyBuilder> responseEntity =
                friendController.deleteUserFriend(2L, userVO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(friendService).deleteUserFriend(eq(1L), eq(2L));
    }

    @Test
    void getAllFriendsByDifferentParametersWithAllCriteriaTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "test";
        Boolean hasSameCity = true;
        Double highestPersonalRate = 210D;
        ZonedDateTime dateTimeOfAddingFriend = ZonedDateTime.parse("2017-10-01T01:00+02:00");

        List<UserFriendDto> friends = new ArrayList<>();
        friends.add(new UserFriendFilterDto(2L, "Lviv", "Friend2",
            "picturePath", 21D, 1L));
        friends.add(new UserFriendFilterDto(3L, "Odesa", "Friend3",
            "picturePath", 25D, 0L));

        var userFriendDtoPage = new PageImpl<>(friends, PageRequest.of(0, 10), 2L);
        var pageableDto = new PageableDto<>(
            userFriendDtoPage.getContent(),
            userFriendDtoPage.getTotalElements(),
            userFriendDtoPage.getPageable().getPageNumber(),
            userFriendDtoPage.getTotalPages());

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(friendService.getAllFriendsByDifferentParameters(any(Pageable.class), anyString(), any(UserVO.class),
            anyBoolean(), anyDouble(), any(ZonedDateTime.class)))
            .thenReturn(pageableDto);

        mockMvc.perform(get(link + "/search")
                .principal(userVO::getEmail)
                .param("name", name)
                .param("hasSameCity", String.valueOf(hasSameCity))
                .param("highestPersonalRate", String.valueOf(highestPersonalRate))
                .param("dateTimeOfAddingFriend", "2017-10-01T01:00+02:00")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(friendService).getAllFriendsByDifferentParameters(
            pageable, name, userVO, hasSameCity, highestPersonalRate, dateTimeOfAddingFriend);
    }

    @Test
    void getAllFriendsByDifferentParametersWithCriteriaNotDefinedTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "test";

        List<UserFriendDto> friends = new ArrayList<>();
        friends.add(new UserFriendFilterDto(2L, "Lviv", "Friend2",
            "picturePath", 21D, 1L));
        friends.add(new UserFriendFilterDto(3L, "Odesa", "Friend3",
            "picturePath", 25D, 0L));

        var userFriendDtoPage = new PageImpl<>(friends, PageRequest.of(0, 10), 2L);
        var pageableDto = new PageableDto<>(
            userFriendDtoPage.getContent(),
            userFriendDtoPage.getTotalElements(),
            userFriendDtoPage.getPageable().getPageNumber(),
            userFriendDtoPage.getTotalPages());

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(friendService.getAllFriendsByDifferentParameters(any(Pageable.class), anyString(), any(UserVO.class),
            anyBoolean(), any(), any())).thenReturn(pageableDto);

        mockMvc.perform(get(link + "/search")
                .principal(userVO::getEmail)
                .param("name", name)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(friendService).getAllFriendsByDifferentParameters(
            eq(pageable), eq(name), eq(userVO), eq(false), eq(null), eq(null));
    }

    @Test
    void getRecommendedFriends() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc
            .perform(
                get(link +"/recommended")
                    .param("page", String.valueOf(pageNumber))
                    .param("size", String.valueOf(pageSize))
                    .principal(userVO::getEmail)
            )
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(friendService).getRecommendedFriends(userVO, pageable);
    }

    @Test
    public void getUsersFriend() throws Exception {
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

        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(friendService.findAllUsersFriends(userVO.getId(), pageable))
                .thenReturn(userFriendPageableDto);
        mockMvc.perform(MockMvcRequestBuilders.get(link)
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                        .principal(userVO::getEmail))
                .andExpect(status().isOk());
        verify(friendService).findAllUsersFriends(userVO.getId(), pageable);
    }
}