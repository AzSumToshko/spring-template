package com.example.spring_template.config;

import com.example.spring_template.domain.enums.Roles;
import com.example.spring_template.auth.filter.JWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.spring_template.constant.Constants.API_ENDPOINT;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public JWTFilter jwtFilter(){
        return new JWTFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter jwtFilter) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/wiki/**").permitAll()

                    .requestMatchers(HttpMethod.GET, API_ENDPOINT + "/cars/**").permitAll()
                    .requestMatchers(HttpMethod.POST, API_ENDPOINT + "/cars/**").hasAuthority(Roles.ADMIN.name())
                    .requestMatchers(HttpMethod.PUT, API_ENDPOINT + "/cars/**").hasAuthority(Roles.ADMIN.name())
                    .requestMatchers(HttpMethod.DELETE, API_ENDPOINT + "/cars/**").hasAuthority(Roles.ADMIN.name())

                    .requestMatchers("/api/v1/admin/**").hasAuthority(Roles.ADMIN.name())
                    // Do not remove the line above since it's used as trigger line for the entity generator and appends 4 rules above it.
                    // it's looking for "/api/v1/admin/**" so do not remove this part and if you move the line keep in mind that if you run the command 4 rules will appear above the line
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
