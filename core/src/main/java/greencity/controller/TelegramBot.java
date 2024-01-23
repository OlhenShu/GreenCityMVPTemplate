package greencity.controller;

import greencity.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bot")
public class TelegramBot {
    private final TelegramBotService botService;

    @GetMapping("/{chatId}/{msg}")
    @ResponseStatus(HttpStatus.OK)
    void sendMessage(@PathVariable("chatId") Long chatId, @PathVariable("msg") String message) {
        botService.sendMessage(chatId,message);
    }
}