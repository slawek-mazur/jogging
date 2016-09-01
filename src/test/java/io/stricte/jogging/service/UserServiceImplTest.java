package io.stricte.jogging.service;

import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.web.rest.model.UserDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private final PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    private UserService userService;

    private UserRepository userRepository;

    @Before
    public void setup() throws Exception {
        userRepository = mock(UserRepository.class);
        userService = spy(new UserServiceImpl(userRepository, passwordEncoder));
    }

    @After
    public void cleanup() throws Exception {
        reset(userRepository);
    }

    @Test
    public void registerUser() throws Exception {
        userService.register(new UserDto("joe@example.com", "password"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testLogin() throws Exception {
        final String email = "test@email";
        final String password = "pass";
        final String hash = passwordEncoder.encode(password);

        final User user = new User();
        user.setEmail(email);
        user.setPassword(hash);

        doReturn(user).when(userRepository).findByEmail(email);

        final User logged = userService.login(new UserDto(email, password));

        assertThat(logged).isNotNull();
    }

    @Test
    public void testEmailExists() throws Exception {
        doReturn(new User()).when(userRepository).findByEmail("test@email");

        assertThat(userService.emailRegistered("test@email")).isTrue();
    }
}