package greencity.service;

import greencity.dto.telegram.TelegramUserDto;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotServiceImpl implements TelegramBotService {
    private final UserRepo userRepo;

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
    public void saveTelegramUser(Long chatId, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with current email not found"));
        user.setChatId(chatId);
        userRepo.save(user);
        log.info("User with id {} was register in telegram bot", user.getId());
    }

    @Override
    public boolean isRegister(Long chatId) {
        return userRepo.findUserByChatId(chatId).isPresent();
    }
}