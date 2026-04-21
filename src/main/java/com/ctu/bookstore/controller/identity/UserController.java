package com.ctu.bookstore.controller.identity;

import com.ctu.bookstore.dto.request.identity.UserUpdateRequestDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.request.identity.UserRequestDTO;
import com.ctu.bookstore.dto.response.identity.UserResponeDTO;
import com.ctu.bookstore.dto.response.display.ProductResponseDTO;
import com.ctu.bookstore.dto.response.payment.UserOrderResponseDTO;
import com.ctu.bookstore.entity.identity.User;
import com.ctu.bookstore.entity.payment.InforCheckout;
import com.ctu.bookstore.mapper.display.ProductMapper;
import com.ctu.bookstore.mapper.payment.UserOrderMapper;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.service.identity.UserService;
import com.ctu.bookstore.service.display.CommentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users")
public class UserController {
    UserService userService;
    UserRepository userRepository;
    UserOrderMapper userOrderMapper;
    CommentService commentService;
    ProductMapper productMapper;

    @PostMapping
    public ApiResponseDTO<UserResponeDTO> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO){
        ApiResponseDTO<User> apiResponseDTO = new ApiResponseDTO<>();
        return ApiResponseDTO.<UserResponeDTO>builder()
                .result(userService.createUser(userRequestDTO))
                .build();
    }

    @GetMapping
    ApiResponseDTO<List<UserResponeDTO>> getUsers(){
        return ApiResponseDTO.<List<UserResponeDTO>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponseDTO<UserResponeDTO> getMyInfo(){
        return ApiResponseDTO.<UserResponeDTO>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("/info-checkout")
    ApiResponseDTO<InforCheckout> getInforCheckout(){
        return ApiResponseDTO.<InforCheckout>builder()
                .result(userService.getInforCheckout())
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponseDTO<UserResponeDTO> updateUser(@RequestBody UserUpdateRequestDTO userRequestBody ){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new RuntimeException("Khong tim thay user trong user controller"));
        return ApiResponseDTO.<UserResponeDTO>builder()
                .result(userService.updateUser(user.getId(),userRequestBody))
                .build();
    }

    @GetMapping("/orders")
    public ApiResponseDTO<List<UserOrderResponseDTO>> getAllUserOrder(){
        return ApiResponseDTO.<List<UserOrderResponseDTO>>builder()
                .result(userService.getAllOrders().stream()
                        .map(order -> userOrderMapper.toUserOrderResponse(order)).toList())
                .build();
    }

    @GetMapping("/productAllowComment")
    public ApiResponseDTO<List<ProductResponseDTO>> getProductAllowComment(){
        return  ApiResponseDTO.<List<ProductResponseDTO>>builder()
                .result(commentService.getAllProductAllowComment().stream()
                        .map(product -> productMapper.toProductResponse(product)).toList())
                .build();
    }
}
