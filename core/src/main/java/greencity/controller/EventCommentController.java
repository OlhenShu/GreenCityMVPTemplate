package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/events/comments")
public class EventCommentController {
    private final EventCommentService eventCommentService;

    @ApiOperation(value = "Add comment.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = AddEventCommentDtoRequest.class),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @PostMapping("/{eventId}")
    public ResponseEntity<AddEventCommentDtoResponse> save(@PathVariable Long eventId,
                                                           @Valid @RequestBody AddEventCommentDtoRequest request,
                                                           @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventCommentService.save(eventId, request, user));
    }

    @GetMapping("{id}")
    public ResponseEntity<EventCommentDto> getEventCommentById(@PathVariable Long id,
                                                               @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventCommentService.getEventCommentById(id, userVO));
    }

    @ApiOperation(value = "Get all active comments.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("/active")
    @ApiPageableWithoutSort
    public ResponseEntity<PageableDto<EventCommentDto>> getAllActiveComments(@ApiIgnore Pageable pageable,
                                                                             Long eventId,
                                                                             @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventCommentService.getAllActiveComments(pageable, user, eventId));
    }

    @ApiOperation(value = "Update comment.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PatchMapping()
    public void update(Long id, @RequestParam @NotBlank String commentText, @ApiIgnore @CurrentUser UserVO user) {
        eventCommentService.update(commentText, id, user);
    }

    @ApiOperation(value = "Mark comment as deleted.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{eventCommentId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventCommentId, @ApiIgnore @CurrentUser UserVO user) {
        eventCommentService.delete(eventCommentId, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}