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
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBotConfig extends TelegramLongPollingBot {
    private final String botUsername;
    private final TelegramBotService botService;

    /**
     * Constructor for configuring a Telegram bot instance.
     * This constructor is responsible for setting up and configuring a Telegram bot with the provided bot token,
     * bot name, and an instance of TelegramBotService. It extends the TelegramLongPollingBot class, and upon
     * instantiation, registers the bot for handling incoming updates.
     *
     * @param botToken    the token associated with the Telegram bot
     * @param botName     the username of the Telegram bot
     * @param botService  an instance of TelegramBotService for handling bot-related operations
     */
    public TelegramBotConfig(@Value("${telegram.bot.token}") String botToken,
                             @Value("${telegram.bot.name}") String botName,
                             TelegramBotService botService
    ) {
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
                    sendMessage(chatId, "Ви ще не прив'язували цей номер до аккаунту GreenCity. "
                            + "Прив'язати: /subscribe");
                }
            } else if (messageText.equals("/unsubscribe")) {
                if (botService.isRegister(chatId)) {
                    botService.deleteTelegramUser(chatId);
                    sendMessage(chatId, "Ваш профіль успішно відв'язано, "
                            + "переадресація повідомлень в Telegram вимкнена.");
                } else {
                    sendMessage(chatId, "Ви ще не прив'язували цей номер до аккаунту GreenCity. "
                            + "Прив'язати: /subscribe");
                }
            } else if (messageText.equals("/help")) {
                sendHelpCommand(chatId);
            } else {
                sendMessage(update.getMessage().getChatId(),
                        "Не знайома мені команда. Повний перелік: "
                                + "\n1/ /start, /subscribe, /notification, /unsubscribe, /help");
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

    /**
     * Sends a notification to a Telegram user with the specified chat ID.
     * This method constructs a Telegram message with the given chat ID and message content,
     *
     * @param chatId   the chat ID of the Telegram user to whom the notification will be sent
     * @param message  the content of the notification message
     */
    public void sendNotification(Long chatId, String message) {
        var msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(message);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.info("Error");
        }
    }

    private void register() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e); //TODO: Create a custom telegram exception instead of this one
        }
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
        String text = "Привіт! За допомогою бота можна отримувати "
                + "нові повідомлення за вашим профілем на GreenCity. "
                + "\n\nСписок команд, які можна використовувати зараз: "
                + "\n/start, /subscribe, /notification, /unsubscribe, /help";

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

    private void sendHelpCommand(long chatId) {
        String text = "Список команд, які можна використовувати зараз: "
                + "\n/start, /subscribe, /notification, /unsubscribe, /help";

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

    /**
     * Sends a notification to a Telegram user with the specified chat ID using the Telegram API.
     * This method constructs and sends a HTTP GET request to the Telegram API's "sendMessage" endpoint
     * with the provided chat ID and message content. The response status code and body are printed to the console.
     *
     * @param chatId   the chat ID of the Telegram user to whom the notification will be sent
     * @param message  the content of the notification message
     */
    public void sendNotificationViaTelegramApi(Long chatId, String message) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{botToken}/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", message);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(builder.build("bot" + this.getBotToken()))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}