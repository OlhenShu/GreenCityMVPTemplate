package greencity.service;

import greencity.dto.specification.SpecificationNameDto;
import greencity.dto.specification.SpecificationVO;
import greencity.entity.Specification;
import greencity.repository.SpecificationRepo;
import greencity.service.SpecificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecificationServiceImplTest {
    @InjectMocks
    private SpecificationServiceImpl specificationService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SpecificationRepo specificationRepo;

    private final SpecificationVO specificationVO =
            SpecificationVO.builder()
                    .id(1L)
                    .name("test")
                    .build();

    @Test
    void save() {
        when(specificationService.save(specificationVO)).thenReturn(specificationVO);
        SpecificationVO actual = specificationService.save(specificationVO);
        assertEquals(specificationVO, actual);
    }

    @Test
    void findById() {
        Specification specification = Specification
                .builder()
                .id(1L)
                .name("test")
                .build();

        when(specificationRepo.findById(anyLong())).thenReturn(Optional.of(new Specification(1L, "test")));
        when(modelMapper.map(specification, SpecificationVO.class)).thenReturn(specificationVO);

        SpecificationVO actual = specificationService.findById(anyLong());
        assertEquals(specificationVO, actual);
    }

    @Test
    void findAll() {
        List<SpecificationVO> specifications = Arrays.asList(
                new SpecificationVO(1L, "test"),
                new SpecificationVO(2L, "test")
        );

        when(specificationService.findAll()).thenReturn(specifications);
        List<SpecificationVO> actual = specificationService.findAll();
        assertEquals(specifications, actual);
    }

    @Test
    void deleteById() {
        Specification specification = new Specification(1L, "test");
        when(specificationRepo.findById(1L)).thenReturn(Optional.of(specification));

        assertEquals(1L, specificationService.deleteById(1L));
    }

    @Test
    void findByName() {
        Specification specification = new Specification(1L, "test");
        when(specificationRepo.findByName(anyString())).thenReturn(Optional.of(specification));
        SpecificationVO actual = specificationService.findByName(anyString());
        assertEquals(modelMapper.map(specification, SpecificationVO.class), actual);

    }

    @Test
    void findAllSpecificationDto() {
        List<SpecificationVO> specifications = Arrays.asList(
                new SpecificationVO(1L, "test"),
                new SpecificationVO(2L, "test")
        );
        when(specificationService.findAll()).thenReturn(specifications);
        List<SpecificationNameDto> actual = specificationService.findAllSpecificationDto();

        assertEquals(specifications.size(), actual.size());
    }
}