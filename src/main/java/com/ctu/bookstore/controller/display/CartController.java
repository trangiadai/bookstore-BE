package com.ctu.bookstore.controller.display;

import com.ctu.bookstore.dto.request.display.CartItemRequestDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.display.CartResponseDTO;
import com.ctu.bookstore.entity.identity.User;
import com.ctu.bookstore.entity.display.Cart;
import com.ctu.bookstore.mapper.display.CartMapper;
import com.ctu.bookstore.repository.identity.UserRepository;
import com.ctu.bookstore.repository.display.CartRepository;
import com.ctu.bookstore.service.display.CartService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/carts")
public class CartController {
    CartService cartService;
    UserRepository userRepository;
    CartRepository cartRepository;
    CartMapper cartMapper;

    @PostMapping
    public ApiResponseDTO<CartResponseDTO> addOrUpdateItem(@Valid @RequestBody CartItemRequestDTO request){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName).orElseThrow();
        String userId = user.getId();
        System.out.println("userID trong carService: "+ userId);
        Cart cart = cartService.addOrUpdateCart(userId,request);
        return  ApiResponseDTO.<CartResponseDTO>builder()
                .result(cartMapper.toCartResponse(cart))
                .build();
    }

    @GetMapping
    public ApiResponseDTO<CartResponseDTO> getMyCart(){
        return ApiResponseDTO.<CartResponseDTO>builder()
                .result(cartMapper.toCartResponse(cartService.getMyCart()))
                .build();
    }

    // This is delete CART not delete the item in the cart
    @DeleteMapping
    public void deleteCart(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()->new RuntimeException("khong có user trong cart controller"));
        Cart cart = cartRepository.findByUserId(user.getId()).orElseThrow(()->new RuntimeException("khong có user trong cart controller"));
        cartService.delete(cart.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") String itemId){
       cartService.deleteCartItem(itemId);
    }

    @GetMapping("/size")
    public ApiResponseDTO<Integer> sizeOfCart(){
        return ApiResponseDTO.<Integer>builder()
                .result(cartService.getSumMyCart())
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponseDTO<CartResponseDTO> incrementQuantity(@PathVariable("id") String itemId){

        return ApiResponseDTO.<CartResponseDTO>builder()
                .result(cartMapper.toCartResponse(cartService.incrementItem(itemId)))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponseDTO<CartResponseDTO> decrementQuantity(@PathVariable("id") String itemId ){

        return ApiResponseDTO.<CartResponseDTO>builder()
                .result(cartMapper.toCartResponse(cartService.decrementItem(itemId)))
                .build();
    }

}
