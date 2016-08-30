package io.stricte.jogging.web;

import com.google.common.collect.Sets;
import io.stricte.jogging.domain.Distance;
import io.stricte.jogging.domain.Run;
import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.RunRepository;
import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.service.RunService;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RunControllerIT {

    private static final String EMAIL = "email@example.com";

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
                .with(user("joe").roles("USER"))
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
                .with(user("joe").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].duration.minutes").value(135))
            .andExpect(jsonPath("$[1].duration.minutes").value(75))
            .andExpect(jsonPath("$[2].duration.minutes").value(25));
    }
}
