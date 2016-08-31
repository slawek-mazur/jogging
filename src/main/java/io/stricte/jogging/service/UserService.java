package io.stricte.jogging.service;

import io.stricte.jogging.domain.User;
import io.stricte.jogging.web.rest.model.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    User register(UserDto userDto);

    User login(UserDto userDto);

    boolean emailRegistered(String email);

    User create(UserDto userDto);

    Page<User> all(Pageable pageable);

    User one(int id);

    void update(UserDto userDto);

    void delete(int id);
}
