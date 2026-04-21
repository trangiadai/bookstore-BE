package com.ctu.bookstore.service.identity;

import com.ctu.bookstore.dto.request.identity.UserRequestDTO;
import com.ctu.bookstore.dto.request.identity.UserUpdateRequestDTO;
import com.ctu.bookstore.dto.response.identity.UserResponeDTO;
import com.ctu.bookstore.entity.identity.User;
//import com.ctu.bookstore.entity.identity.Role;
import com.ctu.bookstore.entity.payment.InforCheckout;
import com.ctu.bookstore.entity.payment.UserOrder;
import com.ctu.bookstore.enums.Role;
import com.ctu.bookstore.exception.AppException;
import com.ctu.bookstore.exception.ErrorCode;
import com.ctu.bookstore.mapper.identity.RoleMapper;
import com.ctu.bookstore.mapper.identity.UserMapper;
import com.ctu.bookstore.repository.identity.RoleRepository;
import com.ctu.bookstore.repository.identity.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleMapper roleMapper;
    RoleRepository roleRepository;

    public UserResponeDTO createUser(UserRequestDTO userRequestDTO){
        if (userRepository.existsByUsername(userRequestDTO.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(userRequestDTO);
        System.out.println("User trong CreateUser, userService: "+ user);
        System.out.println("Password TRƯỚC ENCODE: " + user.getPassword());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        System.out.println("Password SAU ENCODE: " + user.getPassword());
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        InforCheckout inforCheckout = InforCheckout.builder()
                .name(user.getLastname()+ user.getFirstname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .voucher("bạn chưa nhập voucher")
                .note("bạn chưa có ghi chú cho đơn hàng")
                .build();
        user.setInforCheckout(inforCheckout);
        user.setRoles(roles);

       return  userMapper.toUserRespone(userRepository.save(user));

    }

    @PreAuthorize("hasRole('ADMIN')") //Tạo 1 cái proxy ngay trước cái method này, kiểm tra trước lúc gọi mehtod phải có role là ADMIN thì mới gọi đc
    public List<UserResponeDTO> getUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserRespone).toList();
    }

    // Tạo 1 cái proxy, cho phép gọi hàm trước, chạy xong sau đó nếu thỏa đều kiện proxy thì mới cho return còn không thì sẽ chặn lại
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponeDTO getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserRespone(user);
    }

    public InforCheckout getInforCheckout(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new RuntimeException("Không tìm được user trong user service"));
        return user.getInforCheckout();
    }

    public UserResponeDTO updateUser(String id, UserUpdateRequestDTO req){

         User user = userRepository.findById(id)
                 .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        InforCheckout inforCheckout = user.getInforCheckout();
        if (req.getUsername() != null)
            user.setUsername(req.getUsername());

        if (req.getFirstname() != null)
            user.setFirstname(req.getFirstname());

        if (req.getLastname() != null)
            user.setLastname(req.getLastname());

        if (req.getFirstname() != null && req.getLastname() != null)
            inforCheckout.setName(req.getLastname() + req.getFirstname());
        if (req.getGender() != null)
            user.setGender(req.getGender());

        if (req.getDob() != null)
            user.setDob(req.getDob());
        if (req.getPhoneNumber() != null)
            user.setPhoneNumber(req.getPhoneNumber());
            inforCheckout.setPhoneNumber((req.getPhoneNumber()));

        if (req.getAvatar() != null)
            user.setAvatar(req.getAvatar());

        if (req.getEmail() != null)
            user.setEmail(req.getEmail());
            inforCheckout.setEmail(req.getEmail());

        if (req.getAddress() != null)
            user.setAddress(req.getAddress());
            inforCheckout.setAddress(req.getAddress());
//        userMapper.updateUser(user,req);

//         user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));

        // Password chỉ update nếu không null
        if (req.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
//         var roles = roleRepository.findAllById(userUpdateRequest.getRoles());
        // Roles cũng vậy
        if (req.getRoles() != null) {
            var roles = roleRepository.findAllById(req.getRoles());
            user.setRoles(new HashSet<>(roles));
        }

//         user.setRoles(new HashSet<>(roles));
        System.out.println("User trong updateUser UserService: "+ user);
         return userMapper.toUserRespone(userRepository.save(user));


    }

    public void updateInforCheckout(InforCheckout inforCheckout){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(name);
        user.get().setInforCheckout(inforCheckout);
        userRepository.save(user.get());
    }

    public Set<UserOrder> getAllOrders(){
        var name = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByUsername(name);
        System.out.println("order trong User service: "+ user.get().getUserOrders()
                .stream().findFirst().get().getOrderItems()
                .stream().findFirst().get().getProduct().getId());
        return user.get().getUserOrders();
    }

}
