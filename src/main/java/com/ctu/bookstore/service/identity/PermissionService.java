package com.ctu.bookstore.service.identity;

import com.ctu.bookstore.dto.request.identity.PermissionRequestDTO;
import com.ctu.bookstore.dto.response.identity.PermissionResponseDTO;
import com.ctu.bookstore.entity.identity.Permission;
import com.ctu.bookstore.mapper.identity.PermissionMapper;
import com.ctu.bookstore.repository.identity.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponseDTO create(PermissionRequestDTO request){
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionRespone(permission);
    }

    public List<PermissionResponseDTO> getAll(){
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionRespone).toList();
    }

    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }
}