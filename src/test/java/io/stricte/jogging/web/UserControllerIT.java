package io.stricte.jogging.web;

import io.stricte.jogging.domain.Distance;
import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.util.TestUtils;
import io.stricte.jogging.web.rest.model.RunDto;
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

import java.time.Duration;
import java.time.LocalDateTime;

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
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(this.wac)
            .apply(springSecurity())
            .build();

        final User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(passwordEncoder.encode("pass"));

        userRepository.save(user);
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
                .with(user("joe").roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk());
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
            .andExpect(status().isForbidden());
    }

    @Test
    public void testRunsExists() throws Exception {

        final User user = userRepository.findByEmail(EMAIL);

        mockMvc.perform(
            get("/users?sort=day,desc")
                .with(user(EMAIL).roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.length()").value(3))
            .andDo(print())
            .andExpect(jsonPath("$[0].duration").value(135 * 60))
            .andExpect(jsonPath("$[1].duration").value(75 * 60))
            .andExpect(jsonPath("$[2].duration").value(25 * 60));
    }

    @Test
    public void testRunExists() throws Exception {

        final User user = userRepository.findByEmail(EMAIL);

        mockMvc.perform(
            get("/users/" + user.getId())
                .with(user(EMAIL).roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(jsonPath("$.distance").value(1500))
            .andExpect(jsonPath("$.duration").value(25 * 60));
    }

    @Test
    public void testRunNonExistent() throws Exception {

        final User user = userRepository.findByEmail(EMAIL);

        mockMvc.perform(
            get("/users/999")
                .with(user(EMAIL).roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateRunNoSession() throws Exception {
        RunDto run = new RunDto(LocalDateTime.now(), Distance.ofMeters(500), Duration.ofMinutes(50));

        mockMvc.perform(
            post("/users")
                .content(TestUtils.convertObjectToJsonBytes(run))
        )
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateRunInvalid() throws Exception {
        RunDto run = new RunDto(null, Distance.ofMeters(500), Duration.ofMinutes(50));

        mockMvc.perform(
            post("/users")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(run))
        )
            .andExpect(status().isBadRequest());

        assertThat(userRepository.findAll()).isEmpty();
    }

    @Test
    public void testCreateRunValid() throws Exception {
        RunDto run = new RunDto(LocalDateTime.now(), Distance.ofMeters(500), Duration.ofMinutes(50));

        mockMvc.perform(
            post("/users")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(run))
        )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    public void testUpdateRunNonExisting() throws Exception {

        RunDto run = new RunDto(LocalDateTime.now(), Distance.ofMeters(500), Duration.ofMinutes(50));

        mockMvc.perform(
            put("/users")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(run))
        )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateRunExisting() throws Exception {

        User user = new User();

        mockMvc.perform(
            put("/users")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(user))
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        //final Run one = runRepository.findOne(savedRun.getId());
        //assertThat(one.getDistance()).isEqualTo(Distance.ofMeters(999));
    }

    @Test
    public void testDeleteNonExistingOrOtherUsersRuns() throws Exception {

        final User user = new User();
        user.setEmail("other.email@example.com");
        user.setPassword(passwordEncoder.encode("pass"));
        final User savedUser = userRepository.save(user);

        mockMvc.perform(
            delete("/users/" + savedUser.getId())
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isBadRequest());

    }

    @Test
    public void testDeleteExisting() throws Exception {

        final User save = userRepository.save(userRepository.findByEmail(EMAIL));

        mockMvc.perform(
            delete("/users/" + save.getId())
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk());

        assertThat(userRepository.findAll()).isEmpty();
    }
}
