package com.ctu.bookstore.configuration;

import com.ctu.bookstore.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class SecurityConfig {
//    private final String[] PUBLIC_ENDPOINTS = {"/users",
//            "/auth/token", "/auth/introspect" ,"/auth/logout", "/images/upload",
//            "/products","/products/*","checkout/create-session","/carts/my-cart",
//            "/carts/size","user/infor","/intentChat","/category","/products/filter-by-category","/search/*", "/search", "/api/test-gemini"
//    };
    private final String[] PUBLIC_ENDPOINTS = {"checkout/create-session","carts/item","/conversations/create",
        "/conversation","/messages/create","/messages/*",
        "/conversations/create-default","/chat","/intentChat","/users/*"
    };
    @Value("${jwt.signerKey}")
    private String signerKey;
    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 1. Kích hoạt cấu hình CORS (Nó sẽ tìm bean CorsConfigurationSource)
        httpSecurity.cors(Customizer.withDefaults());
        //Cho phep access vào những endpoints trong public_endpoints bằng các phương thức POST, GET mà không cần xác thực
        httpSecurity.authorizeHttpRequests(authorize ->
                authorize.requestMatchers(HttpMethod.POST,PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET,PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(
                                // cho phép truy cập vào nhưng URL này từ Browser mà không cần Authentication
                                // ví dụ: http://localhost:8080/swagger-ui.html
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

        // Cấu hình oauth2. Đăng kí 1 cai provider manager, authetication provider để support JWT token
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

    // Bean để cấu hình CORS
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

    //Bean để thay đổi AuthorityPrefix từ SCOPE_ -> ROLE_
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter (){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    // Bean dùng để mã hóa mật khẩu, giúp không cần phải tốn công tạo thêm 1 cái mỗi lần dùng (ApplicationInitConfig,
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}