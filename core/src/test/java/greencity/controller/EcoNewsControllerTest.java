package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EcoNewsService;
import greencity.service.FileService;
import greencity.service.TagsService;
import greencity.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EcoNewsControllerTest {
    private static final String ecoNewsLink = "/econews";
    private static final String uploadImageLink = "/uploadImage";
    private MockMvc mockMvc;
    @InjectMocks
    private EcoNewsController ecoNewsController;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private TagsService tagsService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FileService fileService;

    private Principal principal = getPrincipal();

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(ecoNewsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void getThreeLastEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/newest"))
                .andExpect(status().isOk());

        verify(ecoNewsService).getThreeLastEcoNews();
    }

    @Test
    void uploadImageTest() throws Exception {
        MockMultipartFile image = new MockMultipartFile("data", "filename.txt",
                "text/plain", "some xml".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(ecoNewsLink + uploadImageLink)
                .file(image)).andExpect(status().isCreated());
        verify(ecoNewsService).uploadImage(isNull());
    }

    @Test
    void saveTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("Olivia.Johnson@gmail.com");
        String json = "{\n" +
                "\"title\": \"title\",\n" +
                " \"tags\": [\"news\"],\n" +
                " \"text\": \"content content content\", \n" +
                "\"source\": \"\",\n" +
                " \"image\": null\n" +
                "}";
        MockMultipartFile jsonFile =
                new MockMultipartFile("addEcoNewsDtoRequest", "", "application/json", json.getBytes());

        this.mockMvc.perform(multipart(ecoNewsLink)
                        .file(jsonFile)
                        .principal(principal)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddEcoNewsDtoRequest addEcoNewsDtoRequest = mapper.readValue(json, AddEcoNewsDtoRequest.class);

        verify(ecoNewsService)
                .saveEcoNews(eq(addEcoNewsDtoRequest), isNull(), eq("Olivia.Johnson@gmail.com"));
    }

    @Test
    void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(ecoNewsLink)
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEcoNewsById() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/{id}", 1))
                .andExpect(status().isOk());

        verify(ecoNewsService).findDtoByIdAndLanguage(1L, "en");
    }

    @Test
    void getEcoNewsByUserTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(ecoNewsLink + "/byUser")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(ecoNewsService).getAllPublishedNewsByUser(userVO);
    }

    @Test
    void findAllTest() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "?page=1"))
                .andExpect(status().isOk());

        verify(ecoNewsService).findGenericAll(pageable);
    }

    @Test
    @SneakyThrows
    void getEcoNewsByUserByPage() {
        int pageNumber = 1;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsLink + "/byUserPage?page=1&size=2"))
                .andExpect(status().isOk());

        verify(ecoNewsService).findAllByUser(null, pageable);
    }

    @Test
    @SneakyThrows
    void getEcoNewsByUserByPageWithException() {
        // Test data
        int pageNumber = 1;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(ecoNewsService.findAllByUser(any(), any())).thenThrow(new IllegalArgumentException("Invalid argument"));

        mockMvc.perform(get(ecoNewsLink + "/byUserPage?page=1&size=2"))
                .andExpect(status().isUnauthorized());

        verify(ecoNewsService).findAllByUser(null, pageable);
    }

    @Test
    void deleteTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(ecoNewsLink + "/{econewsId}", 1)
                        .principal(principal))
                .andExpect(status().isOk());

        verify(ecoNewsService).delete(1L, userVO);
    }

    @Test
    void getEcoNewsTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<String> tags = Collections.singletonList("eco");

        mockMvc.perform(get("/econews/tags?page=5&tags=eco"))
                .andExpect(status().isOk());

        verify(ecoNewsService).find(pageable, tags);
    }

    @Test
    void getEcoNewsEmptyTagsListTest() throws Exception {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get("/econews/tags?page=5"))
                .andExpect(status().isOk());

        verify(ecoNewsService).findGenericAll(pageable);
    }

    @Test
    void getThreeRecommendedEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/recommended?openedEcoNewsId=" + 1L))
                .andExpect(status().isOk());

        verify(ecoNewsService).getThreeRecommendedEcoNews(1L);
    }

    @Test
    void findAllEcoNewsTagsTest() throws Exception {
        String language = "en";
        mockMvc.perform(get(ecoNewsLink + "/tags/all?lang=" + language))
                .andExpect(status().isOk());

        verify(tagsService).findAllEcoNewsTags(language);
    }

    @Test
    void likeTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(ecoNewsLink + "/like?id=1")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(ecoNewsService).like(userVO, 1L);
    }

    @Test
    void dislikeTest() throws Exception {
        // given
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        // when
        mockMvc.perform(post(ecoNewsLink + "/dislike?id=1")
                        .principal(principal))
                .andExpect(status().isOk());
        // then
        verify(ecoNewsService).dislike(userVO, 1L);

    }

    @Test
    void countLikesForEcoNewsTest() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/countLikes/{econewsId}", 1L))
                .andExpect(status().isOk());

        verify(ecoNewsService).countLikesForEcoNews(1L);
    }

    @Test
    void checkNewsIsLikedByUserTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(get(ecoNewsLink + "/isLikedByUser?econewsId=1")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(ecoNewsService).checkNewsIsLikedByUser(1L, userVO);
    }

    @Test
    void findAmountOfPublishedNews() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/count")
                        .param("userId", "1"))
                .andExpect(status().isOk());

        verify(ecoNewsService).getAmountOfPublishedNewsByUserId(1L);
    }

    @Test
    void getContentAndSourceForEcoNewsById() throws Exception {
        mockMvc.perform(get(ecoNewsLink + "/contentAndSourceForEcoNews/{id}", 1L))
                .andExpect(status().isOk());

        verify(ecoNewsService).getContentAndSourceForEcoNewsById(1L);

    }

    @Test
    void getContentAndSourceForEcoNewsByIdNot_Found_Request() throws Exception {

        Mockito.when(ecoNewsService.getContentAndSourceForEcoNewsById(1L)).thenThrow(NotFoundException.class);

        mockMvc.perform(get(ecoNewsLink + "/contentAndSourceForEcoNews/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(ecoNewsService).getContentAndSourceForEcoNewsById(1L);
    }

    @Test
    void testDeleteImage() {
        // Test data
        String testImagePath = "path/to/image.jpg";
        // Perform the test
        ecoNewsController.deleteImage(testImagePath);
        // Verify the expected behavior
        verify(fileService, times(1)).delete(testImagePath);
    }

    @Test
    void testUploadImages() throws Exception {
        // Prepare test data
        MultipartFile[] testImages = {
                new MockMultipartFile("image1.jpg", new byte[0]),
                new MockMultipartFile("image2.jpg", new byte[0]),
                // Add more mock files as needed
        };

        String[] expectedResponse = {"image1.jpg", "image2.jpg"};

        // Mock the behavior of the ecoNewsService.uploadImages method
        when(ecoNewsService.uploadImages(testImages)).thenReturn(expectedResponse);

        // Perform the test
        ResponseEntity<String[]> response = ecoNewsController.uploadImages(testImages);

        // Verify the expected behavior
        verify(ecoNewsService, times(1)).uploadImages(testImages);
        Assertions.assertArrayEquals(expectedResponse, response.getBody());
        assert (response.getStatusCode() == HttpStatus.CREATED);
    }


}
