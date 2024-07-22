package io.hypr.identityservice.config;

import io.hypr.identityservice.entity.Role;
import io.hypr.identityservice.entity.User;
import io.hypr.identityservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@Slf4j
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository
                .findByUsername("admin")
                .isEmpty()) {
                User user = User
                    .builder()
                    .username("admin")
                    .password(passwordEncoder().encode("admin"))
                    .roles(Set.of(Role.ADMIN.name()))
                    .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin");
            }
        };
    }
}
