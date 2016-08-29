package io.stricte.jogging.service;

import io.stricte.jogging.domain.User;
import io.stricte.jogging.web.rest.model.UserDto;

public interface UserService {

    User register(UserDto userDto);

    User login(UserDto userDto);
}
