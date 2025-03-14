package greencity.controller;

import greencity.annotations.ApiPageableWithoutSort;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventDtoForSubscribedUser;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationSourceType;
import greencity.service.EventService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Nullable;
import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {
    private final EventService eventService;

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param eventId The unique identifier of the event.
     * @return A ResponseEntity with the event DTO if found (HTTP 200),
     *          or an empty body with an appropriate HTTP status if not found.
     */
    @ApiOperation(value = "Get the event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getById(eventId));
    }

    /**
     * Handles the HTTP PUT request to update an event.
     *
     * @param eventDto  The data to update the event.
     * @param principal Represents the currently authenticated user.
     * @param images    An array of image files associated with the event (optional).
     * @return A ResponseEntity with the updated EventDto and HTTP status.
     */
    @ApiOperation(value = "Update event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK, response = EventDto.class),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PutMapping(value = "/update",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<EventDto> update(
            @ApiParam(required = true, value = SwaggerExampleModel.UPDATE_EVENT) @RequestPart UpdateEventDto eventDto,
            @ApiIgnore Principal principal,
            @RequestPart(required = false) @Nullable MultipartFile[] images) {
        return ResponseEntity.status(HttpStatus.OK).body(
                eventService.update(eventDto, principal.getName(), images));
    }

    /**
     * Method for creating an event.
     *
     * @param addEventDtoRequest {@link AddEventDtoRequest} The DTO containing information for create event.
     * @param images             Optional array of images related to the event.
     * @return {@link EventDto}.
     * @author Vlada Proskurina.
     */
    @ApiOperation(value = "Create new event")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HttpStatuses.CREATED),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/create",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<EventDto> save(
            @ApiParam(value = SwaggerExampleModel.ADD_EVENT, required = true)
            @RequestPart AddEventDtoRequest addEventDtoRequest,
            @ApiIgnore @CurrentUser UserVO userVO,
            @RequestPart(required = false) @Nullable MultipartFile[] images) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.save(addEventDtoRequest, userVO, images));
    }

    /**
     * Method for deleting an event.
     *
     * @author Bogdan Veremienko.
     */
    @ApiOperation(value = "Delete event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<Object> delete(@PathVariable Long eventId, @ApiIgnore Principal principal) {
        eventService.delete(eventId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * The method for getting count of events.
     *
     * @return count of events.
     * @author Nikita Malov
     */
    @ApiOperation(value = "Find count of events")
    @GetMapping("/count")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    public ResponseEntity<Long> findAmountOfEvents(@RequestParam Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAmountOfEvents(userId));
    }

    /**
     * Likes an event.
     *
     * @param id     The ID of the event to be liked.
     * @param userVO The UserVO who is liking the event.
     */
    @ApiOperation(value = "Like event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/like")
    public void likeEvent(@RequestParam("id") Long id, @ApiIgnore @CurrentUser UserVO userVO) {
        eventService.like(id, userVO, NotificationSourceType.EVENT_LIKED);
    }

    /**
     * Retrieves all subscribed events for the current user.
     *
     * @param userVO The UserVO representing the current user.
     * @return ResponseEntity containing a list of EventDtoForSubscribedUser for subscribed events.
     */
    @ApiOperation(value = "Get all subscribed events for current user")
    @GetMapping("/getAllSubscribers/{eventId}")
    public ResponseEntity<List<EventDtoForSubscribedUser>> getAllSubscribedEvents(
            @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.ok(eventService.getAllSubscribedEvents(userVO.getId()));
    }

    /**
     * Method for getting pages of events.
     *
     * @return a page of {@link EventDto} instance.
     * @author Max Bohonko, Olena Sotnik.
     */
    @ApiOperation(value = "Get all events")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @ApiPageableWithoutSort
    @GetMapping
    public ResponseEntity<PageableAdvancedDto<EventDto>> getAllEvents(
            @ApiIgnore Pageable pageable, @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(eventService.getAll(pageable, principal));
    }
}
