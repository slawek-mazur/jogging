package io.stricte.jogging.web;

import io.stricte.jogging.repository.UserRepository;
import io.stricte.jogging.service.UserService;
import org.junit.After;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RunControllerIT {

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
        mockMvc.perform(
            get("/runs")
                .with(user("joe").password("pass").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(""));
    }
}
