package greencity;

import com.google.maps.model.*;
import greencity.annotations.RatingCalculationEnum;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.*;
import greencity.dto.econewscomment.*;
import greencity.dto.event.*;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.habit.*;
import greencity.dto.habitfact.*;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.language.LanguageVO;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.ratingstatistics.RatingStatisticsVO;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.shoppinglistitem.*;
import greencity.dto.tag.*;
import greencity.dto.user.*;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.*;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.localization.ShoppingListItemTranslation;
import greencity.entity.localization.TagTranslation;
import greencity.enums.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.*;
import java.util.*;

import static greencity.enums.UserStatus.ACTIVATED;

public class ModelUtils {
    public static User TEST_USER = createUser();
    public static User TEST_USER_ROLE_USER = createUserRoleUser();
    public static UserVO TEST_USER_VO = createUserVO();
    public static UserVO TEST_USER_VO_ROLE_USER = createUserVORoleUser();
    public static UserStatusDto TEST_USER_STATUS_DTO = createUserStatusDto();
    public static String TEST_EMAIL = "test@mail.com";
    public static String TEST_EMAIL_2 = "test2@mail.com";
    public static ZonedDateTime zonedDateTime = ZonedDateTime.now();
    public static LocalDateTime localDateTime = LocalDateTime.now();

    public static Tag getTag() {
        return new Tag(1L, TagType.ECO_NEWS, getTagTranslations(), Collections.emptyList(),
            Collections.emptySet(), Collections.emptyList());
    }

    public static Tag getEventTag() {
        return Tag.builder()
                .id(1L)
                .tagTranslations(getEventTagTranslations())
                .type(TagType.EVENT)
                .build();
    }


    public static Tag getHabitTag() {
        return new Tag(1L, TagType.HABIT, getHabitTagTranslations(), Collections.emptyList(),
                Collections.emptySet(), Collections.emptyList());
    }

    public static List<TagTranslation> getTagTranslations() {
        return Arrays.asList(
                TagTranslation.builder().id(1L).name("Новини").language(Language.builder().id(2L).code("ua").build())
                        .build(),
                TagTranslation.builder().id(2L).name("News").language(Language.builder().id(1L).code("en").build())
                        .build());
    }

    public static List<TagTranslation> getHabitTagTranslations() {
        return Arrays.asList(
                TagTranslation.builder().id(1L).name("Багаторазове використання")
                        .language(Language.builder().id(2L).code("ua").build())
                        .build(),
                TagTranslation.builder().id(2L).name("Reusable").language(Language.builder().id(1L).code("en").build())
                        .build());
    }

    public static List<TagTranslation> getEventTagTranslations() {
        Language language = getLanguage();
        return Arrays.asList(
                TagTranslation.builder().id(1L).name("Соціальний").language(getLanguageUa()).build(),
                TagTranslation.builder().id(2L).name("Social").language(language).build(),
                TagTranslation.builder().id(3L).name("Соціальний").language(language).build());
    }

    public static TagDto getTagDto() {
        return TagDto.builder().id(2L).name("News").build();
    }

    public static List<Tag> getTags() {
        return Collections.singletonList(getTag());
    }

    public static List<Tag> getHabitsTags() {
        return Collections.singletonList(getHabitTag());
    }

    public static User getUser() {
        return User.builder()
                .id(1L)
                .email(TestConst.EMAIL)
                .name(TestConst.NAME)
                .role(Role.ROLE_USER)
                .userStatus(UserStatus.ACTIVATED)
                .lastActivityTime(localDateTime)
                .verifyEmail(new VerifyEmail())
                .dateOfRegistration(localDateTime)
                .build();
    }

    public static UserVO getUserVO() {
        return UserVO.builder()
                .id(1L)
                .email(TestConst.EMAIL)
                .name(TestConst.NAME)
                .role(Role.ROLE_USER)
                .lastActivityTime(localDateTime)
                .verifyEmail(new VerifyEmailVO())
                .dateOfRegistration(localDateTime)
                .build();
    }

    public static UserManagementVO getUserManagementVO() {
        return UserManagementVO.builder()
                .id(1L)
                .userStatus(ACTIVATED)
                .email("Test@gmail.com")
                .role(Role.ROLE_ADMIN).build();
    }

    public static UserVO getUserVOWithData() {
        return UserVO.builder()
                .id(13L)
                .name("user")
                .email("namesurname1995@gmail.com")
                .role(Role.ROLE_USER)
                .userCredo("save the world")
                .firstName("name")
                .emailNotification(EmailNotification.MONTHLY)
                .userStatus(UserStatus.ACTIVATED)
                .rating(13.4)
                .verifyEmail(VerifyEmailVO.builder()
                        .id(32L)
                        .user(UserVO.builder()
                                .id(13L)
                                .name("user")
                                .build())
                        .expiryDate(LocalDateTime.of(2021, 7, 7, 7, 7))
                        .token("toooookkkeeeeen42324532542")
                        .build())
                .userFriends(Collections.singletonList(
                        UserVO.builder()
                                .id(75L)
                                .name("Andrew")
                                .build()))
                .refreshTokenKey("refreshtoooookkkeeeeen42324532542")
                .ownSecurity(null)
                .dateOfRegistration(LocalDateTime.of(2020, 6, 6, 13, 47))
                .city("Lviv")
                .showShoppingList(true)
                .showEcoPlace(true)
                .showLocation(true)
                .ownSecurity(OwnSecurityVO.builder()
                        .id(1L)
                        .password("password")
                        .user(UserVO.builder()
                                .id(13L)
                                .build())
                        .build())
                .lastActivityTime(LocalDateTime.of(2020, 12, 11, 13, 30))
                .build();
    }

    public static NotifiedUser getNotifiedUser() {
        return NotifiedUser.builder()
                .isRead(false)
                .id(1L)
                .user(ModelUtils.getUser())
                .notification(Notification.builder()
                        .id(1L)
                        .creationDate(ZonedDateTime.parse("2022-01-01T10:15:30+01:00"))
                        .title("TestTitle")
                        .sourceType(NotificationSourceType.NEWS_LIKED)
                        .sourceId(1L)
                        .author(ModelUtils.getUser())
                        .notificationSource(NotificationSource.NEWS)
                        .notifiedUsers(Collections.emptyList())
                        .build())
                .build();
    }

    public static Language getLanguage() {
        return new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(), Collections.emptyList());
    }

    public static Language getLanguageUa() {
        return new Language(2L, "ua", Collections.emptyList(), Collections.emptyList());
    }

    public static EcoNews getEcoNews() {
        Tag tag = new Tag();
        tag.setTagTranslations(
                List.of(TagTranslation.builder().name("Новини").language(Language.builder().code("ua").build()).build(),
                        TagTranslation.builder().name("News").language(Language.builder().code("en").build()).build()));
        return new EcoNews(1L, zonedDateTime, TestConst.SITE, "source", "shortInfo", getUser(),
                "title", "text", List.of(EcoNewsComment.builder().id(1L).text("test").build()),
                Collections.singletonList(tag), Collections.emptySet(), Collections.emptySet());
    }

    public static EcoNews getEcoNewsForFindDtoByIdAndLanguage() {
        return new EcoNews(1L, null, TestConst.SITE, null, "shortInfo", getUser(),
                "title", "text", null, Collections.singletonList(getTag()), Collections.emptySet(), Collections.emptySet());
    }

    public static EcoNewsVO getEcoNewsVO() {
        return new EcoNewsVO(1L, zonedDateTime, TestConst.SITE, null, getUserVO(),
                "title", "text", null, Collections.emptySet(), Collections.singletonList(getTagVO()),
                Collections.emptySet());
    }

    public static HabitStatusCalendar getHabitStatusCalendar() {
        return HabitStatusCalendar.builder()
                .enrollDate(LocalDate.now()).id(1L).build();
    }

    public static HabitAssign getHabitAssign() {
        return HabitAssign.builder()
                .id(1L)
                .status(HabitAssignStatus.ACQUIRED)
                .createDate(ZonedDateTime.now())
                .habit(Habit.builder()
                        .id(1L)
                        .image("")
                        .userId(2L)
                        .habitTranslations(Collections.singletonList(HabitTranslation.builder()
                                .id(1L)
                                .name("")
                                .description("")
                                .habitItem("")
                                .language(getLanguage())
                                .build()))
                        .build())
                .user(getUser())
                .userShoppingListItems(List.of(getUserShoppingListItem()))
                .workingDays(0)
                .duration(0)
                .habitStreak(0)
                .habitStatistic(Collections.singletonList(getHabitStatistic()))
                .habitStatusCalendars(Collections.singletonList(getHabitStatusCalendar()))
                .lastEnrollmentDate(ZonedDateTime.now())
                .build();
    }

    public static HabitStatistic getHabitStatistic() {
        return HabitStatistic.builder()
                .id(1L).habitRate(HabitRate.GOOD).createDate(ZonedDateTime.now())
                .amountOfItems(10).build();
    }

    public static UserShoppingListItem getCustomUserShoppingListItem() {
        return UserShoppingListItem.builder()
                .id(1L)
                .habitAssign(HabitAssign.builder().id(1L).build())
                .status(ShoppingListItemStatus.DONE)
                .build();
    }

    public static UserShoppingListItem getFullUserShoppingListItem() {
        return UserShoppingListItem.builder()
                .id(1L)
                .shoppingListItem(getShoppingListItem())
                .habitAssign(HabitAssign.builder().id(1L).build())
                .status(ShoppingListItemStatus.DONE)
                .build();
    }

    public static UserShoppingListItemResponseDto getUserShoppingListItemResponseDto() {
        return UserShoppingListItemResponseDto.builder()
                .id(1L)
                .text("TEST")
                .status(ShoppingListItemStatus.ACTIVE)
                .build();
    }

    public static ShoppingListItemResponseDto getShoppingListItemResponseDto() {
        return ShoppingListItemResponseDto.builder()
                .id(1L)
                .translations(Arrays.asList(
                        ShoppingListItemTranslationDTO.builder()
                                .id(2L)
                                .language(new LanguageVO(1L, AppConstant.DEFAULT_LANGUAGE_CODE))
                                .content("Buy a bamboo toothbrush")
                                .build(),
                        ShoppingListItemTranslationDTO.builder()
                                .id(11L)
                                .language(new LanguageVO(1L, AppConstant.DEFAULT_LANGUAGE_CODE))
                                .content("Start recycling batteries")
                                .build())
                ).build();
    }

    public static UserShoppingListItem getPredefinedUserShoppingListItem() {
        return UserShoppingListItem.builder()
                .id(2L)
                .habitAssign(HabitAssign.builder().id(1L).build())
                .status(ShoppingListItemStatus.ACTIVE)
                .shoppingListItem(ShoppingListItem.builder().id(1L).userShoppingListItems(Collections.emptyList())
                        .translations(
                                getShoppingListItemTranslations())
                        .build())
                .build();
    }

    public static UserShoppingListItemVO getUserShoppingListItemVO() {
        return UserShoppingListItemVO.builder()
                .id(1L)
                .habitAssign(HabitAssignVO.builder()
                        .id(1L)
                        .build())
                .status(ShoppingListItemStatus.DONE)
                .shoppingListItemVO(new ShoppingListItemVO(1L, Collections.emptyList(), Collections.emptyList()))
                .build();
    }

    public static UserShoppingListItem getUserShoppingListItem() {
        return UserShoppingListItem.builder()
                .id(1L)
                .status(ShoppingListItemStatus.DONE)
                .habitAssign(HabitAssign.builder()
                        .id(1L)
                        .status(HabitAssignStatus.ACQUIRED)
                        .habitStreak(10)
                        .duration(300)
                        .lastEnrollmentDate(ZonedDateTime.now())
                        .workingDays(5)
                        .build())
                .shoppingListItem(ShoppingListItem.builder()
                        .id(1L)
                        .build())
                .dateCompleted(LocalDateTime.of(2021, 2, 2, 14, 2))
                .build();
    }

    public static List<ShoppingListItemTranslation> getShoppingListItemTranslations() {
        return Arrays.asList(
                ShoppingListItemTranslation.builder()
                        .id(2L)
                        .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                                Collections.emptyList()))
                        .content("Buy a bamboo toothbrush")
                        .shoppingListItem(
                                new ShoppingListItem(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                        .build(),
                ShoppingListItemTranslation.builder()
                        .id(11L)
                        .language(new Language(1L, AppConstant.DEFAULT_LANGUAGE_CODE, Collections.emptyList(),
                                Collections.emptyList()))
                        .content("Start recycling batteries")
                        .shoppingListItem(
                                new ShoppingListItem(4L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
                        .build());
    }

    public static HabitFactTranslation getFactTranslation() {
        return HabitFactTranslation.builder()
                .id(1L)
                .factOfDayStatus(FactOfDayStatus.CURRENT)
                .habitFact(null)
                .content("Content")
                .language(getLanguage())
                .build();
    }

    public static HabitFactTranslationVO getFactTranslationVO() {
        return HabitFactTranslationVO.builder()
                .id(1L)
                .factOfDayStatus(FactOfDayStatus.CURRENT)
                .habitFact(null)
                .language(getLanguageVO())
                .content("Content")
                .build();
    }

    public static LanguageTranslationDTO getLanguageTranslationDTO() {
        return new LanguageTranslationDTO(getLanguageDTO(), "content");
    }

    public static LanguageDTO getLanguageDTO() {
        return new LanguageDTO(1L, "en");
    }

    public static AddEcoNewsDtoRequest getAddEcoNewsDtoRequest() {
        return new AddEcoNewsDtoRequest("title", "text",
                Collections.singletonList("News"), "source", null, "shortInfo");
    }

    public static AddEcoNewsDtoResponse getAddEcoNewsDtoResponse() {
        return new AddEcoNewsDtoResponse(1L, "title",
                "text", "shortInfo", EcoNewsAuthorDto.builder().id(1L).name(TestConst.NAME).build(),
                ZonedDateTime.now(), TestConst.SITE, "source",
                Arrays.asList("Новини", "News"));
    }

    public static MultipartFile getFile() {
        Path path = Paths.get("src/test/resources/test.jpg");
        String name = TestConst.IMG_NAME;
        String contentType = "photo/plain";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new MockMultipartFile(name,
                name, contentType, content);
    }

    public static URL getUrl() throws MalformedURLException {
        return new URL(TestConst.SITE);
    }

    public static EcoNewsAuthorDto getEcoNewsAuthorDto() {
        return new EcoNewsAuthorDto(1L, TestConst.NAME);
    }

    public static List<TagTranslationVO> getTagTranslationsVO() {
        return Arrays.asList(TagTranslationVO.builder().id(1L).name("Новини")
                        .languageVO(LanguageVO.builder().id(1L).code("ua").build()).build(),
                TagTranslationVO.builder().id(2L).name("News").languageVO(LanguageVO.builder().id(2L).code("en").build())
                        .build());
    }

    public static List<TagTranslationVO> getEventTagTranslationsVO() {
        return Arrays.asList(TagTranslationVO.builder().id(1L).name("Соціальний")
                        .languageVO(LanguageVO.builder().id(1L).code("ua").build()).build(),
                TagTranslationVO.builder().id(2L).name("Social").languageVO(LanguageVO.builder().id(2L).code("en").build())
                        .build());
    }

    public static LanguageVO getLanguageVO() {
        return new LanguageVO(1L, AppConstant.DEFAULT_LANGUAGE_CODE);
    }

    public static TagVO getTagVO() {
        return new TagVO(1L, TagType.ECO_NEWS, getTagTranslationsVO(), null, null);
    }

    public static TagVO getEventTagVO() {
        return TagVO.builder()
                .id(1L)
                .tagTranslations(getEventTagTranslationsVO())
                .type(TagType.EVENT)
                .build();
    }

    public static TagUaEnDto getTagUaEnDto(){
        return TagUaEnDto.builder()
                .id(1L)
                .nameUa(getEventTagTranslations().get(0).getName())
                .nameEn(getEventTagTranslations().get(1).getName())
                .build();
    }

    public static TagPostDto getTagPostDto() {
        return new TagPostDto(TagType.ECO_NEWS, getTagTranslationDtos());
    }

    public static List<TagTranslationDto> getTagTranslationDtos() {
        return Arrays.asList(
                TagTranslationDto.TagTranslationDtoBuilder().name("Новини")
                        .language(LanguageDTO.builder().id(2L).code("ua").build()).build(),
                TagTranslationDto.TagTranslationDtoBuilder().name("News")
                        .language(LanguageDTO.builder().id(1L).code("en").build()).build());
    }

    public static TagViewDto getTagViewDto() {
        return new TagViewDto("3", "ECO_NEWS", "News");
    }

    public static PageableAdvancedDto<TagVO> getPageableAdvancedDtoForTag() {
        return new PageableAdvancedDto<>(Collections.singletonList(getTagVO()),
                9, 1, 2, 1,
                true, false, false, true);
    }

    public static AddEcoNewsCommentDtoResponse getAddEcoNewsCommentDtoResponse() {
        return AddEcoNewsCommentDtoResponse.builder()
                .id(getEcoNewsComment().getId())
                .author(getEcoNewsCommentAuthorDto())
                .text(getEcoNewsComment().getText())
                .modifiedDate(getEcoNewsComment().getModifiedDate())
                .build();
    }

    public static EcoNewsComment getEcoNewsComment() {
        return EcoNewsComment.builder()
                .id(1L)
                .text("text")
                .createdDate(LocalDateTime.now())
                .modifiedDate(LocalDateTime.now())
                .user(getUser())
                .ecoNews(getEcoNews())
                .build();
    }

    public static EcoNewsCommentAuthorDto getEcoNewsCommentAuthorDto() {
        return EcoNewsCommentAuthorDto.builder()
                .id(getUser().getId())
                .name(getUser().getName().trim())
                .userProfilePicturePath(getUser().getProfilePicturePath())
                .build();
    }

    public static AddEcoNewsCommentDtoRequest getAddEcoNewsCommentDtoRequest() {
        return new AddEcoNewsCommentDtoRequest("text", 0L);
    }

    public static EcoNewsCommentDto getEcoNewsCommentDto() {
        return EcoNewsCommentDto.builder()
                .id(1L)
                .modifiedDate(LocalDateTime.now())
                .author(getEcoNewsCommentAuthorDto())
                .text("text")
                .replies(0)
                .likes(0)
                .currentUserLiked(false)
                .status(CommentStatus.ORIGINAL)
                .build();
    }

    public static List<LanguageTranslationDTO> getLanguageTranslationsDTOs() {
        return Arrays.asList(
                new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "hello"),
                new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "text"),
                new LanguageTranslationDTO(new LanguageDTO(1L, "en"), "smile"));
    }

    public static EcoNewsDto getEcoNewsDto() {
        return new EcoNewsDto(ZonedDateTime.now(), "imagePath", 1L, "title", "content", "text",
                getEcoNewsAuthorDto(), Collections.singletonList("tag"), Collections.singletonList("тег"), 1, 0, 0);
    }

    public static EcoNewsGenericDto getEcoNewsGenericDto() {
        String[] tagsEn = {"News"};
        String[] tagsUa = {"Новини"};
        return new EcoNewsGenericDto(1L, "title", "text", "shortInfo",
                ModelUtils.getEcoNewsAuthorDto(), zonedDateTime, "https://google.com/", "source",
                List.of(tagsUa), List.of(tagsEn), 0, 1, 0);
    }

    public static EcoNewsDto getEcoNewsDtoForFindDtoByIdAndLanguage() {
        return new EcoNewsDto(null, TestConst.SITE, 1L, "title", "text", "shortInfo",
                getEcoNewsAuthorDto(), Collections.singletonList("News"), Collections.singletonList("Новини"), 0, 0, 0);
    }

    public static UpdateEcoNewsDto getUpdateEcoNewsDto() {
        return new UpdateEcoNewsDto(1L, "title", "text", "shortInfo", Collections.singletonList("tag"),
                "image", "source");
    }

    public static SearchNewsDto getSearchNewsDto() {
        return new SearchNewsDto(1L, "title", getEcoNewsAuthorDto(), ZonedDateTime.now(),
                Collections.singletonList("tag"));
    }

    public static EcoNewsCommentVO getEcoNewsCommentVO() {
        return new EcoNewsCommentVO(1L, "text", LocalDateTime.now(), LocalDateTime.now(), new EcoNewsCommentVO(),
                new ArrayList<>(), getUserVO(), getEcoNewsVO(), false,
                false, new HashSet<>());
    }

    public static EcoNewsDtoManagement getEcoNewsDtoManagement() {
        return new EcoNewsDtoManagement(1L, "title", "text", ZonedDateTime.now(),
                Collections.singletonList("tag"), "imagePath", "source");
    }

    public static EcoNewsViewDto getEcoNewsViewDto() {
        return new EcoNewsViewDto("1", "title", "author", "text", "startDate",
                "endDate", "tag");
    }

    public static ShoppingListItem getShoppingListItem() {
        return ShoppingListItem.builder()
                .id(1L)
                .translations(getShoppingListItemTranslations())
                .build();
    }

    public static HabitAssignPropertiesDto getHabitAssignPropertiesDto() {
        return HabitAssignPropertiesDto.builder()
                .defaultShoppingListItems(List.of(1L))
                .duration(20)
                .build();
    }

    public static HabitAssign getHabitAssignWithUserShoppingListItem() {
        return HabitAssign.builder()
                .id(1L)
                .user(User.builder().id(21L).build())
                .habit(Habit.builder().id(1L).build())
                .status(HabitAssignStatus.INPROGRESS)
                .workingDays(0)
                .duration(20)
                .userShoppingListItems(List.of(UserShoppingListItem.builder()
                        .id(1L)
                        .shoppingListItem(ShoppingListItem.builder().id(1L).build())
                        .status(ShoppingListItemStatus.INPROGRESS)
                        .build()))
                .build();
    }

    private static UserStatusDto createUserStatusDto() {
        return UserStatusDto.builder()
                .id(2L)
                .userStatus(UserStatus.CREATED)
                .build();
    }

    private static User createUserRoleUser() {
        return User.builder()
                .id(2L)
                .role(Role.ROLE_USER)
                .email("test2@mail.com")
                .build();
    }

    private static UserVO createUserVORoleUser() {
        return UserVO.builder()
                .id(2L)
                .role(Role.ROLE_USER)
                .email("test2@mail.com")
                .build();
    }

    private static User createUser() {
        return User.builder()
                .id(1L)
                .role(Role.ROLE_MODERATOR)
                .email("test@mail.com")
                .build();
    }

    private static UserVO createUserVO() {
        return UserVO.builder()
                .id(1L)
                .role(Role.ROLE_MODERATOR)
                .email("test@mail.com")
                .build();
    }

    public static List<UserShoppingListItemVO> getUserShoppingListItemVOList() {
        List<UserShoppingListItemVO> list = new ArrayList<>();
        list.add(UserShoppingListItemVO.builder()
                .id(1L)
                .build());
        return list;
    }

    public static List<CustomShoppingListItemVO> getCustomShoppingListItemVOList() {
        List<CustomShoppingListItemVO> list = new ArrayList<>();
        list.add(CustomShoppingListItemVO.builder()
                .id(1L)
                .text("text")
                .build());
        return list;
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDto() {
        return CustomShoppingListItemResponseDto.builder()
                .id(1L)
                .status(ShoppingListItemStatus.INPROGRESS)
                .text("TEXT")
                .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItem() {
        return CustomShoppingListItem.builder()
                .id(1L)
                .status(ShoppingListItemStatus.INPROGRESS)
                .text("TEXT")
                .build();
    }

    public static Principal getPrincipal() {
        return () -> "danylo@gmail.com";
    }

    public static UserFilterDtoRequest getUserFilterDtoRequest() {
        return UserFilterDtoRequest.builder()
                .userRole("USER")
                .name("Test_Filter")
                .searchCriteria("Test")
                .userStatus("ACTIVATED")
                .build();
    }

    public static UserFilterDtoResponse getUserFilterDtoResponse() {
        return UserFilterDtoResponse.builder()
                .id(1L)
                .userRole("ADMIN")
                .searchCriteria("Test")
                .userStatus("ACTIVATED")
                .name("Test")
                .build();
    }

    public static Filter getFilter() {
        return Filter.builder()
                .id(1L)
                .name("Test")
                .user(new User())
                .type("USERS")
                .values("Test;ADMIN;ACTIVATED")
                .build();
    }

    public static CustomShoppingListItem getCustomShoppingListItemWithStatusInProgress() {
        return CustomShoppingListItem.builder()
                .id(2L)
                .habit(Habit.builder()
                        .id(3L)
                        .build())
                .user(getUser())
                .text("item")
                .status(ShoppingListItemStatus.INPROGRESS)
                .build();
    }

    public static CustomShoppingListItemResponseDto getCustomShoppingListItemResponseDtoWithStatusInProgress() {
        return CustomShoppingListItemResponseDto.builder()
                .id(2L)
                .text("item")
                .status(ShoppingListItemStatus.INPROGRESS)
                .build();
    }

    public static RatingStatisticsVO getRatingStatisticsVO() {
        return RatingStatisticsVO.builder()
                .id(1L)
                .rating(5.0)
                .createDate(zonedDateTime)
                .pointsChanged(1.0)
                .ratingCalculationEnum(RatingCalculationEnum.LIKE_COMMENT)
                .user(getUserVO())
                .build();
    }

    public static RatingStatistics getRatingStatistics() {
        return RatingStatistics.builder()
                .id(1L)
                .rating(5.0)
                .createDate(zonedDateTime)
                .pointsChanged(1.0)
                .ratingCalculationEnum(RatingCalculationEnum.LIKE_COMMENT)
                .user(getUser())
                .build();
    }


    public static Notification getNotification() {
        return Notification.builder()
                .id(1L)
                .notificationSource(NotificationSource.COMMENT)
                .author(ModelUtils.getUser())
                .title("test")
                .notifiedUsers(List.of(getNotifiedUser()))
                .creationDate(zonedDateTime)
                .sourceType(NotificationSourceType.COMMENT_LIKED)
                .sourceId(4L)
                .build();
    }

    public static NotificationDtoResponse getNotificationDtoResponse() {
        return NotificationDtoResponse.builder()
                .id(1L)
                .isRead(false)
                .author(AuthorDto.builder()
                        .id(1L)
                        .name("test")
                        .build())
                .title("test")
                .creationDate(zonedDateTime)
                .sourceType(NotificationSourceType.COMMENT_LIKED)
                .sourceId(4L)
                .build();

    }

    public static AddressLatLngResponse getAddressLatLngResponse() {
        return AddressLatLngResponse
                .builder()
                .latitude(51.1234567)
                .longitude(28.7654321)
                .streetEn("fake street name")
                .streetUa("вулиця")
                .houseNumber("13")
                .cityEn("fake city")
                .cityUa("місто")
                .regionEn("fake region")
                .regionUa("область")
                .countryEn("fake country")
                .countryUa("країна")
                .formattedAddressEn("Full formatted address")
                .formattedAddressUa("Повна відформатована адреса")
                .build();
    }

    public static GeocodingResult[] getGeocodingResultUk() {
        GeocodingResult geocodingResult = new GeocodingResult();

        geocodingResult.formattedAddress = "Повна відформатована адреса";

        AddressComponent route = new AddressComponent();
        route.longName = "вулиця";
        route.types = new AddressComponentType[]{AddressComponentType.ROUTE};

        AddressComponent streetNumber = new AddressComponent();
        streetNumber.longName = "13";
        streetNumber.types = new AddressComponentType[]{AddressComponentType.STREET_NUMBER};

        AddressComponent locality = new AddressComponent();
        locality.longName = "місто";
        locality.types = new AddressComponentType[]{AddressComponentType.LOCALITY};

        AddressComponent region = new AddressComponent();
        region.longName = "область";
        region.types = new AddressComponentType[]{AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1};

        AddressComponent country = new AddressComponent();
        country.longName = "країна";
        country.types = new AddressComponentType[]{AddressComponentType.COUNTRY};

        geocodingResult.addressComponents = new AddressComponent[]{
                locality,
                streetNumber,
                region,
                country,
                route
        };

        return new GeocodingResult[]{geocodingResult};
    }

    public static GeocodingResult[] getGeocodingResultEn() {
        GeocodingResult geocodingResult = new GeocodingResult();

        geocodingResult.formattedAddress = "Full formatted address";

        AddressComponent route = new AddressComponent();
        route.longName = "fake street name";
        route.types = new AddressComponentType[]{AddressComponentType.ROUTE};

        AddressComponent streetNumber = new AddressComponent();
        streetNumber.longName = "13";
        streetNumber.types = new AddressComponentType[]{AddressComponentType.STREET_NUMBER};

        AddressComponent locality = new AddressComponent();
        locality.longName = "fake city";
        locality.types = new AddressComponentType[]{AddressComponentType.LOCALITY};

        AddressComponent region = new AddressComponent();
        region.longName = "fake region";
        region.types = new AddressComponentType[]{AddressComponentType.ADMINISTRATIVE_AREA_LEVEL_1};

        AddressComponent country = new AddressComponent();
        country.longName = "fake country";
        country.types = new AddressComponentType[]{AddressComponentType.COUNTRY};

        geocodingResult.addressComponents = new AddressComponent[]{
                locality,
                streetNumber,
                region,
                country,
                route
        };

        return new GeocodingResult[]{geocodingResult};
    }

    public static AddEventDtoRequest getRequestAddEventDto() {
        return AddEventDtoRequest.builder()
                .title("Eco-Friendly Events Social Media")
                .description("How to Promote Eco-Friendly Events on Social Media")
                .open(true)
                .datesLocations(
                        List.of(EventDateLocationDto.builder()
                                .id(1L)
                                .startDate(ZonedDateTime.parse("2024-01-17T06:00Z[UTC]"))
                                .finishDate(ZonedDateTime.parse("2024-01-17T06:00Z[UTC]"))
                                .onlineLink("http://localhost:8080/swagger-ui.html#/")
                                .coordinates(AddressDto.builder()
                                        .latitude(45.466272)
                                        .longitude(9.188604)
                                        .build())
                                .build()))
                .tags(Collections.singletonList(getTagUaEnDto().getNameUa()))
                .build();
    }

    public static EventDto getEventDto() {
        return EventDto.builder()
                .title("Eco-Friendly Events Social Media")
                .description("How to Promote Eco-Friendly Events on Social Media")
                .open(true)
                .dates(
                        List.of(EventDateLocationDto.builder()
                                .id(1L)
                                .startDate(ZonedDateTime.parse("2026-01-17T06:00Z[UTC]"))
                                .finishDate(ZonedDateTime.parse("2026-01-17T06:00Z[UTC]"))
                                .onlineLink("http://localhost:8080/swagger-ui.html#/")
                                .coordinates(AddressDto.builder()
                                        .latitude(45.466272)
                                        .longitude(9.188604)
                                        .build())
                                .build()))
                .tags(Collections.singletonList(getTagUaEnDto()))
                .build();
    }

    public static Event getEvent() {
        return Event.builder()
                .title("Eco-Friendly Events Social Media")
                .description("How to Promote Eco-Friendly Events on Social Media")
                .open(true)
                .dates(
                        List.of(EventDateLocation.builder()
                                .id(1L)
                                .startDate(ZonedDateTime.parse("2026-01-17T06:00Z[UTC]"))
                                .finishDate(ZonedDateTime.parse("2026-01-17T06:00Z[UTC]"))
                                .onlineLink("http://localhost:8080/swagger-ui.html#/")
                                .coordinates(Address.builder()
                                        .latitude(45.466272)
                                        .longitude(9.188604)
                                        .build())
                                .build()))
                .tags(Collections.singletonList(getEventTag()))
                .build();
    }

    public static UpdateEventDto getUpdateEventDto() {
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setId(1L);
        return updateEventDto;
    }

    public static Event getEventWithFinishedDate() {
        return Event.builder()
                .title("Eco-Friendly Events Social Media")
                .description("How to Promote Eco-Friendly Events on Social Media")
                .titleImage(AppConstant.DEFAULT_EVENT_IMAGES)
                .open(true)
                .dates(
                        List.of(EventDateLocation.builder()
                                .id(1L)
                                .startDate(ZonedDateTime.parse("2021-01-17T06:00Z[UTC]"))
                                .finishDate(ZonedDateTime.parse("2021-01-18T06:00Z[UTC]"))
                                .onlineLink("http://localhost:8080/swagger-ui.html#/")
                                .coordinates(Address.builder()
                                        .latitude(45.466272)
                                        .longitude(9.188604)
                                        .build())
                                .build()))
                .tags(Collections.singletonList(getEventTag()))
                .build();
    }

    public static Event getExpectedEvent() {
        return Event.builder()
                .title("Eco-Friendly Events Social Media")
                .description("How to Promote Eco-Friendly Events on Social Media")
                .titleImage(AppConstant.DEFAULT_EVENT_IMAGES)
                .open(true)
                .dates(
                        List.of(EventDateLocation.builder()
                                .id(1L)
                                .startDate(ZonedDateTime.parse("2021-01-17T06:00Z[UTC]"))
                                .finishDate(ZonedDateTime.parse("2021-01-18T06:00Z[UTC]"))
                                .onlineLink("http://localhost:8080/swagger-ui.html#/")
                                .coordinates(Address.builder()
                                        .latitude(45.466272)
                                        .longitude(9.188604)
                                        .build())
                                .build()))
                .tags(Collections.singletonList(getEventTag()))
                .build();
    }
}
