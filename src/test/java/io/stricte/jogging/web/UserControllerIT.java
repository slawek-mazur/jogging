package io.stricte.jogging.web;

import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.service.UserService;
import io.stricte.jogging.util.TestUtils;
import io.stricte.jogging.web.rest.model.UserDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerIT {

    private static final String EMAIL = "email@example.com";

    private static final String ROLE = "MANAGER";

    private final PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(this.wac)
            .apply(springSecurity())
            .build();
    }

    @After
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void testRedirectIfNotLoggedIn() throws Exception {
        mockMvc.perform(
            get("/users")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testListAsUser() throws Exception {
        mockMvc.perform(
            get("/users")
                .with(user("joe").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isForbidden());
    }

    @Test
    public void testListAsAdmin() throws Exception {
        mockMvc.perform(
            get("/users")
                .with(user("joe").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk());
    }

    @Test
    public void testListAsManager() throws Exception {
        mockMvc.perform(
            get("/users")
                .with(user("joe").roles("MANAGER"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk());
    }

    @Test
    public void testUsersExists() throws Exception {

        userService.create(new UserDto("amanda@example.com", "password"));
        userService.create(new UserDto("bradd@example.com", "password"));
        userService.create(new UserDto("daniel@example.com", "password"));
        userService.create(new UserDto("jessica@example.com", "password"));
        userService.create(new UserDto("monica@example.com", "password"));

        mockMvc.perform(
            get("/users?sort=email,desc")
                .with(user(EMAIL).roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(jsonPath("$.length()").value(5))
            .andExpect(jsonPath("$[0].email").value("monica@example.com"))
            .andExpect(jsonPath("$[1].email").value("jessica@example.com"))
            .andExpect(jsonPath("$[2].email").value("daniel@example.com"))
            .andExpect(jsonPath("$[3].email").value("bradd@example.com"))
            .andExpect(jsonPath("$[4].email").value("amanda@example.com"));
    }

    @Test
    public void testUserExists() throws Exception {
        User user = userService.create(new UserDto("joey@example.com", "password"));

        mockMvc.perform(
            get("/users/" + user.getId())
                .with(user(EMAIL).roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(jsonPath("$.id").value(user.getId()))
            .andExpect(jsonPath("$.email").value("joey@example.com"));
    }

    @Test
    public void testUserNonExistent() throws Exception {
        mockMvc.perform(
            get("/users/999")
                .with(user(EMAIL).roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateUserNoSession() throws Exception {
        UserDto user = new UserDto("joey@example.com", "pass");

        mockMvc.perform(
            post("/users")
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateUserInvalid() throws Exception {
        UserDto user = new UserDto("joey@examplecom", "pass");

        mockMvc.perform(
            post("/users")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isBadRequest());

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void testCreateUserValid() throws Exception {
        UserDto user = new UserDto("john@example.com", "password");

        mockMvc.perform(
            post("/users")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    public void testUpdateUserNonExisting() throws Exception {
        UserDto user = new UserDto("alice@example.com", "password");

        mockMvc.perform(
            put("/users")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateUserExisting() throws Exception {

        final String email = "daniel@example.com";
        final String password = passwordEncoder.encode("password");

        final User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        final User saved = userRepository.save(user);

        final UserDto userDto = new UserDto();
        userDto.setId(saved.getId());
        userDto.setEmail("david@example.com");

        mockMvc.perform(
            put("/users")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(userDto))
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        final User existing = userRepository.findOne(saved.getId());
        assertThat(existing.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(existing.getPassword()).isEqualTo(saved.getPassword());
    }

    @Test
    public void testDeleteNonExistingUser() throws Exception {

        userService.create(new UserDto("alice@example.com", "password"));

        mockMvc.perform(
            delete("/users/444")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isBadRequest());

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    public void testDeleteExisting() throws Exception {

        final User user = userService.create(new UserDto("alice@example.com", "password"));

        assertThat(userRepository.findAll()).hasSize(1);

        mockMvc.perform(
            delete("/users/" + user.getId())
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk());

        assertThat(userRepository.findAll()).isEmpty();
    }
}
