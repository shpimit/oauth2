package vmsa.oauth2.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import vmsa.oauth2.service.impl.UserDetailsServiceImpl;
import vmsa.oauth2.vo.TokenTypeEnum;

import javax.sql.DataSource;

@Configuration
//@PropertySource("classpath:application.yml")
@RequiredArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final CommonConfig commonConfig;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /**
         * DB 이용 방식 - 관련 테이블이 미리 생성되어 있어야 한다.
         * oauth_client_details 테이블에 등록된 사용자로 조회합니다.
         **/
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        /**
         * Token 정보를 DB에 저장하는 로직
         * 로그인 시점에 토큰과 관련 정보들이 잡히게 된다.
         *
         * oauth_access_token 과 같은 테이블이 미리 생성되어 있어야 한다.
         **/
        switch (TokenTypeEnum.valueOf(commonConfig.getTokenType())){
            case BEARER:
                endpoints.tokenStore(new JdbcTokenStore(dataSource))
                        .userDetailsService(userDetailsService);
                break;
            case JWT:
                super.configure(endpoints);
                endpoints.accessTokenConverter(jwtAccessTokenConverter())
                        .tokenStore(new JwtTokenStore(jwtAccessTokenConverter()))
                        .userDetailsService(userDetailsService);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + TokenTypeEnum.valueOf(commonConfig.getTokenType()));
        }
    }

    // Resource 서버에서 보낸  Token 검증 요청을 처리하는 로직
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        System.out.println("##### token check");
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()") //allow check token
                .allowFormAuthenticationForClients();
    }

    // JWT를 사용할경우 Token 자체로 인증정보가 관리 되므로 token 정보를 관리한는 Table은 사용하지 않게 됩니다.
    // https://daddyprogrammer.org/post/1287/spring-oauth2-authorizationserver-database/
    // 대칭키 사용 : https://daddyprogrammer.org/post/1890/spring-boot-oauth2-resourceserver-asymmetric-keys-to-do-the-signing-process/
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new FileSystemResource("src/main/resources/oauth2jwt.jks"), "oauth2jwtpass".toCharArray());
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("oauth2jwt"));
        return converter;
    }

//    @Value("${security.oauth2.jwt.signkey}")
//    private String signKey;
//
//    @Bean
//    public JwtAccessTokenConverter jwtAccessTokenConverter() {
//        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
//        converter.setSigningKey(signKey);
//        return converter;
//    }
}
