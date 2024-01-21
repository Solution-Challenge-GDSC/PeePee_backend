package com.gdsc.solutionchallenge.global.config;

import com.gdsc.solutionchallenge.global.exception.AuthenticationEntryPointHandler;
import com.gdsc.solutionchallenge.global.jwt.JwtAuthenticationFilter;
import com.gdsc.solutionchallenge.global.jwt.JwtTokenUtil;
import com.gdsc.solutionchallenge.oauth.CustomOAuth2UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagent) -> sessionManagent
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/jwt/**").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers( "/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exceptionConfig) ->
                        exceptionConfig.authenticationEntryPoint(authenticationEntryPointHandler))
                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil), UsernamePasswordAuthenticationFilter.class)

                .oauth2Login(oauth2Login ->
                        oauth2Login.userInfoEndpoint(userInfoEndpointConfig ->
                                userInfoEndpointConfig.userService(customOAuth2UserService)));

        return http.build();
    }

}
