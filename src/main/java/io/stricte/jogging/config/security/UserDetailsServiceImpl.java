package io.stricte.jogging.config.security;

import io.stricte.jogging.domain.User;
import io.stricte.jogging.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " doesn't exists or isn't active");
        }

        return new SimpleUserDetails(user.getEmail(), user.getPassword(), user.getRole());
    }
}
