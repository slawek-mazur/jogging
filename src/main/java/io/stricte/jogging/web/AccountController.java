package io.stricte.jogging.web;

import io.stricte.jogging.domain.User;
import io.stricte.jogging.service.UserService;
import io.stricte.jogging.web.rest.model.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDto> info(Principal principal) {
        return Optional.ofNullable(principal)
            .map(p -> ResponseEntity.ok(new UserDto(principal.getName())))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) throws URISyntaxException {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        if (userService.emailRegistered(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        try {
            final User registered = userService.register(userDto);

            return ResponseEntity.created(new URI("/users/" + registered.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {

        return Optional.ofNullable(userService.login(userDto))
            .map(u -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).build())
            .orElse(ResponseEntity.notFound().build());
    }
}
