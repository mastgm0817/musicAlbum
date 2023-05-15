package com.spring.boot.musicAlbum.login.service;

import com.spring.boot.musicAlbum.login.model.Account;
import com.spring.boot.musicAlbum.login.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;



    public Account findUserByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    /*
        @Bean // 스프링 등록
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 해싱 (특정한 문구 -> 대응되는 문자열)
        // 로그인 기능 구현했습니다 -> Spring Security -> Bcrypt 해싱 -> Bcrypt (???)
        -> BCryptPasswordEncoder (등록)
    }
     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void join(Account account) throws Exception {
        // 중복이나 기타...
        // exists -> 존재하면 true, 없으면 false
        // find -> 존재하면 (..), 없으면 null -> != null => exists
//        if(account.getUsername().length() < 5) {
////            throw new Exception("5글자보다 긴 Username이어야 합니다");
//            // UsernameLengthException -> /join
////            throw new UsernameLengthException("5글자보다 긴 Username이어야 합니다");
//            throw new UsernameLengthException(5);
//        }
//        if(accountRepository.existsByUsername(account.getUsername())) {
//            // accountRepository.existsByUsername(account.getUsername()) -> username을 통해서 중복 확인
////            throw new Exception("중복된 Username 입니다");
//            // UniqueUsernameException -> /login
////            throw new UniqueUsernameException("중복된 Username 입니다");
//            throw new UniqueUsernameException();
//        }
        // Encoded password does not look like BCrypt (인코딩 안했을 때 오류)
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        // 일반적인 텍스트로 들어왔던 비밀번호를 bcrypt로 해싱
        accountRepository.save(account);
    }
}