package org.example.ec2confi.security;

import lombok.RequiredArgsConstructor;
import org.example.ec2confi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUserName(String username)
        throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

}
