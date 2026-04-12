package com.ctu.bookstore.mapper.display;

import com.ctu.bookstore.dto.request.display.ProductRequestDTO;
import com.ctu.bookstore.dto.respone.display.ProductResponseDTO;
import com.ctu.bookstore.entity.display.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring" ,uses = {ProductImageMapper.class})
public interface ProductMapper {
    // Không cần gán ID thủ công trong Service nếu dùng @GeneratedValue/Builder/PrePersist trong Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "imagesUrl", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toProduct(ProductRequestDTO request);
    @Mapping(target = "categoryName", source = "category.nameCategory")
    ProductResponseDTO toProductResponse(Product request);
}