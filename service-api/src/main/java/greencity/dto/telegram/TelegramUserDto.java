package greencity.dto.telegram;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TelegramUserDto {
    private Long chatId;
    private Long userId;
}