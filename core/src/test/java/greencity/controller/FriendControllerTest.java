package greencity.controller;

import greencity.ModelUtils;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserFriendFilterDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.FriendService;
import greencity.service.UserService;
import java.time.ZonedDateTime;
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
import static org.mockito.Mockito.*;
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
    private final UserVO userVO = ModelUtils.getUserVO();

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

        mockMvc.perform(get(link)
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
    void searchFriendsWithCriteriaNotDefinedTest() throws Exception {
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

        mockMvc.perform(get(link)
                .principal(userVO::getEmail)
                .param("name", name)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk());

        verify(userService).findByEmail(userVO.getEmail());
        verify(friendService).getAllFriendsByDifferentParameters(
            eq(pageable), eq(name), eq(userVO), eq(false), eq(null), eq(null));
    }
}
