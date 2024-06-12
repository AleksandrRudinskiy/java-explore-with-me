package ru.practicum.explore.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.category.dto.CategoryDto;
import ru.practicum.explore.category.model.Category;
import ru.practicum.explore.common.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        return CategoryMapper.convertToCategoryDto(categoryRepository.save(
                CategoryMapper.convertToCategory(categoryDto))
        );
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new RuntimeException("Параметр from не должен быть меньше 1");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return categoryRepository.findAllCategories(page).stream()
                .map(CategoryMapper::convertToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Не найден id =" + id);
        } else {

            return CategoryMapper.convertToCategoryDto(
                    categoryRepository.findById(id).get());
        }
    }

    @Override
    public void deleteCategoryById(long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Не найден id =" + id);
        } else {
            categoryRepository.deleteById(id);
        }
    }

    @Override
    public CategoryDto patchCategoryById(CategoryDto categoryDto, long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Не найден id =" + id);
        }
        Category oldCategory = categoryRepository.findById(id).get();
        oldCategory.setName(categoryDto.getName());
        Category patchedCategory = categoryRepository.save(oldCategory);
        return CategoryMapper.convertToCategoryDto(patchedCategory);
    }
}
