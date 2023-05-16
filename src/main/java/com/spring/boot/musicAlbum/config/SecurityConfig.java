package com.spring.boot.musicAlbum.config;
import com.spring.boot.musicAlbum.login.model.Account;
import com.spring.boot.musicAlbum.login.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration // 밑의 코드를 바탕으로 설정 진행
@EnableWebSecurity // 웹 설정
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //    @Autowired
//    private PasswordEncoder passwordEncoder;
    @Bean // 스프링 등록
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 해싱 (특정한 문구 -> 대응되는 문자열)
        // 로그인 기능 구현했습니다 -> Spring Security -> Bcrypt 해싱 -> Bcrypt (???)
    } // 순환 참조 2 : passwordEncoder를 다른 곳으로 옮기던가...

    // The dependencies of some of the beans in the application context form a cycle
    // 순환 참조
    // SecurityConfig -> LoginService
    // LoginService -> passwordEncoder(SecurityConfig)
//    @Autowired
//    private LoginService loginService;
    @Autowired // Service 대신에 Repository를 써서 순환 참조 방지
    private AccountRepository accountRepository;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**","/error","/favicon.ico");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 로그인 시에 로직 처리
        // 우리의 DB에 있는 유저(Account)를 쓰도록 연결
        auth.userDetailsService(
                // 유저를 구분할 수 있는 인증정보
                username -> {
//                    Account account = loginService.findUserByUsername(username);
                    Account account = accountRepository.findByUsername(username);
                    // 유저의 존재를 파악
                    if (account == null) { // 존재하지 않는다면 (Username에 맞는)
                        // UsernameNotFoundException <- Security가 가진 자체 에러
                        throw new UsernameNotFoundException("해당 유저가 없습니다");
                        // throw == return -> 처리되지 않으면 그 즉시 상위 메소드
                    }
                    // 유저가 있다
                    // 유저의 정보를 찾아서 return
                    // 우리가 만든 User가 아닌 Spring Security의 유저
                    return User.withUsername(username)
                            .password(account.getPassword()) // Lombok이 만들어준 getPassword
                            .roles(account.getRole())
                            .build(); // 위의 조건을 만족시키는 User를 만들어서 돌려주겠다 -> Session.
                }
        ).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 권한이나 보안 설정
                .authorizeRequests()
                .antMatchers("/login").permitAll() // 로그인 페이지는 모든 사용자에게 허용
                .antMatchers("/join").permitAll() // 가입 페이지는 모든 사용자에게 허용
//                .antMatchers("/bList").permitAll() // 가입 페이지는 모든 사용자에게 허용
                .antMatchers("/normal").hasAnyRole("normal", "admin") // 이것들 중에 특정한 권한이 있다면 접속 허용
                .antMatchers("/main").hasAnyRole("normal", "admin") // 이것들 중에 특정한 권한이 있다면 접속 허용
                .antMatchers("/admin").hasRole("admin") // 1개의 권한만 허용
                // 403 -> 권한이 없는 사람이 들어가려고 하면 403 에러
                .anyRequest().authenticated() // 나머지 요청은 인증(로그인만 의미)이 필요
                // 401 -> 로그인 안되어있으면 401 에러가 나고 있는데... Spring Security가 알아서 login 페이지로...
                .and()
                // ** 로그인을 처리하는 부분
                .formLogin()
                .loginPage("/login") // 커스텀 로그인 페이지 경로
                .defaultSuccessUrl("/home") // 로그인 성공 후 이동할 페이지 경로
                .failureUrl("/login-fail")
                // 로그인 실패 시 이동할 url을 입력
                .permitAll()
                .and()
                // ** 로그아웃을 처리하는 부분
                .logout()
                .logoutUrl("/logout") // post 요청을 하게 되면 처리
                .logoutSuccessUrl("/login") // 로그아웃 성공 후 이동할 페이지 경로
                // 1 : 어느경로로 로그아웃 요청을 받을 것인가?
                // 2 : 세션과 쿠키를 어떻게 할것인가?
                .invalidateHttpSession(true) // .invalidateHttpSession : 세션 비활성화
                .deleteCookies("JSESSIONID") // JSESSIONID : Java를 통해서 생성된 SESSION ID
                .permitAll()
                // 인가에 따른 (403) 문제를 처리
                .and()
                .exceptionHandling()
                .accessDeniedPage("/access-denied")
//                .accessDeniedHandler()
        ;
    }
}