package greencity.mapping;

import greencity.dto.user.UserFriendDto;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;

public class UserFriendDtoMapper extends AbstractConverter<User, UserFriendDto> {
    @Override
    protected UserFriendDto convert(User user) {
        return UserFriendDto.builder()
                .id(user.getId())
                .city(user.getCity())
                .email(user.getEmail())
                .friendStatus(null)
                .id(user.getId())
                .mutualFriends(null)
                .name(user.getName())
                .profilePicturePath(user.getProfilePicturePath())
                .rating(user.getRating())
                .build();
    }
}
