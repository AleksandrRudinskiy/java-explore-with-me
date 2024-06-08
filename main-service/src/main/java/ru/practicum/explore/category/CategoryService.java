package ru.practicum.explore.category;

import ru.practicum.explore.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(CategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long id);

    void deleteCategoryById(long id);

    CategoryDto patchCategoryById(CategoryDto categoryDto, long id);

}
