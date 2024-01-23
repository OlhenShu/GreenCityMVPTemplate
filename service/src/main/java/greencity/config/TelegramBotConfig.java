package greencity.config;

import greencity.dto.telegram.TelegramUserDto;
import greencity.service.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class TelegramBotConfig extends TelegramLongPollingBot {
    private String botUsername;
    private final TelegramBotService botService;
    public TelegramBotConfig(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName,
            TelegramBotService botService) {
        super(botToken);
        this.botUsername = botName;
        this.botService = botService;
        register();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText())
            return;

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (botService.isRegister(chatId)) {
                //register user block
            }
            else {
                sendMessage(chatId, "Please enter your email");
                botService.saveTelegramUser(chatId, messageText);
                sendMessage(chatId, "Thanks for registration, now yor register in " + getBotUsername());
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    private void register() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e); //TODO: Create a custom telegram exception instead of this one
        }
    }

    private void helpMessage(long chatId) {
        sendMessage(chatId, "/start for registration in bot");
    }

    private void sendMessage(long chatId, String message) {
        var msg = new SendMessage();
        msg.setText(message);
        msg.setChatId(chatId);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.info("Error");
        }
    }
}