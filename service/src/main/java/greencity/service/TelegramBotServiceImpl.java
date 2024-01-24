package greencity.service;

import greencity.dto.notification.NotificationsDto;
import greencity.dto.telegram.TelegramUserDto;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotServiceImpl implements TelegramBotService {
    private final UserRepo userRepo;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public TelegramUserDto findByChatId(Long chatId) {
        User user = userRepo.findUserByChatId(chatId)
                .orElseThrow(() -> new NotFoundException("User with current chat id not found"));
        return TelegramUserDto.builder()
                .userId(user.getId())
                .chatId(user.getChatId())
                .build();
    }

    @Override
    @Transactional
    public void saveTelegramUser(Long chatId, String phoneNumber) {
        User user = userRepo.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("User with current phone number not found"));
        user.setChatId(chatId);
        userRepo.save(user);
        log.info("User with id {} was register in telegram bot", user.getId());
    }

    @Override
    public void deleteTelegramUser(Long chatId) {
        User user = userRepo.findUserByChatId(chatId)
                .orElseThrow(() -> new NotFoundException("User with current chat id not found"));
        user.setChatId(null);
        userRepo.save(user);
        log.info("User with id {} was unregistered in telegram bot", user.getId());
    }

    @Override
    public boolean isRegister(Long chatId) {
        return userRepo.findUserByChatId(chatId).isPresent();
    }

    @Override
    public String getAllUnreadNotification(Long chatId) {
        User user = userRepo.findUserByChatId(chatId)
                .orElseThrow(() -> new NotFoundException("User with current chat id not found"));
        List<NotificationsDto> allUnreadNotificationByUserId = notificationService.findAllUnreadNotificationByUserId(user.getId());
        return getUnreadNotification(allUnreadNotificationByUserId);
    }

    private String getUnreadNotification(List<NotificationsDto> notifications) {
        StringJoiner joiner = new StringJoiner("/n");
        for (NotificationsDto notification : notifications) {
            joiner.add("Повідомлення від: " + notification.getUserName()  +
                    "\nТекст: " + notification.getObjectTitle()
                    + "\nКоли: " + notification.getNotificationTime()
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        }
        return joiner.toString();
    }
}