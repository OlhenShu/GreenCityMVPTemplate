package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.PageableDto;
import greencity.dto.friends.FriendsDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendsService;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;


@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendsController {
    private final FriendsService friendsService;

    /**
     * Forms a list of based on type of the classes initialized in
     * the constructors.
     */
    @GetMapping
    public ResponseEntity<PageableDto<FriendsDto>> searchFriends(
        @ApiIgnore Pageable pageable,
        @ApiParam(value = "Query to search") @RequestParam String searchQuery,
        @RequestParam(required = false, name = "hasMutualFriends") Boolean hasMutualFriends,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK).body(
            friendsService.searchFriends(pageable,
                searchQuery, userVO.getId(), hasMutualFriends));
    }
}
