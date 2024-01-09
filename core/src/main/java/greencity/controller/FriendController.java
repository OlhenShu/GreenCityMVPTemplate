package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.user.RecommendFriendDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/friends")
@AllArgsConstructor
@Validated
@Slf4j
public class FriendController {
    private final FriendService friendService;

    /**
     * Searches for friends based on specific criteria.
     *
     * @param pageable         Pagination information for the result.
     * @param name             The query used for searching friends.
     * @param hasSameCity      Flag indicating whether to include only friends from the same city.
     * @param hasMutualFriends Flag indicating whether to include friends only with mutual connections.
     * @return A {@link PageableDto} containing a paginated list of {@link UserFriendDto} as search results.
     */
    @ApiOperation(value = "Searches for friends based on name, city, mutual friends.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/not-friends-yet")
    public ResponseEntity<PageableDto<UserFriendDto>> searchFriends(
        @ApiIgnore Pageable pageable,
        @ApiParam(value = "Query to search 1 to 30 characters") @RequestParam String name,
        @RequestParam(required = false, name = "hasSameCity", defaultValue = "false") Boolean hasSameCity,
        @RequestParam(required = false, name = "hasMutualFriends", defaultValue = "false") Boolean hasMutualFriends,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK).body(
            friendService.searchFriends(pageable, name, userVO, hasSameCity, hasMutualFriends));
    }

    /**
     * Adds a new friend relationship between the current user and the specified friend.
     *
     * @param friendId The unique identifier of the friend to be added.
     * @param userVO   The details of the current user.
     * @return A ResponseEntity indicating the success of the friend addition operation.
     */
    @ApiOperation(value = "Add new friend relationship between the current user and the specified friend.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PostMapping("/{friendId}")
    public ResponseEntity<ResponseEntity.BodyBuilder> addFriend(
        @PathVariable Long friendId, @ApiIgnore @CurrentUser UserVO userVO) {
        friendService.addFriend(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Deletes a new friend relationship between the current user and the specified friend.
     *
     * @param friendId The unique identifier of the friend to be added.
     * @param userVO   The details of the current user.
     * @return A ResponseEntity indicating the success of the friend connection delete operation.
     */
    @ApiOperation(value = "Delete/unfriend user's friend")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ResponseEntity.BodyBuilder> deleteUserFriend(
            @ApiParam("friendId of current User cannot be empty.") @PathVariable Long friendId,
            @ApiIgnore @CurrentUser UserVO userVO) {
        friendService.deleteUserFriend(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Retrieves a paginated list of recommended friends for the authenticated user.
     *
     * @param pageable Pageable object defining the page size, page number
     * @param user     UserVO object representing the authenticated user obtained from the current session.
     * @return ResponseEntity containing a {@link PageableDto}
     *         with {@link RecommendFriendDto} objects representing recommended friends.
     */
    @ApiOperation(value = "Get recommended friends")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/recommended")
    @ApiPageable
    public ResponseEntity<PageableDto<RecommendFriendDto>> getRecommendedFriends(
        Pageable pageable, @ApiIgnore @CurrentUser UserVO user) {
        return ResponseEntity.ok(
            friendService.getRecommendedFriends(user,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())));
    }
}
