package com.ctu.bookstore.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {
    String[] PUBLIC_POST_ENDPOINTS = {"/auth/**", "/users"};
    String[] PUBLIC_GET_ENDPOINTS = {"/products", "/products/**", "/category", "/category/**", "/search"};
    CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // 1. Kích hoạt cấu hình CORS (Nó sẽ tìm bean CorsConfigurationSource)
        httpSecurity.cors(Customizer.withDefaults());
        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(
                                "/bookstore/swagger-ui/**",
                                "/bookstore/swagger-ui.html",
                                "/bookstore/v3/api-docs/**",
                                "/bookstore/v3/api-docs",
                                "/bookstore/v3/api-docs/swagger-config",

                                // fallback no context-path
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/v3/api-docs/swagger-config",

                                // static swagger resources
                                "/swagger-ui/index.html",
                                "/swagger-ui/swagger-ui.css",
                                "/swagger-ui/swagger-ui-bundle.js",
                                "/swagger-ui/swagger-ui-standalone-preset.js"
                        ).permitAll()
                        .anyRequest().authenticated());

        // 2. Tắt CSRF nếu dùng JWT (vì JWT đã chống CSRF rồi)
        httpSecurity.csrf(csrf -> csrf.disable());
        // Cấu hình oauth2. turn on a provider manager, authetication provider để support JWT token
        httpSecurity.oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                        .decoder(customJwtDecoder)
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                // giúp chỉ ra khi mà cái authentication nó fail thì ta sẽ điều hướng user đi đâu
                // trong trường hợp này ta chỉ return về 1 error message chứ không điều hướng đi đầu cả
        );

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    //Bean để thay đổi AuthorityPrefix từ "SCOPE_" -> "", do đã cài đặt prefix là "ROLE_" đối với roles trong AuthService
    // và để tránh Spring gán "SCOPE_" cho mọi permissions nên ta chuyển thành ""
    // giúp khi decode JWT ra dể phân biệt cái nào là role cái nào là permission
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}