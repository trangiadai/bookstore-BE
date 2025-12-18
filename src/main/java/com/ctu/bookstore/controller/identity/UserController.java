package com.ctu.bookstore.controller.identity;

import com.ctu.bookstore.dto.request.identity.UserUpdateRequest;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.request.identity.UserRequest;
import com.ctu.bookstore.dto.respone.identity.UserRespone;
import com.ctu.bookstore.dto.respone.display.ProductResponse;
import com.ctu.bookstore.dto.respone.payment.UserOrderResponse;
import com.ctu.bookstore.entity.identity.User;
import com.ctu.bookstore.entity.payment.InforCheckout;
import com.ctu.bookstore.mapper.identity.UserMapper;
import com.ctu.bookstore.mapper.display.ProductMapper;
import com.ctu.bookstore.mapper.payment.UserOrderMapper;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.service.identity.UserService;
import com.ctu.bookstore.service.display.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserOrderMapper userOrderMapper;
    @Autowired
    CommentService commentService;
    @Autowired
    ProductMapper productMapper;
//    @PostMapping
//    public ApiRespone<UserRespone> createUser(@RequestBody @Valid UserRequest userRequest){
//
//         return ApiRespone.<UserRespone>builder()
//                 .result(userMapper.toUserRespone(userService.createUser(userRequest)))
//                 .build();
//    }
    @PostMapping
    public ApiRespone<UserRespone> createUser(@RequestBody @Valid UserRequest userRequest){
        ApiRespone<User> apiRespone = new ApiRespone<>();


        return ApiRespone.<UserRespone>builder()
                .result(userService.createUser(userRequest))
                .build();
    }
    @GetMapping
    ApiRespone<List<UserRespone>> getUsers(){
        return ApiRespone.<List<UserRespone>>builder()
                .result(userService.getUsers())
                .build();
    }
    @GetMapping("/{userId}")
    ApiRespone<UserRespone> getUser(@PathVariable("userId") String userId){
        return ApiRespone.<UserRespone>builder()
                .result(userService.getUser(userId))
                .build();
    }
    @GetMapping("/info")
    ApiRespone<UserRespone> getInfor(){
        return ApiRespone.<UserRespone>builder()
                .result(userService.getMyInfor())
                .build();
    }
    @GetMapping("/info-checkout")
    ApiRespone<InforCheckout> getInforCheckout(){
        return ApiRespone.<InforCheckout>builder()
                .result(userService.getInforCheckout())
                .build();
    }
    @PutMapping("/{userId}")
    public ApiRespone<UserRespone> updateUser(@RequestBody UserUpdateRequest userRequestBody ){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new RuntimeException("Khong tim thay user trong user controller"));
        return ApiRespone.<UserRespone>builder()
                .result(userService.updateUser(user.getId(),userRequestBody))
                .build();
    }
    @GetMapping("/orders")
    public ApiRespone<List<UserOrderResponse>> getAllUserOrder(){
        return ApiRespone.<List<UserOrderResponse>>builder()
                .result(userService.getAllOrders().stream()
                        .map(order -> userOrderMapper.toUserOrderResponse(order)).toList())
                .build();
    }
    @GetMapping("/productAllowComment")
    public ApiRespone<List<ProductResponse>> getProductAllowComment(){
        return  ApiRespone.<List<ProductResponse>>builder()
                .result(commentService.getAllProductAllowComment().stream()
                        .map(product -> productMapper.toProductResponse(product)).toList())
                .build();
    }
}
