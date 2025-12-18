package com.ctu.bookstore.repository.identity;

import com.ctu.bookstore.entity.identity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
