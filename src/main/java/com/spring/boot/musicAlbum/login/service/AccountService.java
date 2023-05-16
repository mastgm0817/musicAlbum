package com.spring.boot.musicAlbum.login.service;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.login.model.Account;
import com.spring.boot.musicAlbum.login.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private S3Client s3Client; // @Bean
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Account findUserByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public void join(Account account) throws Exception {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepository.save(account);
    }

    public Account addAccount(Account account, MultipartFile imageFile) throws IOException {
        String bucketName = "project-profile-image";
        if (imageFile != null) {
            // 새로운 파일 이름 - 파일이 겹칠 수 있으니까
            String newFileName = System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
            // 업로드를 받을 폴더 이름 -> 새로운 파일이 들어갈 최종 경로
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .build();
            s3Client.putObject(request, RequestBody.fromBytes(imageFile.getBytes()));
            account.setImage(newFileName); // db에 저장하기 위한 새로운 파일 이름
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public byte[] loadFile(String key) throws IOException {
        String bucketName = "project-profile-image";
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        return s3Client.getObject(request).readAllBytes();
    }


    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findAccountById(id);
    }


}