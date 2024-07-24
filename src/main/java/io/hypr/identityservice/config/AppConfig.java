package io.hypr.identityservice.config;

import io.hypr.identityservice.entity.Role;
import io.hypr.identityservice.entity.RoleEnum;
import io.hypr.identityservice.entity.User;
import io.hypr.identityservice.repository.RoleRepository;
import io.hypr.identityservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@Slf4j
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "spring",
        value = "datasource.driver-class-name",
        havingValue = "org.postgresql.Driver"
    )
    public ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application...");
        return args -> {
            if (userRepository
                .findByUsername("admin")
                .isEmpty()) {
                roleRepository.save(Role
                                        .builder()
                                        .name(RoleEnum.USER.name())
                                        .description("User role")
                                        .build());

                Role adminRole = roleRepository.save(Role
                                                         .builder()
                                                         .name(RoleEnum.ADMIN.name())
                                                         .description("Admin role")
                                                         .build());

                var roles = new HashSet<Role>();
                roles.add(adminRole);

                User user = User
                    .builder()
                    .username("admin")
                    .password(passwordEncoder().encode("admin"))
                    .roles(roles)
                    .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin");
            }
        };
    }
}
