package Capstone_Project_1.Evert.Organizer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
/* Filter chain for security configuration and to filter the requests but currently I am facing some issue when i limit the request
    so currently I am allowing all the requests I will resolve this issue and update it as soon as possible*/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable all security features except password encoding
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow ALL requests
                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF
                .formLogin(form -> form.disable()) // Disable form login
                .logout(logout -> logout.disable()); // Disable logout handling

        return http.build();
    }
    // Password encoder bean for encoding passwords It is convert the password into a hashcode
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
