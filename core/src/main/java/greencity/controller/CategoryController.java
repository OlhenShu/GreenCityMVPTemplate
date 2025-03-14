package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.category.CategoryDto;
import greencity.dto.category.CategoryDtoResponse;
import greencity.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    /**
     * The method which returns new {@code Category}.
     *
     * @param dto - CategoryDto dto for adding with all parameters.
     * @return new {@code Category}.
     * @author Kateryna Horokh
     */
    @ApiOperation(value = "Save category")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = CategoryDtoResponse.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @PostMapping
    public ResponseEntity<CategoryDtoResponse> saveCategory(@Valid @RequestBody CategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(dto));
    }

    /**
     * The method which returns all {@code Category}.
     *
     * @return list of {@code Category}.
     * @author Kateryna Horokh
     */
    @ApiOperation(value = "View a list of available categories")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAllCategory() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAllCategoryDto());
    }
}
