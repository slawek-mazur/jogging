package io.stricte.jogging.web;

import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.service.UserService;
import io.stricte.jogging.util.TestUtils;
import io.stricte.jogging.web.rest.model.UserDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountControllerIT {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders
            .webAppContextSetup(this.wac)
            .apply(springSecurity())
            .build();
    }

    @Test
    public void testRegisterInvalidEmail() throws Exception {

        final UserDto user = new UserDto("test", "some-good-random-pass");

        mockMvc.perform(
            post("/account/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().is4xxClientError());

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void testAccountInfo() throws Exception {
        mockMvc.perform(
            get("/account/info")
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().is5xxServerError());
    }

    @Test
    public void testAccountInfoAsLogged() throws Exception {
        mockMvc.perform(
            get("/account/info")
                .with(user("joe@example.com").roles("ADMIN"))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("joe@example.com"));
    }

    @Test
    public void testInvalidPassword() throws Exception {

        final UserDto user = new UserDto("test@jogging.com", "pass");

        mockMvc.perform(
            post("/account/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().is4xxClientError());

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void testRegisterValidData() throws Exception {

        final UserDto user = new UserDto("test@jogging.com", "some-good-random-pass");

        mockMvc.perform(
            post("/account/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(redirectedUrlPattern("/users/*"));

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    public void testRegisterSameEmailTwice() throws Exception {

        final UserDto user = new UserDto("test@jogging.com", "some-good-random-pass");

        mockMvc.perform(
            post("/account/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        mockMvc.perform(
            post("/account/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isConflict());

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    public void testLoginNonExisting() throws Exception {

        final UserDto user = new UserDto("test@jogging.com", "some-good-random-pass");

        userService.register(new UserDto("test@jogging.pl", "some-good-random-pass"));

        mockMvc.perform(
            post("/account/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isNotFound());
    }

    @Test
    public void testLoginExisting() throws Exception {

        final UserDto user = new UserDto("test@jogging.com", "some-good-random-pass");

        userService.register(user);

        mockMvc.perform(
            post("/account/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }
}
