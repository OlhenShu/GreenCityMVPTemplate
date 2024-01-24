package greencity.service;

import greencity.dto.telegram.TelegramUserDto;

public interface TelegramBotService {
    TelegramUserDto findByChatId(Long chatId);

    void saveTelegramUser(Long chatId, String phoneNumber);

    boolean isRegister(Long chatId);

    String getAllUnreadNotification(Long chatId);

    void deleteTelegramUser(Long chatId);
}