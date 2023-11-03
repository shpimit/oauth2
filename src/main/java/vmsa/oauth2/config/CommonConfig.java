package vmsa.oauth2.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("common")
public class CommonConfig {
    private String tokenType;
    private String clientId;
    private String clientSecret;
}
