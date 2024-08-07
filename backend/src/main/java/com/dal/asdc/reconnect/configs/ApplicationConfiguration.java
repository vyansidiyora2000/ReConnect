package com.dal.asdc.reconnect.configs;

import com.dal.asdc.reconnect.repository.UsersRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


/**
 * Configuration class for setting up application security configurations.
 */

@Configuration
public class ApplicationConfiguration {
    private final UsersRepository userRepository;

    /**
     * Constructor for ApplicationConfiguration, initializes with a UsersRepository.
     *
     * @param userRepository the repository to interact with user data.
     */
    public ApplicationConfiguration(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Defines a bean for UserDetailsService, which retrieves user-specific data.
     *
     * @return a UserDetailsService that fetches user details from the database using email.
     */
    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findByUserEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Defines a bean for BCryptPasswordEncoder, which is used for hashing passwords.
     *
     * @return an instance of BCryptPasswordEncoder.
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines a bean for AuthenticationManager, which is used for managing authentication.
     *
     * @param config the AuthenticationConfiguration containing the authentication configuration.
     * @return an instance of AuthenticationManager.
     * @throws Exception if an error occurs while configuring the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines a bean for AuthenticationProvider, which is responsible for authenticating a user.
     *
     * @return an instance of AuthenticationProvider configured with UserDetailsService and password encoder.
     */
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}
