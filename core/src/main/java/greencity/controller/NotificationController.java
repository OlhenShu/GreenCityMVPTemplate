package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * Retrieves the three latest eco news notifications for the authenticated user.
     *
     * @param userVO The UserVO object representing the authenticated user.
     * @return ResponseEntity containing a list of {@link NotificationDto} objects
     *         representing the three latest eco news notifications.
     */
    @ApiOperation(value = "Get three last eco news.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("/latest")
    public ResponseEntity<List<NotificationDto>> getTheLatestThreeNotifications(@CurrentUser @ApiIgnore UserVO userVO) {
        return ResponseEntity.ok(notificationService.getTheLatestThreeNotifications(userVO.getId()));
    }
}
