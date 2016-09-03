package io.stricte.jogging.config;

import io.stricte.jogging.config.security.CsrfHeaderFilter;
import io.stricte.jogging.config.security.LoginFailureHandler;
import io.stricte.jogging.config.security.LoginSuccessHandler;
import io.stricte.jogging.config.security.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionRoot;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;

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
    void configureGlobal(AuthenticationManagerBuilder auth, UserDetailsService userDetailsService)
        throws Exception {

        auth.userDetailsService(userDetailsService);
    }

    @EnableWebSecurity
    static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        DefaultWebSecurityExpressionHandler expressionHandler;

        @Autowired
        LoginSuccessHandler loginSuccessHandler;

        @Autowired
        LoginFailureHandler loginFailureHandler;

        @Autowired
        LogoutSuccessHandler logoutSuccessHandler;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                .csrf()
                .and()
                    .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
                .formLogin()
                    .loginProcessingUrl("/account/authentication")
                    .successHandler(loginSuccessHandler)
                    .failureHandler(loginFailureHandler)
                    .permitAll()
                .and()
                    .logout()
                        .logoutUrl("/account/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .deleteCookies("JSESSIONID", "XSRF-TOKEN")
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
    SecurityEvaluationContextExtension extension() {
        return new SecurityEvaluationContextExtension() {

            @Override
            public Object getRootObject() {
                Authentication authentication = getAuthentication();
                final SecurityExpressionRoot root = new SecurityExpressionRoot(authentication) {

                };
                root.setRoleHierarchy(roleHierarchy());
                return root;
            }

            private Authentication getAuthentication() {
                return SecurityContextHolder.getContext().getAuthentication();
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder();
    }

    @Bean
    RoleHierarchyVoter roleHierarchyVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }

    @Bean
    RoleHierarchyImpl roleHierarchy() {
        final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(Role.ADMIN + " > " + Role.MANAGER + " and " + Role.ADMIN + " > " + Role.USER);
        return roleHierarchy;
    }
}
