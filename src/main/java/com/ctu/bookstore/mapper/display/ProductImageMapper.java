package com.ctu.bookstore.mapper.display;

import com.ctu.bookstore.dto.respone.display.ProductImageResponse;
import com.ctu.bookstore.entity.display.ProductImages;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageResponse toResponse(ProductImages productImages);
}