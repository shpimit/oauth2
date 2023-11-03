package vmsa.oauth2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DataInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

        PasswordEncoder passwordEncoder;
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        System.out.printf("gvpro secret : %s\n", passwordEncoder.encode("gcat"));
        System.out.printf("sunho secret : %s\n", passwordEncoder.encode("sunho123"));
        System.out.printf("hae secret : %s\n", passwordEncoder.encode("hae123"));
        System.out.printf("admin secret : %s\n", passwordEncoder.encode("admin123"));
        System.out.printf("manager secret : %s\n", passwordEncoder.encode("manager123"));


    }
}
