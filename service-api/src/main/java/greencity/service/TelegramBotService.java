package greencity.service;

import greencity.dto.telegram.TelegramUserDto;

public interface TelegramBotService {
    void sendMessage(Long chatId, String message);
    TelegramUserDto findByChatId(Long chatId);
    void saveTelegramUser(Long chatId, String email);
    boolean isRegister(Long chatId);
}
