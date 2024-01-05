package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     *
     *
     * @param pageable         Pagination information for the result.
     * @param name             The query used for searching friends.
     * @param hasSameCity      Flag indicating whether to include only friends from the same city.
     * @param hasMutualFriends Flag indicating whether to include friends only with mutual connections.
     * @return A {@link PageableDto} containing a paginated list of {@link UserFriendDto} as search results.
     */
    @ApiOperation(value = "")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @ApiPageable
    @GetMapping()
    public ResponseEntity<PageableDto<UserFriendDto>> getAllFriendsByDifferentParameters(
        @ApiIgnore Pageable pageable,
        @ApiParam(value = "Query to search 1 to 30 characters") @RequestParam String name,
        @RequestParam(required = false, name = "hasSameCity", defaultValue = "false") Boolean hasSameCity,
        @RequestParam(required = false, name = "hasMutualFriends", defaultValue = "false") Boolean hasMutualFriends,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK).body(
            friendService.getAllFriendsByDifferentParameters(pageable, name, userVO, hasSameCity, hasMutualFriends));
    }
}
