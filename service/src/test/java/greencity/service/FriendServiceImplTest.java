package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.user.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.repository.UserRepo;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @InjectMocks
    private FriendServiceImpl friendService;
    @Mock
    private ModelMapper modelMapper;

    private UserVO userVO = ModelUtils.getUserVO();
@Test
    public void findAllUsersFriends (){
        User user = new User();
        user.setId(1L);
        UserFriendDto userFriendDto = new UserFriendDto();

        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<UserFriendDto> friendDtoList = Collections.singletonList(new UserFriendDto());
        Page<UserFriendDto> friendDtoPage = new PageImpl<>(friendDtoList, pageable, friendDtoList.size());
        PageableDto<UserFriendDto> userFriendPageableDto = new PageableDto<>(
                friendDtoPage.getContent(),
                friendDtoPage.getTotalElements(),
                friendDtoPage.getPageable().getPageNumber(),
                friendDtoPage.getTotalPages());


        List<User> users = new ArrayList<>(List.of(user));

        when(userRepo.getAllUserFriends(userVO.getId())).thenReturn(users);
        when(modelMapper.map(user,  UserFriendDto.class)).thenReturn(userFriendDto);
        assertEquals(userFriendPageableDto, friendService.findAllUsersFriends(userVO.getId(),pageable));
        verify(userRepo, times(1)).getAllUserFriends(userVO.getId());
    }
}
