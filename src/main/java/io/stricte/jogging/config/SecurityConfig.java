package io.stricte.jogging.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .formLogin()
				.loginPage("/login")
                .defaultSuccessUrl("/index")
				.failureUrl("/login?error")
                .permitAll()
			.and()
                .logout()
                    .logoutSuccessUrl("/login?logout")
                    .logoutUrl("/logout")
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            .and()
                .authorizeRequests()
                    .antMatchers(
                        "/system/**"
                    ).denyAll()
                    .anyRequest().permitAll()
            .and()
                .rememberMe()
            .and()
                .exceptionHandling()
                    .accessDeniedPage("/accessDenied");

        // @formatter:on
    }

    @Configuration
    static class EncoderConfig {

        @Bean
        PasswordEncoder passwordEncoder() {
            return new StandardPasswordEncoder();
        }
    }
}
