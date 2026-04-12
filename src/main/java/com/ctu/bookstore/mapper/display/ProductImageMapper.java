package com.ctu.bookstore.mapper.display;

import com.ctu.bookstore.dto.respone.display.ProductImageResponseDTO;
import com.ctu.bookstore.entity.display.ProductImages;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageResponseDTO toResponse(ProductImages productImages);
}