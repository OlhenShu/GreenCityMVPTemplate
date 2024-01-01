package greencity.controller;

import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.FriendService;
import greencity.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


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
    private UserVO userVO = ModelUtils.getUserVO();

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
    void searchFriendsWithCriteriaNotDefinedTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "test";

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
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(friendService).searchFriends(pageable, name, userVO, false, false);
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
}
