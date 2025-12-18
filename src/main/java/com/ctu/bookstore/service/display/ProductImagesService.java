package com.ctu.bookstore.service.display;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ctu.bookstore.entity.display.ProductImages;
import com.ctu.bookstore.repository.display.ProductImagesRepository;
import com.ctu.bookstore.repository.display.ProductImagesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ProductImagesService {
    private final Cloudinary cloudinary;
    private final ProductImagesRepository productImagesRepository;

    public String uploadImage(MultipartFile file) throws IOException {
        String publicId = UUID.randomUUID().toString();

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", publicId,
                        "folder", "bookstore-images" // Tùy chọn: Đặt ảnh vào một folder cụ thể trên Cloudinary
                )
        );


        String imageUrl = (String) uploadResult.get("secure_url");

        String imageId = publicId;

//        ProductImages newImage = ProductImages.builder()
//                .id(imageId) // Dùng publicId làm ID trong database
//                .Url(imageUrl)
//                .build();

        return imageUrl;
    }


    public void deleteImage(String imageId) throws IOException {

        cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());

        productImagesRepository.deleteById(imageId);
    }

}