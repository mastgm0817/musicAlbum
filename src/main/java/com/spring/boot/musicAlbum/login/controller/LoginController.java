package com.spring.boot.musicAlbum.login.controller;

import com.spring.boot.musicAlbum.board.model.BoardDTO;
import com.spring.boot.musicAlbum.login.model.Account;
import com.spring.boot.musicAlbum.login.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.s3.S3Client;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping
//@RequestMapping("/")
public class LoginController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private S3Client s3Client; // @Bean

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            Account account = accountService.findUserByUsername(username);
            model.addAttribute("account", account);
        }
        return "main";
    }

//    @GetMapping("/main")
//    public String main() {
//        return "main";
//    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/join")
    public String showJoinPage() {
        return "join";
    }

    @PostMapping("/join")
    public String join(
            @ModelAttribute Account account,
            @RequestParam("imageFile") MultipartFile imageFile
    ) throws Exception {
        // 가입 로직을 수행하고 결과를 처리하는 부분을 구현하세요.
        // account 객체에는 사용자로부터 입력받은 값들이 들어 있습니다.
        accountService.addAccount(account, imageFile);

        // 가입이 성공적으로 완료되었다면 다음 페이지로 이동하도록 설정합니다.
        return "redirect:/login";
    }


    @GetMapping("/normal")
    public String basic(Model model) {
        model.addAttribute("grade", "basic");
        return "movie";
    }

    @GetMapping("/admin")
    public String gold(Model model) {
        model.addAttribute("grade", "gold");
        return "movie";
    }

    @GetMapping("/login-fail")
    public String loginFail(Model model) {
        model.addAttribute("msg", "잘못된 Username 또는 비밀번호입니다");
        return "error";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("msg", "접속 권한이 없는 페이지입니다");
        return "error";
    }

    @GetMapping("/upload/image/{filename}")
    public ResponseEntity<?> upload(@PathVariable String filename) throws IOException {
        byte[] byteArray = accountService.loadFile(filename);
        return new ResponseEntity<>(byteArray, HttpStatus.OK);
    }

}