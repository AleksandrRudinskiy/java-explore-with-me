package ru.practicum.explore.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.category.dto.CategoryDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("POST-add new category {}", categoryDto);
        return categoryService.addCategory(categoryDto);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        log.info("GET all categories");
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto getCategoryById(@PathVariable long id) {
        log.info("GET-category by id {}", id);
        return categoryService.getCategoryById(id);
    }

    @PatchMapping("/admin/categories/{id}")
    public CategoryDto patchCategoryById(@RequestBody @Valid CategoryDto categoryDto,
                                         @PathVariable long id) {
        return categoryService.patchCategoryById(categoryDto, id);
    }

    @DeleteMapping("/admin/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable long id) {
        log.info("DELETE category by id {}", id);
        categoryService.deleteCategoryById(id);
    }
}
