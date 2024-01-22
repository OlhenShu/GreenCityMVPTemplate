package greencity.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
public class TelegramBotConfig extends TelegramLongPollingBot {
    private String botUsername;
    public TelegramBotConfig(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.name}") String botName) {
        super(botToken);
        this.botUsername = botName;
        register();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Ви відправили: " + update.getMessage().getText())
                    .build();
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.warn(e.getMessage());
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
}
