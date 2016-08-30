package io.stricte.jogging.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    DefaultWebSecurityExpressionHandler expressionHandler;

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
                    .expressionHandler(expressionHandler)
                    .antMatchers(
                        "/runs/**",
                        "/admin/**",
                        "/management/**"
                    ).authenticated()
                    .anyRequest().permitAll()
            .and()
                .rememberMe()
            .and()
                .exceptionHandling()
                    .accessDeniedPage("/accessDenied");

        // @formatter:on
    }

    @Configuration
    static class SecurityAuxiliaryConfig {

        @Bean
        PasswordEncoder passwordEncoder() {
            return new StandardPasswordEncoder();
        }

        @Bean
        DefaultWebSecurityExpressionHandler expressionHandler() {
            final DefaultWebSecurityExpressionHandler handler =
                new DefaultWebSecurityExpressionHandler();

            final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
            roleHierarchy.setHierarchy("ROLE_MASTER > ROLE_ADMIN and ROLE_ADMIN > ROLE_USER");
            handler.setRoleHierarchy(roleHierarchy);

            return handler;
        }
    }
}
