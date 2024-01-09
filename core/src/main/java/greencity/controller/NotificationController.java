package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.user.UserVO;
import greencity.service.NotificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
     * Method that returns page of {@link NotificationDtoResponse} received by user with specified id.
     *
     * @param user  user id.
     * @param page  {@link Pageable} object.
     * @return      page of {@link NotificationDtoResponse}.
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
}
