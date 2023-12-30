package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.service.FriendService;
import io.swagger.annotations.ApiParam;
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

    @GetMapping("/not-friends-yet")
    public ResponseEntity<PageableDto<UserFriendDto>> searchFriends(
        @ApiIgnore Pageable pageable,
        @ApiParam(value = "Query to search") @RequestParam String name,
        @RequestParam(required = false, name = "Has same city", defaultValue = "false") Boolean hasSameCity,
        @RequestParam(required = false, name = "Has mutual friends", defaultValue = "false") Boolean hasMutualFriends,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity.status(HttpStatus.OK).body(
            friendService.searchFriends(pageable, name, userVO, hasSameCity, hasMutualFriends));
    }
}
