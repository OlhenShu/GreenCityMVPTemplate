package greencity.service;

import greencity.dto.telegram.TelegramUserDto;

public interface TelegramBotService {
    TelegramUserDto findByChatId(Long chatId);

    void saveTelegramUser(Long chatId, String email);

    boolean isRegister(Long chatId);
}