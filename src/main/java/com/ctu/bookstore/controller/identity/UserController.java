package com.ctu.bookstore.controller.identity;

import com.ctu.bookstore.dto.request.identity.UserUpdateRequestDTO;
import com.ctu.bookstore.dto.respone.ApiResponeDTO;
import com.ctu.bookstore.dto.request.identity.UserRequestDTO;
import com.ctu.bookstore.dto.respone.identity.UserResponeDTO;
import com.ctu.bookstore.dto.respone.display.ProductResponseDTO;
import com.ctu.bookstore.dto.respone.payment.UserOrderResponseDTO;
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
    public ApiResponeDTO<UserResponeDTO> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO){
        ApiResponeDTO<User> apiResponeDTO = new ApiResponeDTO<>();
        return ApiResponeDTO.<UserResponeDTO>builder()
                .result(userService.createUser(userRequestDTO))
                .build();
    }

    @GetMapping
    ApiResponeDTO<List<UserResponeDTO>> getUsers(){
        return ApiResponeDTO.<List<UserResponeDTO>>builder()
                .result(userService.getUsers())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponeDTO<UserResponeDTO> getUser(@PathVariable("userId") String userId){
        return ApiResponeDTO.<UserResponeDTO>builder()
                .result(userService.getUser(userId))
                .build();
    }

    // User khác gì Infor?????????????????????????
    //??????????????????????????//

    @GetMapping("/info")
    ApiResponeDTO<UserResponeDTO> getInfor(){
        return ApiResponeDTO.<UserResponeDTO>builder()
                .result(userService.getMyInfor())
                .build();
    }

    @GetMapping("/info-checkout")
    ApiResponeDTO<InforCheckout> getInforCheckout(){
        return ApiResponeDTO.<InforCheckout>builder()
                .result(userService.getInforCheckout())
                .build();
    }

    @PutMapping("/{userId}")
    public ApiResponeDTO<UserResponeDTO> updateUser(@RequestBody UserUpdateRequestDTO userRequestBody ){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new RuntimeException("Khong tim thay user trong user controller"));
        return ApiResponeDTO.<UserResponeDTO>builder()
                .result(userService.updateUser(user.getId(),userRequestBody))
                .build();
    }

    @GetMapping("/orders")
    public ApiResponeDTO<List<UserOrderResponseDTO>> getAllUserOrder(){
        return ApiResponeDTO.<List<UserOrderResponseDTO>>builder()
                .result(userService.getAllOrders().stream()
                        .map(order -> userOrderMapper.toUserOrderResponse(order)).toList())
                .build();
    }

    @GetMapping("/productAllowComment")
    public ApiResponeDTO<List<ProductResponseDTO>> getProductAllowComment(){
        return  ApiResponeDTO.<List<ProductResponseDTO>>builder()
                .result(commentService.getAllProductAllowComment().stream()
                        .map(product -> productMapper.toProductResponse(product)).toList())
                .build();
    }
}
