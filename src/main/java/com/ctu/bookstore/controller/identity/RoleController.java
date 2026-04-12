package com.ctu.bookstore.controller.identity;

import com.ctu.bookstore.dto.request.identity.RoleRequestDTO;
import com.ctu.bookstore.dto.respone.ApiResponeDTO;
import com.ctu.bookstore.dto.respone.identity.RoleResponeDTO;
import com.ctu.bookstore.service.identity.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/roles")
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponeDTO<RoleResponeDTO> create(@RequestBody RoleRequestDTO request){
        return ApiResponeDTO.<RoleResponeDTO>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponeDTO<List<RoleResponeDTO>> getAll(){
        return ApiResponeDTO.<List<RoleResponeDTO>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponeDTO<Void> delete(@PathVariable String role){
        roleService.delete(role);
        return ApiResponeDTO.<Void>builder().build();
    }
}