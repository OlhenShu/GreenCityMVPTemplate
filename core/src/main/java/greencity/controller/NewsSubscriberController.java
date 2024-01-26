package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.service.NewsSubscriberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newsSubscriber")
@RequiredArgsConstructor
public class NewsSubscriberController {
    private final NewsSubscriberService newsSubscriberService;

    /**
     * Method for adding new subscriber.
     *
     * @param email     email to send notifications.
     * @return status.
     */
    @ApiOperation(value = "Add new news subscriber.")
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @PostMapping
    public ResponseEntity<ResponseEntity.BodyBuilder> save(@Email @RequestParam String email) {
        newsSubscriberService.save(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
