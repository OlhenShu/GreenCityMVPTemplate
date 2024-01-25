package greencity.service;

import greencity.ModelUtils;
import greencity.dto.telegram.TelegramUserDto;
import greencity.entity.User;
import greencity.repository.NotificationRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private NotificationRepo notificationRepo;
    @InjectMocks
    private TelegramBotServiceImpl telegramBotService;

    @Test
    void findByChatId() {
        Long chatId = 123456L;
        User mockUser = ModelUtils.getUser();
        mockUser.setId(1L);
        mockUser.setChatId(chatId);

        when(userRepo.findUserByChatId(chatId)).thenReturn(Optional.of(mockUser));
        TelegramUserDto result = telegramBotService.findByChatId(chatId);

        assertEquals(mockUser.getId(), result.getUserId());
        assertEquals(mockUser.getChatId(), result.getChatId());

        verify(userRepo, times(1)).findUserByChatId(chatId);
    }

    @Test
    void saveTelegramUser() {
        Long chatId = 123456L;
        String phoneNumber = "123456789";

        User existingUser = ModelUtils.getUser();
        existingUser.setId(1L);
        existingUser.setPhoneNumber(phoneNumber);

        when(userRepo.findUserByPhoneNumber(phoneNumber)).thenReturn(Optional.of(existingUser));

        telegramBotService.saveTelegramUser(chatId, phoneNumber);

        assertEquals(chatId, existingUser.getChatId());
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    void deleteTelegramUser() {
        Long chatId = 123456L;

        User existingUser = ModelUtils.getUser();
        existingUser.setId(1L);
        existingUser.setChatId(chatId);

        when(userRepo.findUserByChatId(chatId)).thenReturn(Optional.of(existingUser));

        telegramBotService.deleteTelegramUser(chatId);

        assertNull(existingUser.getChatId());
        verify(userRepo, times(1)).save(existingUser);
    }

    @Test
    void isRegister() {
        Long chatId = 123456L;
        when(userRepo.findUserByChatId(chatId)).thenReturn(Optional.of(new User()));
        boolean result = telegramBotService.isRegister(chatId);

        assertTrue(result);
        verify(userRepo, times(1)).findUserByChatId(chatId);
    }

    @Test
    public void testIsRegisterUserNotPresent() {
        Long chatId = 123456L;
        when(userRepo.findUserByChatId(chatId)).thenReturn(Optional.empty());
        boolean result = telegramBotService.isRegister(chatId);

        assertFalse(result);
        verify(userRepo, times(1)).findUserByChatId(chatId);
    }

//    @Test
//    void getAllUnreadNotification() {
//        Long chatId = 123456L;
//
//        User existingUser = new User();
//        existingUser.setId(1L);
//        existingUser.setChatId(chatId);
//
//        when(userRepo.findUserByChatId(anyLong())).thenReturn(Optional.of(existingUser));
//
//        // Mock the behavior of notificationRepo
//        List<NotificationDtoResponse> notificationDtoResponseList = List.of(ModelUtils.getNotificationDtoResponse());
//        Page<NotificationDtoResponse> friendRequestDtoPage = new PageImpl<>(notificationDtoResponseList, PageRequest.of(0, 3), notificationDtoResponseList.size());
//        when(notificationRepo.findAllReceivedNotificationDtoByUserId(eq(existingUser.getId()), any())).thenReturn(friendRequestDtoPage);
//
//        when(telegramBotService.getAllUnreadNotification(anyLong())).thenReturn("Unread notifications content");
//
//
//        // Act
//        String result = telegramBotService.getAllUnreadNotification(chatId);
//
//        // Assert and Verify
//        assertEquals("Unread notifications content", result);
//        verify(userRepo, times(1)).findUserByChatId(chatId);
//        verify(notificationRepo, times(1)).findAllReceivedNotificationDtoByUserId(existingUser.getId(), PageRequest.of(0, 3));
//        verify(telegramBotService, times(1)).getAllUnreadNotification(chatId);
//    }
}