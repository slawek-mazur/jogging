package io.stricte.jogging.service;

import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.web.rest.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserDto userDto) {
        final User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public User lookup(UserDto userDto) {
        final User user = userRepository.findByEmail(userDto.getEmail());

        return Optional.ofNullable(user)
            .filter(u -> passwordEncoder.matches(userDto.getPassword(), user.getPassword()))
            .orElse(null);
    }
}
