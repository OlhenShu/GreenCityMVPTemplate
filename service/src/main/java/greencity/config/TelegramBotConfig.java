package greencity.config;

import greencity.service.TelegramBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.ws.rs.core.UriBuilder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendGreeting(chatId);
            } else if (messageText.equals("/subscribe")) {
                if (botService.isRegister(chatId)) {
                    sendMessage(chatId, "Ви вже зареєстровані. Очікуйте на нові нотифікації");
                } else {
                    askUserToSendContact(chatId);
                }
            } else if (messageText.equals("/notification")) {
                if (botService.isRegister(chatId)) {
                    sendMessage(chatId, botService.getAllUnreadNotification(chatId));
                } else {
                    sendMessage(chatId, "Ви ще не прив'язували цей номер до аккаунту GreenCity. " +
                            "Прив'язати: /subscribe");
                }
            } else if (messageText.equals("/unsubscribe")) {
                if (botService.isRegister(chatId)) {
                    botService.deleteTelegramUser(chatId);
                    sendMessage(chatId, "Ваш профіль успішно відв'язано, " +
                            "переадресація повідомлень в Telegram вимкнена.");
                } else {
                    sendMessage(chatId, "Ви ще не прив'язували цей номер до аккаунту GreenCity. " +
                            "Прив'язати: /subscribe");
                }
            }
            else {
                sendMessage(update.getMessage().getChatId(),
                        "Не знайома мені команда. Повний перелік: " +
                                "\n/start, /subscribe, /notification, /unsubscribe");
            }
        } else {
            if (update.getMessage().hasContact()) {
                sendMessage(update.getMessage().getChatId(), "Ваш телеграм успішно прив'язаний.");
                botService.saveTelegramUser(
                        update.getMessage().getChatId(), update.getMessage().getContact().getPhoneNumber()
                );
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

    private void sendGreeting(long chatId) {
        String text = "Привіт! \uD83D\uDC7D \n\nЗа допомогою бота можна отримувати " +
                "нові повідомлення за вашим профілем на GreenCity. \n\nСписок команд, які можна використовувати зараз: " +
                "\n/start, /subscribe, /notification, /unsubscribe";

        SendMessage message = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.info("Error");
        }
    }

    private void askUserToSendContact(Long chatId) {
        KeyboardButton contactButton = new KeyboardButton("Надати номер телефону");
        contactButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(contactButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Натисніть кнопку нижче, щоб поділитись контактом: ");
        sendMessage.setReplyMarkup(markup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendNotificationViaTelegramApi(Long chatId) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", "friend request");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + this.getBotToken()))
                .timeout(Duration.ofSeconds(5))
                .build();
    }
}