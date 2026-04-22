package com.ctu.bookstore.service.identity;

import com.ctu.bookstore.dto.request.identity.RoleRequestDTO;
import com.ctu.bookstore.dto.response.identity.RoleResponseDTO;
import com.ctu.bookstore.mapper.identity.RoleMapper;
import com.ctu.bookstore.repository.identity.PermissionRepository;
import com.ctu.bookstore.repository.identity.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponseDTO create(RoleRequestDTO request){
        var role = roleMapper.toRole(request);
        // sử lý map permission dạng String -> dạng Permission
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        role = roleRepository.save(role);

        return roleMapper.toRoleRespone(role);
    }

    public List<RoleResponseDTO> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleRespone)
                .toList();
    }

    public void delete(String role){
        roleRepository.deleteById(role);
    }
}