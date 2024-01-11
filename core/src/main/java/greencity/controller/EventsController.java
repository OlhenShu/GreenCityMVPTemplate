package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.event.AddEventDto;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.service.EventService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventsController {

    private final EventService eventService;

    /**
     * Method for creating an event.
     *
     * @param addEventDto {@link AddEventDto} The DTO containing information for create event.
     * @param images      Optional array of images related to the event.
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
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<EventDto> save(
            @ApiParam(value = SwaggerExampleModel.ADD_EVENT, required = true)
            @RequestPart AddEventDto addEventDto,
            @ApiIgnore @CurrentUser UserVO userVO,
            @RequestPart(required = false) @Nullable List<MultipartFile> images) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(eventService.save(addEventDto, userVO, images));
    }
}
