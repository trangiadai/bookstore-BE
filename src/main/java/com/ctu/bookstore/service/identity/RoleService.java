package com.ctu.bookstore.service.identity;

import com.ctu.bookstore.dto.request.identity.RoleRequest;
import com.ctu.bookstore.dto.respone.identity.RoleRespone;
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
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleRespone create(RoleRequest request){
        var role = roleMapper.toRole(request);
        System.out.println("role: "+role);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        System.out.println("permissions: "+permissions);
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        System.out.println("role: "+role);
        return roleMapper.toRoleRespone(role);
    }

    public List<RoleRespone> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleRespone)
                .toList();
    }

    public void delete(String role){
        roleRepository.deleteById(role);
    }
}