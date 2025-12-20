package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.ProductRequest;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.respone.display.PageResponse;
import com.ctu.bookstore.dto.respone.display.ProductResponse;
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
    public ApiRespone<ProductResponse> createProduct(@ModelAttribute ProductRequest productRequest
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        Product newProduct = productService.create(productRequest);

        return ApiRespone.<ProductResponse>builder()
                .result(productMapper.toProductResponse(newProduct))
                .build();
    }

    @GetMapping
    public ApiRespone<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size
    ){
        PageResponse<ProductResponse> products = productService.findAll(page, size);
        return ApiRespone.<PageResponse<ProductResponse>>builder()
                .result(products)
                .build();
    }

    @GetMapping("/{id}")
    public ApiRespone<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse product = productService.findById(id);
        return ApiRespone.<ProductResponse>builder()
                .result(product)
                .build();
    }

    // NHớ chọn danh mục trước khi update, danh mục rổng là không update được
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiRespone<ProductResponse> updateProduct(
            @PathVariable String id,
            @ModelAttribute ProductRequest productRequest) throws IOException {

        ProductResponse updatedProduct = productService.update(id, productRequest);
        System.out.println(id);
        return ApiRespone.<ProductResponse>builder()
                .result(updatedProduct)
                .build();
    }
    @GetMapping("/filter-by-price")
    public ApiRespone<PageResponse<ProductResponse>> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size

    ) {
        PageResponse<ProductResponse> result =
                productService.filterByPrice(minPrice, maxPrice, page, size);

        return ApiRespone.<PageResponse<ProductResponse>>builder()
                .result(result)
                .build();
    }
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id){
        productService.delete(id);
    }

    @GetMapping("/filter-by-category")
    public ApiRespone<PageResponse<ProductResponse>> filterByCategory(
            @RequestParam String categoryId,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "6") int size
    ) {
        PageResponse<ProductResponse> result =
                productService.filterByCategory(categoryId, page, size);

        return ApiRespone.<PageResponse<ProductResponse>>builder()
                .result(result)
                .build();
    }


    // ⭐ Lọc theo rating: cho phép min-max, ví dụ: ?minStars=3&maxStars=5
    @GetMapping("/filter-by-rating")
    public ApiRespone<PageResponse<ProductResponse>> filterByRating(
            @RequestParam(required = false) Double minStars,
            @RequestParam(required = false) Double maxStars,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        PageResponse<ProductResponse> result =
                productService.filterByRating(minStars, maxStars, page, size);

        return ApiRespone.<PageResponse<ProductResponse>>builder()
                .result(result)
                .build();
    }

    // Nếu bạn muốn endpoint kiểu ">= X sao"
    @GetMapping("/filter-by-min-rating")
    public ApiRespone<PageResponse<ProductResponse>> filterByMinRating(
            @RequestParam Double minStars,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        PageResponse<ProductResponse> result =
                productService.filterByMinRating(minStars, page, size);

        return ApiRespone.<PageResponse<ProductResponse>>builder()
                .result(result)
                .build();
    }
}