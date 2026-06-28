package com.ctu.bookstore.elasticsearch;

import com.ctu.bookstore.entity.display.Product;
import com.ctu.bookstore.repository.display.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/search")
public class ProductSearchController {
    ProductSearchService productSearchService;
    ProductRepository productRepository;

    @GetMapping
    public List<ProductDocument> search(@RequestParam String keyword) {
        return productSearchService.searchByName(keyword);
    }

    // === 2) Đồng bộ toàn bộ sản phẩm của DB lên ES ===
    @PostMapping("/sync")
    public String syncAll() {
        List<Product> allProducts = productRepository.findAll();
        productSearchService.syncAllProducts(allProducts);
        return "Đồng bộ " + allProducts.size() + " sản phẩm lên Elasticsearch thành công!";
    }

    @PostMapping("/index/{id}")
    public String indexOne(@PathVariable String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));

        productSearchService.indexProduct(product);
        return "Indexed product id = " + id;
    }
}
