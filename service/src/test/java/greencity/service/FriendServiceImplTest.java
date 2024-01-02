package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private FriendServiceImpl friendService;

    @Test
    void deleteUserFriend_SuccessfulDeletion() {
        Long userId = 1L;
        Long friendId = 2L;

        when(userRepo.isFriend(userId, friendId)).thenReturn(true);
        when(userRepo.existsById(friendId)).thenReturn(true);

        friendService.deleteUserFriend(userId, friendId);

        verify(userRepo, times(1)).deleteUserFriend(userId, friendId);
    }

    @Test
    public void deleteUserFriend_self() {
        Long userId = 1L;

        assertThrows(BadRequestException.class, () -> friendService.deleteUserFriend(userId, userId));
    }

    @Test
    public void deleteUserFriend_notFriends() {
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
}