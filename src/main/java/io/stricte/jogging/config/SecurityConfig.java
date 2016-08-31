package io.stricte.jogging.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;

import java.util.List;

@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        final AffirmativeBased manager = (AffirmativeBased) super.accessDecisionManager();
        final List<AccessDecisionVoter<?>> decisionVoters = manager.getDecisionVoters();
        decisionVoters.clear();
        decisionVoters.add(roleHierarchyVoter());
        return manager;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService)
        throws Exception {

        auth.userDetailsService(userDetailsService);
    }

    @EnableWebSecurity
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        DefaultWebSecurityExpressionHandler expressionHandler;

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

        @Bean
        DefaultWebSecurityExpressionHandler expressionHandler(RoleHierarchy roleHierarchy) {
            final DefaultWebSecurityExpressionHandler handler =
                new DefaultWebSecurityExpressionHandler();

            handler.setRoleHierarchy(roleHierarchy);

            return handler;
        }
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Bean
    public RoleHierarchyVoter roleHierarchyVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_MANAGER and ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }
}
