package greencity.webcontroller;

import greencity.annotations.ApiPageable;
import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habit.HabitVO;
import greencity.enums.HabitAssignStatus;
import greencity.service.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@Controller
@AllArgsConstructor
@RequestMapping("/management/habits")
public class ManagementHabitController {
    private final ManagementHabitService managementHabitService;
    private final LanguageService languageService;
    private final HabitFactService habitFactService;
    private final ShoppingListItemService shoppingListItemService;
    private final HabitAssignService habitAssignService;

    /**
     * Returns management page with all {@link HabitVO}'s.
     *
     * @param model    {@link Model} that will be configured and returned to user.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     */

    @GetMapping
    @ApiPageable
    public String findAllHabits(Model model, @ApiIgnore Pageable pageable,
        @RequestParam(value = "searchReg", required = false) String searchReg,
        @RequestParam(value = "durationFrom", required = false) Integer durationFrom,
        @RequestParam(value = "durationTo", required = false) Integer durationTo,
        @RequestParam(value = "complexity", required = false) Integer complexity,
        @RequestParam(value = "withoutImage", required = false) Boolean withoutImage,
        @RequestParam(value = "withImage", required = false) Boolean withImage) {
        PageableDto<HabitManagementDto> allHabits = managementHabitService.getAllHabitsDto(searchReg,
            durationFrom, durationTo, complexity, withoutImage, withImage, pageable);
        model.addAttribute("pageable", allHabits);
        model.addAttribute("languages", languageService.getAllLanguages());
        return "core/management_user_habits";
    }

    /**
     * Method finds {@link HabitVO} with all translations by given id.
     *
     * @param id of {@link HabitVO}.
     * @return {@link HabitManagementDto}.
     */
    @ApiOperation(value = "Find habit by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitManagementDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{id}/find")
    public ResponseEntity<HabitManagementDto> getHabitById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(managementHabitService.getById(id));
    }

    /**
     * Returns management page with single {@link HabitVO}.
     *
     * @param id of {@link HabitVO}.
     * @return {@link HabitManagementDto}.
     * @author Vira Maksymets
     */
    @ApiOperation(value = "Find habit by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = HabitManagementDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/{id}")
    public String getHabitPage(@PathVariable("id") Long id,
        @ApiIgnore Pageable pageable,
        @ApiIgnore Locale locale, Model model) {
        model.addAttribute("hfacts", habitFactService.getAllHabitFactsVO(pageable));
        model.addAttribute("hshops", shoppingListItemService.getShoppingListByHabitId(id));
        model.addAttribute("habit", managementHabitService.getById(id));
        model.addAttribute("acquired",
            habitAssignService.getNumberHabitAssignsByHabitIdAndStatus(id, HabitAssignStatus.ACQUIRED));
        model.addAttribute("inProgress",
            habitAssignService.getNumberHabitAssignsByHabitIdAndStatus(id, HabitAssignStatus.INPROGRESS));
        model.addAttribute("canceled",
            habitAssignService.getNumberHabitAssignsByHabitIdAndStatus(id, HabitAssignStatus.CANCELLED));
        return "core/management_user_habit";
    }

    /**
     * Method saves {@link HabitVO} with translations.
     *
     * @param habitManagementDto {@link HabitManagementDto}.
     * @param bindingResult      {@link BindingResult}.
     * @param file               of {@link MultipartFile}.
     * @return {@link GenericResponseDto} with result of operation and errors
     *         fields.
     */
    @ApiOperation(value = "Save habit with translations.")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED, response = GenericResponseDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PostMapping("/save")
    public GenericResponseDto save(@Valid @RequestPart HabitManagementDto habitManagementDto,
        BindingResult bindingResult,
        @ImageValidation @RequestParam(required = false, name = "file") MultipartFile file) {
        if (!bindingResult.hasErrors()) {
            managementHabitService.saveHabitAndTranslations(habitManagementDto, file);
        }
        return GenericResponseDto.buildGenericResponseDto(bindingResult);
    }

    /**
     * Method updates {@link HabitVO} with translations.
     *
     * @param habitManagementDto {@link HabitManagementDto}.
     * @param bindingResult      {@link BindingResult}.
     * @param file               of {@link MultipartFile}.
     * @return {@link GenericResponseDto} with result of operation and errors
     *         fields.
     */
    @ApiOperation(value = "Update habit with translations.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = GenericResponseDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ResponseBody
    @PutMapping("/update")
    public GenericResponseDto update(@Valid @RequestPart HabitManagementDto habitManagementDto,
        BindingResult bindingResult,
        @ImageValidation @RequestParam(required = false, name = "file") MultipartFile file) {
        if (!bindingResult.hasErrors()) {
            managementHabitService.update(habitManagementDto, file);
        }
        return GenericResponseDto.buildGenericResponseDto(bindingResult);
    }

    /**
     * Method deletes {@link HabitVO} by id.
     *
     * @param id {@link HabitDto}'s id.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Delete habit by id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        managementHabitService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    /**
     * Method deletes all {@link HabitVO}'s by given id's.
     *
     * @param listId {@link List} of id's.
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Delete all habits by given id's.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        managementHabitService.deleteAll(listId);
        return ResponseEntity.status(HttpStatus.OK).body(listId);
    }
}
