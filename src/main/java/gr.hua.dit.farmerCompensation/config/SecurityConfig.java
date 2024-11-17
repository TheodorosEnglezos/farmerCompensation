package gr.hua.dit.farmerCompensation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig{

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean (AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();

    }
    //set allowed methods, headers, and requestmatchers depending on the role of the user
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(
                List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
        corsConfiguration
                .setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        http
                                        .csrf(csrf -> csrf.disable())
                                        .cors(cors -> cors.configurationSource(request -> corsConfiguration))
                                        .addFilterBefore(authenticationJwtTokenFilter(),UsernamePasswordAuthenticationFilter.class)
                                        .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(unauthorizedHandler))
                                        .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/v3/api-docs/**", "/v2/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers("/api/admin/users/**").hasRole("ADMIN")
                                                .requestMatchers("/api/declaration/{user_id}/**").hasAnyRole("ADMIN", "INSPECTOR", "FARMER")
                                                .requestMatchers("/api/users/**").hasAnyRole("ADMIN", "INSPECTOR", "FARMER")
                                                .requestMatchers("/api/declaration/report/{user_id}/**").hasAnyRole("ADMIN", "INSPECTOR")

                                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout((logout) -> logout.permitAll());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
