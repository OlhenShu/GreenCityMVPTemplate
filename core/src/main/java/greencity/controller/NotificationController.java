package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import greencity.service.NotifiedUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final NotifiedUserService notifiedUserService;

    /**
     * Retrieves the three latest notifications for the authenticated user.
     *
     * @param userVO The UserVO object representing the authenticated user.
     * @return ResponseEntity containing a list of {@link ShortNotificationDtoResponse} objects
     * representing the three latest notifications.
     */
    @ApiOperation(value = "Get three last notifications.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK)
    })
    @GetMapping("/latest")
    public ResponseEntity<List<ShortNotificationDtoResponse>> getTheLatestThreeNotifications(@CurrentUser @ApiIgnore
                                                                                             UserVO userVO) {
        return ResponseEntity.ok(notificationService.getTheLatestThreeNotifications(userVO.getId()));
    }

    /**
     * Marks a notification with the given ID as read for the authorized user.
     *
     * @param userVO The authenticated user information.
     * @param id     The ID of the notification to mark as read.
     * @return ResponseEntity indicating the success of marking the notification as read.
     */
    @ApiOperation(value = "Marks a notification as read for the authorized user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PutMapping("/mark-as-read/{id}")
    public ResponseEntity<Void> markAsReadNotification(@ApiIgnore @CurrentUser UserVO userVO,
                                                       @PathVariable("id") Long id) {
        notifiedUserService.markAsReadNotification(userVO.getId(), id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Creates a new notification based on the provided request data and returns the corresponding notification DTO.
     *
     * @param userVO  the authenticated user information
     * @param request the request body containing data to create the notification
     * @return ResponseEntity containing the DTO representing the created notification {@link NotificationDtoResponse}
     */
    @ApiOperation(value = "Create new notification")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/create")
    public ResponseEntity<NotificationDtoResponse> createNewNotification(@ApiIgnore @CurrentUser UserVO userVO,
                                                                         @RequestBody NewNotificationDtoRequest request) {
        return ResponseEntity.ok(notificationService.createNewNotification(userVO.getId(), request));
    }
}