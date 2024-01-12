package greencity.service.ticket292;

import greencity.ModelUtils;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.*;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.dto.language.LanguageDTO;
import greencity.dto.habitfact.HabitFactDto;
import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.dto.habitfact.HabitFactUpdateDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.habitfact.HabitFactViewDto;

import greencity.enums.FactOfDayStatus;

import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotUpdatedException;

import greencity.filters.HabitFactSpecification;

import greencity.repository.HabitFactRepo;
import greencity.repository.HabitFactTranslationRepo;
import greencity.repository.HabitRepo;

import greencity.entity.HabitFactTranslation;
import greencity.entity.Habit;
import greencity.entity.HabitFact;
import greencity.service.HabitFactServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static greencity.ModelUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class HabitFactServiceImplTest {

    @Mock
    HabitRepo habitRepo;
    @Mock
    HabitFactRepo habitFactRepo;
    @Mock
    HabitFactTranslationRepo habitFactTranslationRepo;
    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    HabitFactServiceImpl habitFactService;

    @BeforeEach
    void initEach() {
    }


    @Test
    void getAllHabitFactsTest() {
        Long langId = 1L;
        String langCode = "en";
        String content = "some content";
        Pageable page = PageRequest.of(0, 5);

        LanguageDTO langDTO = new LanguageDTO(langId, langCode);
        LanguageTranslationDTO langTransDTO = new LanguageTranslationDTO(langDTO, content);
        List<LanguageTranslationDTO> langTransDTOList = Collections.singletonList(langTransDTO);

        HabitFactTranslation habitFactTrans = getFactTranslation();
        List<HabitFactTranslation> habitFactTransList = Collections.singletonList(habitFactTrans);


        PageableDto<LanguageTranslationDTO> pageableDto =
                new PageableDto<>(langTransDTOList, langTransDTOList.size(), 0, 1);

        Page<HabitFactTranslation> pageFacts =
                new PageImpl<>(habitFactTransList, page, habitFactTransList.size());

        Mockito.when(modelMapper.map(habitFactTransList.get(0), LanguageTranslationDTO.class))
                .thenReturn(langTransDTO);

        Mockito.when(habitFactTranslationRepo.findAllByLanguageCode(page, langCode))
                .thenReturn(pageFacts);

        PageableDto<LanguageTranslationDTO> actual = habitFactService.getAllHabitFacts(page, langCode);
        Assertions.assertEquals(pageableDto, actual);
    }

    @Test
    void getAllHabitFactsTest_Exception() {
        String language = "en";
        Pageable pageable = PageRequest.of(5, 5);
        List<HabitFactTranslation> habitFactTranslation =
                Collections.singletonList(ModelUtils.getFactTranslation());
        Page<HabitFactTranslation> pageFacts = new PageImpl<>(habitFactTranslation);
        Mockito.when(habitFactTranslationRepo.findAllByLanguageCode(pageable, language))
                .thenReturn(pageFacts);

        Assertions.assertThrows(BadRequestException.class, () -> habitFactService.getAllHabitFacts(pageable, language));
    }

    @Test
    void getAllHabitFactsListTest() {
        String language = "en";
        Pageable pageable = PageRequest.of(5, 5);
        List<HabitFactTranslation> habitFactTranslation =
                Collections.singletonList(ModelUtils.getFactTranslation());
        Page<HabitFactTranslation> pageFacts = new PageImpl<>(habitFactTranslation);
        Mockito.when(habitFactTranslationRepo.findAllByLanguageCode(pageable, language))
                .thenReturn(pageFacts);
        Assertions.assertThrows(BadRequestException.class, () -> habitFactService.getAllHabitFactsList(pageable, language));
    }

    @Test
    void getRandomHabitFactByHabitIdAndLanguageTest() {
        Long id = 1L;
        String language = "ua";
        HabitFactTranslation habitFactTranslation = ModelUtils.getFactTranslation();
        LanguageTranslationDTO languageTranslationDTO = ModelUtils.getLanguageTranslationDTO();
        Mockito.when(habitFactTranslationRepo.getRandomHabitFactTranslationByHabitIdAndLanguage(language, id))
                .thenReturn(Optional.of(habitFactTranslation));
        Mockito.when(modelMapper.map(habitFactTranslation, LanguageTranslationDTO.class)).thenReturn(languageTranslationDTO);

        LanguageTranslationDTO actual = habitFactService.getRandomHabitFactByHabitIdAndLanguage(id, language);

        Assertions.assertEquals(languageTranslationDTO, actual);
    }

    @Test
    void getAllHabitFactsVOTest() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);

        HabitFactVO habitFactVO = new HabitFactVO(1L, Collections.singletonList(getFactTranslationVO()), null);

        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        Page<HabitFact> pageAdvices = new PageImpl<>(habitFacts,
                pageable, habitFacts.size());
        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);
        Mockito.when(habitFactRepo.findAll(pageable)).thenReturn(pageAdvices);
        Mockito.when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        PageableDto<HabitFactVO> actual = habitFactService.getAllHabitFactsVO(pageable);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getRandomHabitFactByHabitIdAndLanguageTest2() {
        Long id = 1L;
        String language = "ua";
        Mockito.when(habitFactTranslationRepo.getRandomHabitFactTranslationByHabitIdAndLanguage(language, id))
                .thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class,
                () -> habitFactService.getRandomHabitFactByHabitIdAndLanguage(id, language));
    }

    @Test
    void getHabitFactByIdTest() {
        Long id = 1L;
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
        HabitFactVO habitFactVO = new HabitFactVO(1L, Collections.singletonList(getFactTranslationVO()), null);

        HabitFactDtoResponse habitFactDtoResponse = new HabitFactDtoResponse();
        Mockito.when(habitFactRepo.findById(id)).thenReturn(Optional.of(habitFact));
        Mockito.when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        Mockito.when(modelMapper.map(habitFactVO, HabitFactDtoResponse.class)).thenReturn(habitFactDtoResponse);

        HabitFactDtoResponse actual = habitFactService.getHabitFactById(id);

        Assertions.assertEquals(habitFactDtoResponse, actual);
    }

    @Test
    void getHabitFactByIdTest2() {
        Long id = 1L;
        Mockito.when(habitFactRepo.findById(id)).thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> habitFactService.getHabitFactById(id));
    }

    @Test
    void getHabitFactByNameTest() {
        String language = "ua";
        String name = "name";
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);

        HabitFactTranslation habitFactTranslation = HabitFactTranslation.builder()
                .id(1L)
                .habitFact(habitFact)
                .factOfDayStatus(FactOfDayStatus.POTENTIAL)
                .language(ModelUtils.getLanguage())
                .content("content")
                .build();


        HabitFactDto habitFactDto = HabitFactDto.builder()
                .id(1L)
                .habit(HabitDto.builder()
                        .id(1L)
                        .image("")
                        .habitTranslation(null)
                        .build())
                .content("content")
                .build();

        Mockito.when(habitFactTranslationRepo.findFactTranslationByLanguageCodeAndContent(language, name))
                .thenReturn(Optional.of(habitFactTranslation));
        Mockito.when(modelMapper.map(habitFactTranslation, HabitFactDto.class)).thenReturn(habitFactDto);

        HabitFactDto actual = habitFactService.getHabitFactByName(language, name);

        Assertions.assertEquals(habitFactDto, actual);
    }

    @Test
    void getHabitFactByNameTest2() {
        String language = "ua";
        String name = "name";
        Mockito.when(habitFactTranslationRepo.findFactTranslationByLanguageCodeAndContent(language, name))
                .thenThrow(NotFoundException.class);

        Assertions.assertThrows(NotFoundException.class, () -> habitFactService.getHabitFactByName(language, name));
    }

    @Test
    void saveTest() {
//        Tag tag =  new Tag(1L, TagType.ECO_NEWS, getTagTranslations(), Collections.emptyList(), Collections.emptySet());

        Habit habit = Habit.builder().id(1L).image("image.png").complexity(1).tags(new HashSet<>(getTags())).build();
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
        habitFact.setHabit(habit);

        HabitVO habitVO = HabitVO.builder().id(1L).image("img.png").build();
        HabitFactVO habitFactVO = new HabitFactVO(1L, Collections.singletonList(getFactTranslationVO()), null);
        habitFactVO.setHabit(habitVO);

        HabitFactPostDto habitFactPostDto = HabitFactPostDto.builder()
                .habit(HabitIdRequestDto.builder()
                        .id(1L)
                        .build())
                .translations(Collections.singletonList(getLanguageTranslationDTO()))
                .build();

        Mockito.when(modelMapper.map(habitFactPostDto, HabitFact.class)).thenReturn(habitFact);
        Mockito.when(habitFactRepo.save(habitFact)).thenReturn(habitFact);
        Mockito.when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        Mockito.when(habitRepo.findById(habitFactVO.getHabit().getId())).thenReturn(Optional.of(habitFact.getHabit()));

        HabitFactVO actual = habitFactService.save(habitFactPostDto);

        Assertions.assertEquals(habitFactVO, actual);
    }

    @Test
    void saveTest_Exception() {
        HabitFactPostDto habitFactPostDto = HabitFactPostDto.builder()
                .habit(HabitIdRequestDto.builder()
                        .id(1L)
                        .build())
                .translations(Collections.singletonList(getLanguageTranslationDTO()))
                .build();
        Mockito.when(habitRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> habitFactService.save(habitFactPostDto));
    }

    @Test
    void updateTest() {
        Long id = 1L;
        Habit habit = Habit.builder().id(1L).image("image.png").complexity(1).tags(new HashSet<>(getTags())).build();
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
        habitFact.setHabit(habit);

        List<HabitFactTranslationUpdateDto> habitFactTranslationUpdateDtoList = new ArrayList<>(Arrays.asList(
                HabitFactTranslationUpdateDto.builder().content("ua").factOfDayStatus(FactOfDayStatus.POTENTIAL)
                        .language(getLanguageDTO()).build(),
                HabitFactTranslationUpdateDto.builder().content("en").factOfDayStatus(FactOfDayStatus.POTENTIAL)
                        .language(getLanguageDTO()).build()));

        HabitFactUpdateDto habitFactUpdateDto = HabitFactUpdateDto.builder()
                .habit(HabitIdRequestDto.builder()
                        .id(1L)
                        .build())
                .translations(habitFactTranslationUpdateDtoList)
                .build();

        Mockito.when(habitFactRepo.findById(id)).thenReturn(Optional.of(habitFact));
        Mockito.when(habitRepo.findById(id)).thenReturn(Optional.of(habit));
        Mockito.when(habitFactRepo.save(habitFact)).thenReturn(habitFact);

        HabitFactVO expected = modelMapper.map(habitFact, HabitFactVO.class);
        Mockito.when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(expected);

        HabitFactVO actual = habitFactService.update(habitFactUpdateDto, id);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void updateTest_Exception() {
        Long id = 1L;

        List<HabitFactTranslationUpdateDto> habitFactTranslationUpdateDtoList = new ArrayList<>(Arrays.asList(
                HabitFactTranslationUpdateDto.builder().content("ua").factOfDayStatus(FactOfDayStatus.POTENTIAL)
                        .language(getLanguageDTO()).build(),
                HabitFactTranslationUpdateDto.builder().content("en").factOfDayStatus(FactOfDayStatus.POTENTIAL)
                        .language(getLanguageDTO()).build()));

        HabitFactUpdateDto habitFactUpdateDto = HabitFactUpdateDto.builder()
                .habit(HabitIdRequestDto.builder()
                        .id(1L)
                        .build())
                .translations(habitFactTranslationUpdateDtoList)
                .build();

        Assertions.assertThrows(NotUpdatedException.class, () -> habitFactService.update(habitFactUpdateDto, id));
    }

    @Test
    void deleteTest() {
        Long id = 1L;
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
        Mockito.when(habitFactRepo.findById(id)).thenReturn(Optional.of(habitFact));
        Long actual = habitFactService.delete(id);

        Mockito.verify(habitFactRepo).deleteById(id);
        Assertions.assertEquals(id, actual);
    }

    @Test
    void deleteTest_Exception() {
        Long id = 1L;
        Mockito.when(habitFactRepo.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotDeletedException.class, () -> habitFactService.delete(id));
    }

    @Test
    void deleteAllByHabitTest() {
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
        HabitVO habitVO = HabitVO.builder().id(1L).image("img.png").build();
        Mockito.when(habitFactRepo.findAllByHabitId(habitVO.getId())).thenReturn(Collections.singletonList(habitFact));
        habitFactService.deleteAllByHabit(habitVO);

        Mockito.verify(habitFactTranslationRepo, times(1)).deleteAllByHabitFact(habitFact);
    }

    @Test
    void deleteAllHabitFactsByListOfIdTest() {
        List<Long> ids = List.of(1L, 2L, 3L);
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
        Mockito.when(habitFactRepo.findById(anyLong())).thenReturn(Optional.of(habitFact));
        List<Long> actual = habitFactService.deleteAllHabitFactsByListOfId(ids);

        Assertions.assertEquals(ids, actual);
        Mockito.verify(habitFactRepo, times(ids.size())).delete(habitFact);
    }

    @Test
    void searchByTest() {
        int pageNumber = 0;
        int pageSize = 1;
        String query = "eng";
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
        HabitFactVO habitFactVO = new HabitFactVO(1L, Collections.singletonList(getFactTranslationVO()), null);
        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        Page<HabitFact> habitFactPage = new PageImpl<>(habitFacts,
                pageable, habitFacts.size());
        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);

        Mockito.when(habitFactRepo.searchHabitFactByFilter(pageable, query)).thenReturn(habitFactPage);
        Mockito.when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);

        PageableDto<HabitFactVO> actual = habitFactService.getAllHabitFactVOsWithFilter(query, pageable);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getFilteredDataForManagementByPage2() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);
        HabitFactVO habitFactVO = new HabitFactVO(1L, Collections.singletonList(ModelUtils.getFactTranslationVO()), null);
        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        HabitFactViewDto habitFactViewDto = new HabitFactViewDto("1", "1", "eng");
        Page<HabitFact> habitFactPage = new PageImpl<>(habitFacts,
                pageable, habitFacts.size());

        Mockito.when(habitFactRepo.findAll(any(HabitFactSpecification.class), eq(pageable))).thenReturn(habitFactPage);
        Mockito.when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);

        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);
        PageableDto<HabitFactVO> actual = habitFactService.getFilteredDataForManagementByPage(pageable, habitFactViewDto);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getFilteredDataForManagementByPage() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        HabitFact habitFact = new HabitFact(1L, Collections.singletonList(getFactTranslation()), null);

        HabitFactVO habitFactVO = new HabitFactVO(1L, Collections.singletonList(ModelUtils.getFactTranslationVO()), null);

        List<HabitFact> habitFacts = Collections.singletonList(habitFact);
        List<HabitFactVO> habitFactVOS = Collections.singletonList(habitFactVO);
        HabitFactViewDto habitFactViewDto = new HabitFactViewDto("", "", "");
        Page<HabitFact> habitFactPage = new PageImpl<>(habitFacts,
                pageable, habitFacts.size());

        Mockito.when(habitFactRepo.findAll(any(HabitFactSpecification.class), eq(pageable))).thenReturn(habitFactPage);

        Mockito.when(modelMapper.map(habitFact, HabitFactVO.class)).thenReturn(habitFactVO);
        PageableDto<HabitFactVO> expected = new PageableDto<>(habitFactVOS, habitFacts.size(), pageNumber, pageSize);
        PageableDto<HabitFactVO> actual = habitFactService
                .getFilteredDataForManagementByPage(pageable, habitFactViewDto);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getHabitFactOfTheDay() {
        HabitFactTranslation res = getFactTranslation();
        Mockito.when(habitFactTranslationRepo.findAllByFactOfDayStatusAndLanguageId(FactOfDayStatus.CURRENT, 1L))
                .thenReturn(res);
        Mockito.when(modelMapper.map(res, LanguageTranslationDTO.class)).thenReturn(getLanguageTranslationDTO());
        Assertions.assertEquals(getLanguageTranslationDTO(), habitFactService.getHabitFactOfTheDay(1L));
    }

}

