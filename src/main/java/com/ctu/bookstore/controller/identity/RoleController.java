package com.ctu.bookstore.controller.identity;

import com.ctu.bookstore.dto.request.identity.RoleRequestDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.identity.RoleResposeDTO;
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
    ApiResponseDTO<RoleResposeDTO> create(@RequestBody RoleRequestDTO request){
        return ApiResponseDTO.<RoleResposeDTO>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    ApiResponseDTO<List<RoleResposeDTO>> getAll(){
        return ApiResponseDTO.<List<RoleResposeDTO>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponseDTO<Void> delete(@PathVariable String role){
        roleService.delete(role);
        return ApiResponseDTO.<Void>builder().build();
    }
}