package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.notification.NotificationMarkAsReadDtoRequest;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import greencity.service.NotifiedUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotifiedUserService notifiedUserService;

    /**
     * Marks a notification as read for the authorized user.
     *
     * @param userVO  The authenticated user information.
     * @param request The request containing the notification ID to mark as read.
     * @return ResponseEntity indicating the success of marking the notification as read.
     */
    @ApiOperation(value = "Marks a notification as read for the authorized user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/mark-as-read")
    public ResponseEntity<?> markAsReadNotification(@ApiIgnore @CurrentUser UserVO userVO,
                                                    @RequestBody NotificationMarkAsReadDtoRequest request) {
        notifiedUserService.markAsReadNotification(userVO.getId(), request.getNotificationId());
        return ResponseEntity.ok().build();
    }
}
