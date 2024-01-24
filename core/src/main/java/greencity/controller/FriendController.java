package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.user.RecommendFriendDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
     * Retrieves a paginated list of user friends for the authenticated user.
     * @param pageable The pagination information for the result set.
     * @param userVO   The authenticated user details.
     * @return A {@link ResponseEntity} containing a {@link PageableDto} of {@link UserFriendDto}.
     */
    @ApiOperation(value = "Searches for current user`s friends.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiPageable
    @GetMapping
    public ResponseEntity<PageableDto<UserFriendDto>> getAllUserFriend(
            @ApiIgnore Pageable pageable,
            @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK).body(
                friendService.getUserFriendsByUserId(userVO.getId(), pageable)
        );
    }

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
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })

    @PatchMapping("/{friendId}/acceptFriend")
    public ResponseEntity<ResponseEntity.BodyBuilder> acceptFriendRequest(
            @PathVariable Long friendId, @ApiIgnore @CurrentUser UserVO userVO
    ) {
        friendService.acceptFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Rejects a friend request from another user.
     * @param friendId The ID of the user who sent the friend request.
     * @param userVO   The authenticated user details.
     * @return A {@link ResponseEntity} representing the result of the operation.
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK),
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

    @ApiPageable
    @GetMapping("/friendRequests")
    public ResponseEntity<PageableDto<UserFriendDto>> getAllFriendRequest(
            @ApiIgnore Pageable pageable, @ApiIgnore @CurrentUser UserVO user
    ) {
        return ResponseEntity.ok(
                friendService.allFriendRequests(user.getId(), pageable)
        );
    }


    /**
     * Retrieves a paginated list of friends' details with specific filtering criteria.
     * @param pageable               Pagination information for the resulting list.
     * @param name                   The criteria for filtering friend names (can be null).
     * @param userVO                 User, which friends are being fetched.
     * @param hasSameCity            Whether to filter friends by the same city as the user.
     * @param highestPersonalRate    The maximum rating allowed for friends in the result set.
     * @param dateTimeOfAddingFriend The date when friends were added, filtering based on this timestamp.
     * @return                       A paginated list of {@link UserFriendDto} containing friend details.
     */
    @ApiOperation(value = "Retrieves a paginated list of friends' details with specific filtering criteria.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiPageable
    @GetMapping("/search")
    public ResponseEntity<PageableDto<UserFriendDto>> getAllFriendsByDifferentParameters(
        @ApiIgnore Pageable pageable,
        @ApiParam(value = "Query to search 1 to 30 characters") @RequestParam String name,
        @RequestParam(required = false, name = "hasSameCity", defaultValue = "false") Boolean hasSameCity,
        @RequestParam(required = false, name = "highestPersonalRate") Double highestPersonalRate,
        @RequestParam(required = false, name = "dateTimeOfAddingFriend")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime dateTimeOfAddingFriend,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK).body(
            friendService.getAllFriendsByDifferentParameters(
                pageable, name, userVO, hasSameCity, highestPersonalRate, dateTimeOfAddingFriend));
    }

    /**
     * Rejects a friend request from a specified user.
     * This endpoint is mapped to the HTTP PATCH method and is accessible at "/{friendId}/rejectRequest".
     *
     * @param friendId The unique identifier of the friend whose request is to be rejected.
     * @param userVO   The current user information obtained from the authentication context.
     * @return         A ResponseEntity with an HTTP status code indicating the success or failure of the operation.
     *
     */
    @ApiOperation(value = "Get current user's friends.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitAssignDto.class),
            @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
            @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
            @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{friendId}/declineFriend")
    public ResponseEntity<ResponseEntity.BodyBuilder> rejectFriendRequest(
            @PathVariable Long friendId, @ApiIgnore @CurrentUser UserVO userVO
    ) {
        friendService.declineFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
