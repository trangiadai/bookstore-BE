package com.ctu.bookstore.service.identity;

import com.ctu.bookstore.dto.request.identity.UserRequest;
import com.ctu.bookstore.dto.request.identity.UserUpdateRequest;
import com.ctu.bookstore.dto.respone.identity.UserRespone;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    private RoleRepository roleRepository;
    public UserRespone createUser(UserRequest userRequest){
        if (userRepository.existsByUsername(userRequest.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(userRequest);
        System.out.println("User trong CreateUser, userService: "+ user);
        System.out.println("Password TRƯỚC ENCODE: " + user.getPassword());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        System.out.println("Password SAU ENCODE: " + user.getPassword());
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        InforCheckout inforCheckout = InforCheckout.builder()
                .name(user.getLastname()+ user.getFirstname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .adress(user.getAdress())
                .voucher("bạn chưa nhập voucher")
                .note("bạn chưa có ghi chú cho đơn hàng")
                .build();
        user.setInforCheckout(inforCheckout);
//        user.setRoles(roles);

       return  userMapper.toUserRespone(userRepository.save(user));

    }
//    @PreAuthorize("hasRole('ADMIN8')"
    @PreAuthorize("hasAuthority('APPROVE_POST')")
    public List<UserRespone> getUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserRespone).toList();
    }
    public UserRespone getUser(String id){
        log.info("In method get user by Id");
        return userMapper.toUserRespone(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }
    @PostAuthorize("returnObject.username == authentication.name")
    public UserRespone getMyInfor(){
        SecurityContextHolder securityContextHolder = new SecurityContextHolder();
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        String n = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("name trong user service " + n);
        User user = userRepository.findByUsername(n).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));
        System.out.println("id user trong user service " + user.getId());
        return userMapper.toUserRespone(user);
    }

    public InforCheckout getInforCheckout(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(()-> new RuntimeException("Không tìm được user trong user service"));
        return user.getInforCheckout();
    }
    public UserRespone updateUser(String id, UserUpdateRequest req){

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

        if (req.getAdress() != null)
            user.setAdress(req.getAdress());
            inforCheckout.setAdress(req.getAdress());
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
