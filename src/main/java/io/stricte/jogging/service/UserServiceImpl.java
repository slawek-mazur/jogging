package io.stricte.jogging.service;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.web.rest.model.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleHierarchy roleHierarchy;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
        RoleHierarchy roleHierarchy) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleHierarchy = roleHierarchy;
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
    public Page<UserDto> all(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::asDto);
    }

    @Override
    public UserDto one(int id) {
        return Optional.ofNullable(userRepository.findOne(id))
            .map(this::asDto)
            .orElse(null);
    }

    private UserDto asDto(User user) {
        final UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());

        final Collection<? extends GrantedAuthority> authorities =
            roleHierarchy.getReachableGrantedAuthorities(Sets.newHashSet(new SimpleGrantedAuthority(user.getRole())));

        dto.setAuthorities(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));

        return dto;
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
