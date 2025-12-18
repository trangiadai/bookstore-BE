package com.ctu.bookstore.repository.identity;

import com.ctu.bookstore.entity.identity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
