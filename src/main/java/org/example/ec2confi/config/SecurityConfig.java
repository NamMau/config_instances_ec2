package org.example.ec2confi.config;

import lombok.RequiredArgsConstructor;
import org.example.ec2confi.security.CustomUserDetailsService;
import org.example.ec2confi.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService); // ✅ FIX

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                // ⭐ JWT → STATELESS
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/api/auth/**",
                                "/login",
                                "/register",
                                "/dashboard",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/image/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/api/user/**").authenticated()
                        //.requestMatchers("/api/files/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/files/upload").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/files/my-files").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/files/download/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/files/delete/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/files/preview/**").authenticated()
                        .anyRequest().authenticated()
                )

                // ✅ JWT filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}