package greencity.controller;

import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendControllerTest {

    @Mock
    private FriendService friendService;

    @InjectMocks
    private FriendController friendController;

    @Test
    public void testDeleteUserFriend() {
        UserVO userVO = new UserVO();
        userVO.setId(1L);

        ResponseEntity<ResponseEntity.BodyBuilder> responseEntity =
                friendController.deleteUserFriend(2L, userVO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        verify(friendService, times(1)).deleteUserFriend(eq(1L), eq(2L));
    }
}