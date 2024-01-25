package greencity.service;

import greencity.dto.telegram.TelegramUserDto;

public interface TelegramBotService {
    /**
     * Retrieves a Telegram user DTO by their chat ID.
     *
     * @param chatId the chat ID of the Telegram user to be retrieved
     * @return The TelegramUserDto with the specified chat ID, or null if not found.
     */
    TelegramUserDto findByChatId(Long chatId);

    /**
     * Saves a Telegram user with the provided chat ID and phone number.
     * This method creates or updates a Telegram user in the database with the specified chat ID and phone number.
     *
     * @param chatId       the chat ID of the Telegram user to be saved
     * @param phoneNumber  the phone number associated with the Telegram user
     */
    void saveTelegramUser(Long chatId, String phoneNumber);

    /**
     * Checks if a Telegram user is registered based on their chat ID.
     * This method verifies whether a Telegram user with the given chat ID is registered in the system.
     *
     * @param chatId the chat ID of the Telegram user to be checked for registration
     * @return true if the user is registered, false otherwise
     */
    boolean isRegister(Long chatId);

    /**
     * Retrieves all unread notifications for a Telegram user based on their chat ID.
     * This method fetches and returns a string containing all unread notifications for the Telegram user
     *
     * @param chatId the chat ID of the Telegram user for whom unread notifications are being retrieved
     * @return A string containing all unread notifications, or an empty string if none are found.
     */
    String getAllUnreadNotification(Long chatId);

    /**
     * Deletes a Telegram user based on their chat ID.
     * This method removes the Telegram user with the specified chat ID from the system.
     *
     * @param chatId the chat ID of the Telegram user to be deleted
     */
    void deleteTelegramUser(Long chatId);
}