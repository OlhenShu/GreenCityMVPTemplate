package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/friends")
@AllArgsConstructor
@Validated
@Slf4j
public class FriendController {
    private final FriendService friendService;

    /**
     * Retrieves a paginated list of recommended friends for the specified user.
     *
     * @param pageable object specifying pagination information
     * @param user     the user object containing information about the current authenticated user
     * @return ResponseEntity containing a {@link PageableDto} with {@link UserFriendDto} objects representing recommended friends
     */
    @ApiOperation(value = "Get recommended friends")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/recommended")
    @ApiPageable
    public ResponseEntity<PageableDto<UserFriendDto>> getRecommendedFriends(@ApiIgnore Pageable pageable,
                                                                            @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.ok(friendService.getRecommendedFriends(user, pageable));
    }
}
