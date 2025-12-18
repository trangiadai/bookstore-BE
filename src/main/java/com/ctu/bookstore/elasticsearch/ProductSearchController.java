package com.ctu.bookstore.elasticsearch;

import com.ctu.bookstore.entity.display.Product;
import com.ctu.bookstore.repository.display.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;
    private final ProductRepository productRepository;

    // === 1) Search sản phẩm theo tên ===
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

    // === 3) Đồng bộ 1 sản phẩm theo ID ===
    // HIỆN TẠI KHÔNG DÙNG TỚI VÌ KHI TẠO PRODUCT THÌ TRONG SERVICE CỦA PRODUCT ĐÃ CÓ GỌI METHOD TỰ ĐỒNG BỘ LÊN ELASTIC
    @PostMapping("/index/{id}")
    public String indexOne(@PathVariable String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));

        productSearchService.indexProduct(product);
        return "Indexed product id = " + id;
    }
}
