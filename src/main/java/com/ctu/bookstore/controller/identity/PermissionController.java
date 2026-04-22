package com.ctu.bookstore.controller.identity;


import com.ctu.bookstore.dto.request.identity.PermissionRequestDTO;
import com.ctu.bookstore.dto.response.ApiResponseDTO;
import com.ctu.bookstore.dto.response.identity.PermissionResponseDTO;
import com.ctu.bookstore.service.identity.PermissionService;
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
@RequestMapping("/permissions")
public class PermissionController {
    PermissionService permissionService;

    @PostMapping
    ApiResponseDTO<PermissionResponseDTO> create(@RequestBody PermissionRequestDTO request){
        return ApiResponseDTO.<PermissionResponseDTO>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponseDTO<List<PermissionResponseDTO>> getAll(){
        return ApiResponseDTO.<List<PermissionResponseDTO>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponseDTO<Void> delete(@PathVariable String permission){
        permissionService.delete(permission);
        return ApiResponseDTO.<Void>builder().build();
    }
}