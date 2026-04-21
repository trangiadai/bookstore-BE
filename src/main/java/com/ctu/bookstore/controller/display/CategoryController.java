package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.CategoryRequestDTO;
import com.ctu.bookstore.dto.response.display.CategoryResponseDTO;
import com.ctu.bookstore.entity.display.Category;
import com.ctu.bookstore.mapper.display.CategoryMapper;
import com.ctu.bookstore.service.display.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Refactorrrrrrrrrrrrrrrrrr
//rrrrrrrrrrrrrrrrrrrr
//rrrrrrrrrrrrrrrrr
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/category")
public class CategoryController {
    CategoryService categoryService;
    CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO category) {
        Category createdCategory = categoryService.create(category);

        CategoryResponseDTO categoryResponseDTO = categoryMapper.toCategoryRespone(createdCategory);
//        categoryRespone.setChildren(createdCategory.getChildCategory());
        return new ResponseEntity<CategoryResponseDTO>(categoryResponseDTO, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getByID(@PathVariable("id") String categoryId) {
        Category category = categoryService.getByID(categoryId);

        // Tạo bản sao để tránh lỗi chỉnh sửa đồng thời
        Set<Category> childrenCopy = new HashSet<>(category.getChildCategory());
        Set<CategoryResponseDTO> childrenRespones = categoryMapper.toCategoryResponeSet(childrenCopy);

        CategoryResponseDTO categoryResponseDTO = categoryMapper.toCategoryRespone(category);
        categoryResponseDTO.setChildren(childrenRespones);

        return ResponseEntity.ok(categoryResponseDTO);
    }
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        // Map sang DTO response
        List<CategoryResponseDTO> responses = categories.stream()
                .map(categoryMapper::toCategoryRespone)
                .toList();

        return ResponseEntity.ok(responses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable("id") String id,
            @Valid @RequestBody CategoryRequestDTO updatedRequest) {

        Category updatedCategory = categoryService.updateCategory(id, updatedRequest);
        CategoryResponseDTO response = categoryMapper.toCategoryRespone(updatedCategory);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
