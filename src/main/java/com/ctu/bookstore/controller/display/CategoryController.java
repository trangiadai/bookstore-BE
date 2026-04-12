package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.CategoryRequestDTO;
import com.ctu.bookstore.dto.respone.display.CategoryResponeDTO;
import com.ctu.bookstore.entity.display.Category;
import com.ctu.bookstore.mapper.display.CategoryMapper;
import com.ctu.bookstore.service.display.CategoryService;
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
    public ResponseEntity<CategoryResponeDTO> createCategory(@RequestBody CategoryRequestDTO category) {
        Category createdCategory = categoryService.create(category);

        CategoryResponeDTO categoryResponeDTO = categoryMapper.toCategoryRespone(createdCategory);
//        categoryRespone.setChildren(createdCategory.getChildCategory());
        return new ResponseEntity<CategoryResponeDTO>(categoryResponeDTO, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponeDTO> getByID(@PathVariable("id") String categoryId) {
        Category category = categoryService.getByID(categoryId);

        // Tạo bản sao để tránh lỗi chỉnh sửa đồng thời
        Set<Category> childrenCopy = new HashSet<>(category.getChildCategory());
        Set<CategoryResponeDTO> childrenRespones = categoryMapper.toCategoryResponeSet(childrenCopy);

        CategoryResponeDTO categoryResponeDTO = categoryMapper.toCategoryRespone(category);
        categoryResponeDTO.setChildren(childrenRespones);

        return ResponseEntity.ok(categoryResponeDTO);
    }
    @GetMapping
    public ResponseEntity<List<CategoryResponeDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        // Map sang DTO response
        List<CategoryResponeDTO> responses = categories.stream()
                .map(categoryMapper::toCategoryRespone)
                .toList();

        return ResponseEntity.ok(responses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponeDTO> updateCategory(
            @PathVariable("id") String id,
            @RequestBody CategoryRequestDTO updatedRequest) {

        Category updatedCategory = categoryService.updateCategory(id, updatedRequest);
        CategoryResponeDTO response = categoryMapper.toCategoryRespone(updatedCategory);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
