package io.stricte.jogging.service;

import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.web.rest.model.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

    UserService userService;

    UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        userService = spy(new UserServiceImpl(userRepository, passwordEncoder));
    }

    @Test
    public void registerUser() throws Exception {
        userService.registerUser(new UserDto("test", "pass"));

        verify(userRepository).save(any(User.class));
    }
}