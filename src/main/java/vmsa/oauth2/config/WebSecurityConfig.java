package vmsa.oauth2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import vmsa.oauth2.service.CustomAuthenticationProvider;
import vmsa.oauth2.service.impl.UserDetailsServiceImpl;

// Spring Security Config 설정
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

//    private final CustomAuthenticationProvider customAuthenticationProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

//    public WebSecurityConfig(CustomAuthenticationProvider customAuthenticationProvider) {
//        this.customAuthenticationProvider = customAuthenticationProvider;
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        logger.info("############################################################################");
        logger.info("WebSecurityConfiguration = AuthenticationManagerBuilder configure ");
        // UserDetailService로 가져오기
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());

//        auth.authenticationProvider(customAuthenticationProvider);
    }

    @Bean
    // BCryptPasswordEncoder는 Spring Security에서 제공하는 비밀번호 암호화 객체입니다.
    // Service에서 비밀번호를 암호화할 수 있도록 Bean으로 등록합니다.
    public static PasswordEncoder passwordEncoder() {
        // Spring5부터 PasswordEncoder 지정은 필수로 진행해주어야 합니다.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests().antMatchers("/oauth/**",
                "/oauth/token",
                "/oauth2/callback").permitAll()
                .and()
                    .formLogin()
//                        .loginPage("http://www.vaatz.com")
                .and()
                .httpBasic();
    }
}