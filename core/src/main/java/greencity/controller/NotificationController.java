package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.notification.NewNotificationDtoRequest;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.ShortNotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
     * Marks the latest unread notifications as read for the authenticated user.
     *
     * @param userVO The authenticated user's value object.
     * @return ResponseEntity with status 200 if successful, 401 if unauthorized, or 404 if not found.
     */
    @ApiOperation(value = "Mark as read latest unread notifications")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping("/mark-as-read/")
    public ResponseEntity<Void> markAsReadLatestNotification(@ApiIgnore @CurrentUser UserVO userVO) {
        notificationService.readLatestNotification(userVO.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method that returns page of {@link NotificationDtoResponse} received by user with specified id.
     *
     * @param user user id.
     * @param page {@link Pageable} object.
     * @return page of {@link NotificationDtoResponse}.
     */
    @ApiOperation(value = "Find page of notifications by authorised user.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping
    @ApiPageable
    public ResponseEntity<PageableDto<NotificationDtoResponse>> findAll(
            @ApiIgnore @CurrentUser UserVO user, @ApiIgnore Pageable page) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.findAllByUser(user.getId(), page));
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
        notificationService.markAsReadNotification(userVO.getId(), id);
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

    @GetMapping("/friend-requests")
    public ResponseEntity<PageableDto<NotificationDtoResponse>> findAllFriendRequestsByUserId(@ApiIgnore @CurrentUser UserVO userVO,
                                                                                 @ApiIgnore Pageable page) {
        return ResponseEntity.ok(notificationService.findAllFriendRequestsByUserId(userVO.getId(), page));

    }
}