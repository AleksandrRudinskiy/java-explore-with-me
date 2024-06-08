package ru.practicum.explore.category;

import lombok.experimental.UtilityClass;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.model.Category;

@UtilityClass
public class CategoryMapper {

    public static CategoryDto convertToCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category convertToCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }


}
