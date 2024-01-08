package greencity.controller;

import static greencity.ModelUtils.getUserVO;
import greencity.converters.UserArgumentResolver;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class FriendsControllerTest {
    private static final String friendsLink = "/friends";
    @Mock
    private FriendService friendService;
    @InjectMocks
    FriendController friendController;
    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;


    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(friendController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
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
                    get(friendsLink +"/recommended")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                        .principal(userVO::getEmail)
                )
                .andExpect(status().isOk());

            verify(userService).findByEmail(userVO.getEmail());
            verify(friendService).getRecommendedFriends(userVO, pageable);
    }
}
