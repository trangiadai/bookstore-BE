package com.ctu.bookstore.controller.identity;

import com.ctu.bookstore.dto.request.identity.RoleRequest;
import com.ctu.bookstore.dto.respone.ApiRespone;
import com.ctu.bookstore.dto.respone.identity.RoleRespone;
import com.ctu.bookstore.service.identity.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiRespone<RoleRespone> create(@RequestBody RoleRequest request){
        return ApiRespone.<RoleRespone>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiRespone<List<RoleRespone>> getAll(){
        return ApiRespone.<List<RoleRespone>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiRespone<Void> delete(@PathVariable String role){
        roleService.delete(role);
        return ApiRespone.<Void>builder().build();
    }
}