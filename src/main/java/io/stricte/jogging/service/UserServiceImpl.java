package io.stricte.jogging.service;

import com.google.common.base.Strings;
import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.web.rest.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(UserDto userDto) {
        return create(userDto);
    }

    @Override
    public User login(UserDto userDto) {
        final User user = userRepository.findByEmail(userDto.getEmail());

        return Optional.ofNullable(user)
            .filter(u -> passwordEncoder.matches(userDto.getPassword(), user.getPassword()))
            .orElse(null);
    }

    @Override
    public boolean emailRegistered(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User create(UserDto userDto) {
        final User user = new User();
        user.setEmail(userDto.getEmail());

        final String password = userDto.getPassword();
        if (Strings.isNullOrEmpty(password) || password.length() < 5) {
            throw new IllegalArgumentException("Password is too short");
        }
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public Page<User> all(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User one(int id) {
        return userRepository.findOne(id);
    }

    @Override
    public void update(UserDto userDto) {
        final User user = userRepository.findOne(userDto.getId());
        user.setEmail(userDto.getEmail());
        //update password if set
        if (!Strings.isNullOrEmpty(userDto.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public void delete(int id) {
        userRepository.delete(id);
    }
}
