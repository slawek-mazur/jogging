package io.stricte.jogging.config.security;

import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@ToString
@EqualsAndHashCode(of = "email")
class SimpleUserDetails implements UserDetails {

    private final String email;

    private final String password;

    private final Collection<GrantedAuthority> authorities;

    SimpleUserDetails(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.authorities = Sets.newHashSet(new SimpleGrantedAuthority(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
