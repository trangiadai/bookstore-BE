package com.ctu.bookstore.service.display;

import com.ctu.bookstore.dto.request.display.CategoryRequestDTO;
import com.ctu.bookstore.entity.display.Category;
import com.ctu.bookstore.exception.AppException;
import com.ctu.bookstore.exception.ErrorCode;
import com.ctu.bookstore.mapper.display.CategoryMapper;
import com.ctu.bookstore.repository.display.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryRepository categoryRepository;
//    public Category create(CategoryRequest request){
//        Category category = categoryMapper.toCategory(request);
//
//        return categoryRepository.save(category);
//    }
    // Helper: Ném ngoại lệ khi không tìm thấy
    private Category findByIdOrThrow(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Category not found with id: " + id));
    }
    @Transactional
    public Category create(CategoryRequestDTO request){
        Category category = categoryMapper.toCategory(request);
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Category not found with id: " + request.getParentId()));

            category.setParentCategory(parent);
            // JPA tự quản lý quan hệ, nhưng để đồng bộ trong bộ nhớ:
            // parent.getChildCategory().add(category);
        }

        Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }
    @Transactional(readOnly = true)
    public Category getByID(String id){
        return  categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 🟡 2. Cập nhật danh mục
    @Transactional
    public Category updateCategory(String id, CategoryRequestDTO updatedCategory) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục có id = " + id));

        // Cập nhật thông tin
        existingCategory.setNameCategory(updatedCategory.getNameCategory());
//        existingCategory.setDescription(updatedCategory.getDescription());

        // Nếu bạn có danh mục cha
        Category parentCategory = categoryRepository.findById(updatedCategory.getParentId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục có id = " + id));;
        existingCategory.setParentCategory(parentCategory);

        return categoryRepository.save(existingCategory);
    }

    // 🔴 3. Xóa danh mục
    @Transactional
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục có id = " + id));

        // Nếu có danh mục con, bạn nên quyết định xử lý:
        // a. Xóa đệ quy
        // b. Hoặc set parentCategory = null
        if (category.getChildCategory() != null && !category.getChildCategory().isEmpty()) {
            for (Category sub : category.getChildCategory()) {
                sub.setParentCategory(null);
                categoryRepository.save(sub);
            }
        }

        categoryRepository.delete(category);
    }
//    public List<CategoryRespone> getAllCategories() { // Đổi đầu ra là DTO
//        List<Category> entities = categoryRepository.findAll();
//        // Sử dụng Mapper để ánh xạ danh sách
//
//        return entities.stream().map(categoryMapper::toCategoryRespone).toList();
//        // LƯU Ý: Nếu bạn muốn trả về cấu trúc CÂY, bạn sẽ cần thêm logic xây dựng cây TẠI ĐÂY
//    }
}
