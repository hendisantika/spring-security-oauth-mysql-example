package com.hendisantika.springsecurityauthserver.config;

import com.hendisantika.springsecurityauthserver.entity.Role;
import com.hendisantika.springsecurityauthserver.entity.Users;
import com.hendisantika.springsecurityauthserver.repository.RoleRepository;
import com.hendisantika.springsecurityauthserver.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-security-auth-server
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Link : s.id/hendisantika
 * Date: 27/11/26
 * Time: 07.56
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements ApplicationRunner {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.username:hendi}")
    private String username;

    @Value("${app.seed.password:password}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usersRepository.findByName(username).isPresent()) {
            return;
        }
        Role role = roleRepository.findByRole("USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "USER")));

        Users user = new Users();
        user.setName(username);
        user.setLastName("Santika");
        user.setEmail(username + "@example.com");
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(1);
        user.setRoles(Set.of(role));
        usersRepository.save(user);
        log.info("Seeded demo user '{}'", username);
    }
}
