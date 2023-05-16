package com.spring.boot.musicAlbum.login.repository;

import com.spring.boot.musicAlbum.login.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username); // JPA 메소드 생성규칙
    Account findAccountById(Long id);
    boolean existsByUsername(String username); // JPA 메소드 생성규칙
}
