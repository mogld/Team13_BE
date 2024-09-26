package dbdr.config;

import dbdr.security.BaseUserDetailsService;
import dbdr.security.JwtFilter;
import dbdr.security.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractAuthenticationFilterConfigurer::disable)
            .sessionManagement(
                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests((authorize) -> {
                authorize
                    .requestMatchers("/v1/guardian/login").permitAll()
                    .requestMatchers("/v1/admin/guardian").hasAnyRole("GUARDIAN", "ADMIN")
                    .anyRequest().authenticated();
            })

            .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)

            .exceptionHandling((exception) -> exception
            .accessDeniedHandler((request, response, accessDeniedException) -> {
                log.debug("접근 거부: {}", accessDeniedException.getMessage());
                Authentication auth = (Authentication) request.getUserPrincipal();
                if (auth != null) {
                    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
                    log.warn("User '{}' 거부됨: {} with authorities: {}", auth.getName(), request.getRequestURI(), authorities.stream().map(GrantedAuthority::getAuthority).toArray());
                    log.warn("시큐리팃 : {}",SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray());
                }
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 거부");
            })
            .authenticationEntryPoint((request, response, authException) -> {
                log.debug("인증 실패: {}", authException.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
            }));


        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
