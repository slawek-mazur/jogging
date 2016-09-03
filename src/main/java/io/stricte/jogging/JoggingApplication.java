package io.stricte.jogging;

import com.google.common.collect.Sets;
import io.stricte.jogging.config.security.Role;
import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JoggingApplication {

    public static void main(String[] args) {
        SpringApplication.run(JoggingApplication.class, args);
    }

    @Bean
    CommandLineRunner initDb(UserRepository userRepository, PasswordEncoder encoder) {
        return strings -> {

            final User user = new User();
            user.setEmail("joe@example.com");
            user.setPassword(encoder.encode("joe_pass"));
            user.setRole(Role.USER);

            final User manager = new User();
            manager.setEmail("jack@example.com");
            manager.setPassword(encoder.encode("jack_pass"));
            manager.setRole(Role.MANAGER);

            final User admin = new User();
            admin.setEmail("jill@example.com");
            admin.setPassword(encoder.encode("jill_pass"));
            admin.setRole(Role.ADMIN);

            userRepository.save(Sets.newHashSet(user, manager, admin));
        };
    }
}
