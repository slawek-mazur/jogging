package io.stricte.jogging.web;

import com.google.common.collect.Sets;
import io.stricte.jogging.domain.Distance;
import io.stricte.jogging.domain.Run;
import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.RunRepository;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.service.RunService;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RunControllerIT {

    private static final String EMAIL = "email@example.com";

    private static final String ROLE = "USER";

    private final PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    @Autowired
    UserRepository userRepository;

    @Autowired
    RunRepository runRepository;

    @Autowired
    RunService runService;

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
        runRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testRedirectIfNotLoggedIn() throws Exception {
        mockMvc.perform(
            get("/runs")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testListAsUser() throws Exception {
        mockMvc.perform(
            get("/runs")
                .with(user("joe").roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk());
    }

    @Test
    public void testListAsAdmin() throws Exception {
        mockMvc.perform(
            get("/runs")
                .with(user("joe").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk());
    }

    @Test
    public void testRunsExists() throws Exception {

        final User user = userRepository.findByEmail(EMAIL);

        final LocalDateTime now = LocalDateTime.now();

        final Run run1 = new Run();
        run1.setUser(user);
        run1.setDistance(Distance.ofMeters(1500));
        run1.setDuration(Duration.ofMinutes(25));
        run1.setDay(now.minusDays(3));

        final Run run2 = new Run();
        run2.setUser(user);
        run2.setDistance(Distance.ofMeters(2500));
        run2.setDuration(Duration.ofMinutes(75));
        run2.setDay(now.minusDays(2));

        final Run run3 = new Run();
        run3.setUser(user);
        run3.setDistance(Distance.ofMeters(7500));
        run3.setDuration(Duration.ofMinutes(135));
        run3.setDay(now.minusDays(1));

        runRepository.save(Sets.newHashSet(run1, run2, run3));

        mockMvc.perform(
            get("/runs?sort=day,desc")
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

        final LocalDateTime now = LocalDateTime.now();

        final Run run1 = new Run();
        run1.setUser(user);
        run1.setDistance(Distance.ofMeters(1500));
        run1.setDuration(Duration.ofMinutes(25));
        run1.setDay(now.minusDays(3));

        runRepository.save(Sets.newHashSet(run1));

        mockMvc.perform(
            get("/runs/1")
                .with(user(EMAIL).roles(ROLE))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.id").value(1))
            .andDo(print())
            .andExpect(jsonPath("$.distance").value(1500))
            .andExpect(jsonPath("$.duration").value(25 * 60));
    }

    @Test
    public void testRunNonExistent() throws Exception {

        final User user = userRepository.findByEmail(EMAIL);

        final LocalDateTime now = LocalDateTime.now();

        final Run run1 = new Run();
        run1.setUser(user);
        run1.setDistance(Distance.ofMeters(1500));
        run1.setDuration(Duration.ofMinutes(25));
        run1.setDay(now.minusDays(3));

        runRepository.save(Sets.newHashSet(run1));

        mockMvc.perform(
            get("/runs/5")
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
            post("/run")
                .content(TestUtils.convertObjectToJsonBytes(run))
        )
            .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateRunInvalid() throws Exception {
        RunDto run = new RunDto(null, Distance.ofMeters(500), Duration.ofMinutes(50));

        mockMvc.perform(
            post("/runs")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(run))
        )
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateRunValid() throws Exception {
        RunDto run = new RunDto(LocalDateTime.now(), Distance.ofMeters(500), Duration.ofMinutes(50));

        mockMvc.perform(
            post("/runs")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(run))
        )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void testUpdateRunNonExisting() throws Exception {

        RunDto run = new RunDto(LocalDateTime.now(), Distance.ofMeters(500), Duration.ofMinutes(50));

        mockMvc.perform(
            put("/runs")
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

        final Run entity = new Run();
        entity.setDay(LocalDateTime.now());
        entity.setDistance(Distance.ofMeters(500));
        entity.setDuration(Duration.ofMinutes(50));
        entity.setUser(userRepository.findOne(1));

        final Run savedRun = runRepository.save(entity);

        RunDto run = new RunDto(savedRun.getId(), savedRun.getDay(), Distance.ofMeters(999), savedRun.getDuration());

        mockMvc.perform(
            put("/runs")
                .with(user(EMAIL).roles(ROLE))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(TestUtils.convertObjectToJsonBytes(run))
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }
}
