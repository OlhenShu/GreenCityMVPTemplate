package greencity.service;

import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserVO;
import greencity.dto.PageableDto;
import greencity.dto.user.RecommendFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.repository.UserRepo;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class FriendsServiceImplTest {
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private FriendServiceImpl friendService;

    @Test
    void getRecommendedFriends(){
        UserVO userVO = getUserVO();
        User user = getUser();
        PageRequest pageable = PageRequest.of(0, 5);
        RecommendFriendDto recommendFriendDto =
            new RecommendFriendDto(user.getId(), user.getCity(), user.getName(), user.getProfilePicturePath()
                , user.getRating(), 0L, 0L);

        when(userRepo.findAllRecommendedFriends(userVO.getId(), pageable,userVO.getCity())).
            thenReturn(new PageImpl<>(List.of(recommendFriendDto),pageable,1));

        var actual = friendService.getRecommendedFriends(userVO,pageable);
        verify(userRepo).findAllRecommendedFriends(userVO.getId(), pageable,userVO.getCity());
        var expected = new PageableDto<>(List.of(recommendFriendDto), 1, 0, 1);
        assertEquals(expected,actual);
    }
}
