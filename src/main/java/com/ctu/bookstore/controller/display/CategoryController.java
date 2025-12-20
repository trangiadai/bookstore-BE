package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.CategoryRequest;
import com.ctu.bookstore.dto.respone.display.CategoryRespone;
import com.ctu.bookstore.entity.display.Category;
import com.ctu.bookstore.mapper.display.CategoryMapper;
import com.ctu.bookstore.service.display.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//Refactorrrrrrrrrrrrrrrrrr
//rrrrrrrrrrrrrrrrrrrr
//rrrrrrrrrrrrrrrrr
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/category")
public class CategoryController {
    CategoryService categoryService;
    CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryRespone> createCategory(@RequestBody CategoryRequest category) {
        Category createdCategory = categoryService.create(category);

        CategoryRespone categoryRespone = categoryMapper.toCategoryRespone(createdCategory);
//        categoryRespone.setChildren(createdCategory.getChildCategory());
        return new ResponseEntity<CategoryRespone>(categoryRespone, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryRespone> getByID(@PathVariable("id") String categoryId) {
        Category category = categoryService.getByID(categoryId);

        // Tạo bản sao để tránh lỗi chỉnh sửa đồng thời
        Set<Category> childrenCopy = new HashSet<>(category.getChildCategory());
        Set<CategoryRespone> childrenRespones = categoryMapper.toCategoryResponeSet(childrenCopy);

        CategoryRespone categoryRespone = categoryMapper.toCategoryRespone(category);
        categoryRespone.setChildren(childrenRespones);

        return ResponseEntity.ok(categoryRespone);
    }
    @GetMapping
    public ResponseEntity<List<CategoryRespone>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        // Map sang DTO response
        List<CategoryRespone> responses = categories.stream()
                .map(categoryMapper::toCategoryRespone)
                .toList();

        return ResponseEntity.ok(responses);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoryRespone> updateCategory(
            @PathVariable("id") String id,
            @RequestBody CategoryRequest updatedRequest) {

        Category updatedCategory = categoryService.updateCategory(id, updatedRequest);
        CategoryRespone response = categoryMapper.toCategoryRespone(updatedCategory);

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
