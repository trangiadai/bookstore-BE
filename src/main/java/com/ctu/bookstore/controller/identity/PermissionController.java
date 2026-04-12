package com.ctu.bookstore.controller.identity;


import com.ctu.bookstore.dto.request.identity.PermissionRequestDTO;
import com.ctu.bookstore.dto.respone.ApiResponeDTO;
import com.ctu.bookstore.dto.respone.identity.PermissionResponeDTO;
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
    ApiResponeDTO<PermissionResponeDTO> create(@RequestBody PermissionRequestDTO request){
        return ApiResponeDTO.<PermissionResponeDTO>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponeDTO<List<PermissionResponeDTO>> getAll(){
        return ApiResponeDTO.<List<PermissionResponeDTO>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponeDTO<Void> delete(@PathVariable String permission){
        permissionService.delete(permission);
        return ApiResponeDTO.<Void>builder().build();
    }
}