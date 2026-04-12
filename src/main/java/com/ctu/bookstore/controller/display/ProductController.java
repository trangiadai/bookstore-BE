package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.ProductRequestDTO;
import com.ctu.bookstore.dto.respone.ApiResponeDTO;
import com.ctu.bookstore.dto.respone.display.PageResponseDTO;
import com.ctu.bookstore.dto.respone.display.ProductResponseDTO;
import com.ctu.bookstore.entity.display.Product;
import com.ctu.bookstore.mapper.display.ProductMapper;
import com.ctu.bookstore.service.display.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/products")
public class ProductController {
    ProductService productService;
    ProductMapper productMapper ;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponeDTO<ProductResponseDTO> createProduct(@ModelAttribute ProductRequestDTO productRequestDTO
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        Product newProduct = productService.create(productRequestDTO);

        return ApiResponeDTO.<ProductResponseDTO>builder()
                .result(productMapper.toProductResponse(newProduct))
                .build();
    }

    @GetMapping
    public ApiResponeDTO<PageResponseDTO<ProductResponseDTO>> getAllProducts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size
    ){
        PageResponseDTO<ProductResponseDTO> products = productService.findAll(page, size);
        return ApiResponeDTO.<PageResponseDTO<ProductResponseDTO>>builder()
                .result(products)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponeDTO<ProductResponseDTO> getProductById(@PathVariable String id) {
        ProductResponseDTO product = productService.findById(id);
        return ApiResponeDTO.<ProductResponseDTO>builder()
                .result(product)
                .build();
    }

    // Nhớ chọn danh mục trước khi update, danh mục rổng là không update được
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponeDTO<ProductResponseDTO> updateProduct(
            @PathVariable String id,
            @ModelAttribute ProductRequestDTO productRequestDTO) throws IOException {

        ProductResponseDTO updatedProduct = productService.update(id, productRequestDTO);
        System.out.println(id);
        return ApiResponeDTO.<ProductResponseDTO>builder()
                .result(updatedProduct)
                .build();
    }

    @GetMapping("/filter-by-price")
    public ApiResponeDTO<PageResponseDTO<ProductResponseDTO>> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size

    ) {
        PageResponseDTO<ProductResponseDTO> result =
                productService.filterByPrice(minPrice, maxPrice, page, size);

        return ApiResponeDTO.<PageResponseDTO<ProductResponseDTO>>builder()
                .result(result)
                .build();
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        productService.delete(id);
    }

    @GetMapping("/filter-by-category")
    public ApiResponeDTO<PageResponseDTO<ProductResponseDTO>> filterByCategory(
            @RequestParam String categoryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size
    ) {
        PageResponseDTO<ProductResponseDTO> result =
                productService.filterByCategory(categoryId, page, size);

        return ApiResponeDTO.<PageResponseDTO<ProductResponseDTO>>builder()
                .result(result)
                .build();
    }


    // ⭐ Lọc theo rating: cho phép min-max, ví dụ: ?minStars=3&maxStars=5
    @GetMapping("/filter-by-rating")
    public ApiResponeDTO<PageResponseDTO<ProductResponseDTO>> filterByRating(
            @RequestParam(required = false) Double minStars,
            @RequestParam(required = false) Double maxStars,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        PageResponseDTO<ProductResponseDTO> result =
                productService.filterByRating(minStars, maxStars, page, size);

        return ApiResponeDTO.<PageResponseDTO<ProductResponseDTO>>builder()
                .result(result)
                .build();
    }

    // Nếu bạn muốn endpoint kiểu ">= X sao"
    @GetMapping("/filter-by-min-rating")
    public ApiResponeDTO<PageResponseDTO<ProductResponseDTO>> filterByMinRating(
            @RequestParam Double minStars,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        PageResponseDTO<ProductResponseDTO> result =
                productService.filterByMinRating(minStars, page, size);

        return ApiResponeDTO.<PageResponseDTO<ProductResponseDTO>>builder()
                .result(result)
                .build();
    }
}