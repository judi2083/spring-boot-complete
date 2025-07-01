// package com.example.springboot.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig {

//     // 👤 Define in-memory users with roles
//     @Bean
//     public InMemoryUserDetailsManager userDetailsService() {
//         UserDetails admin = User.withUsername("admin")
//             .password(passwordEncoder().encode("admin123"))
//             .roles("ADMIN")
//             .build();

//         UserDetails user = User.withUsername("user")
//             .password(passwordEncoder().encode("user123"))
//             .roles("USER")
//             .build();

//         return new InMemoryUserDetailsManager(admin, user);
//     }

//     // 🔐 Secure endpoints based on roles
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .csrf(csrf -> csrf.disable())
//             .authorizeHttpRequests(auth -> auth
//                 // allow Swagger endpoints for all
//                 .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
//                 // only ADMIN can access employee CRUD
//                 .requestMatchers("/api/employees/**").hasRole("ADMIN")
//                 // anything else requires auth
//                 .anyRequest().authenticated()
//             )
//             .httpBasic(Customizer.withDefaults());

//         return http.build();
//     }

//     @Bean
//     public BCryptPasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }

package com.example.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.security.config.Customizer;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .authorizeHttpRequests(auth -> auth
    //             // 👉 Allow unauthenticated access to token generation
    //             .requestMatchers("/auth/token").permitAll()

    //             // 👉 Allow Swagger for all
    //             .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

    //             // 👉 Secure employee endpoints
    //             .requestMatchers("/api/employees/**").hasRole("ADMIN")

    //             .anyRequest().authenticated()
    //         )
    //         .httpBasic(Customizer.withDefaults()); // or use formLogin() if preferred

    //     return http.build();
    // }


    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .authorizeHttpRequests(auth -> auth
    //             // 👉 Allow unauthenticated access to token generation
    //             .requestMatchers("/auth/token").permitAll()

    //             // 👉 Allow Swagger for all
    //             .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                
    //             // ✅ Allow all GETs to /api/employees/**
    //             //.requestMatchers(HttpMethod.GET, "/api/employees/**").permitAll()

    //             // 👉 Secure employee endpoints
    //             .requestMatchers("/api/employees/**").hasRole("ADMIN")

    //             .anyRequest().authenticated()
    //         )
    //         .oauth2ResourceServer(oauth2 -> oauth2
    //             .jwt(Customizer.withDefaults()) // <-- Use JwtDecoder bean instead
    //         );

    //     return http.build();
    // }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 🔓 Public endpoints
               // .requestMatchers("/auth/token").permitAll()
                // 🔓 Public endpoints
            .requestMatchers(
                "/auth/token",
                "/index.html",
                "/",
                "/favicon.ico",
                "/manifest.json",
                "/logo192.png",
                "/logo512.png",
                "/static/**",
                "/robots.txt"
            ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api","/api/","/").permitAll()
                .requestMatchers(HttpMethod.GET, "/employees/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/actuator/**","/actuator/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()

                // 🔐 Secure everything else
                .anyRequest().authenticated()
            )
            // 🔐 Use JWT authentication
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }





    @Bean
    public JwtDecoder jwtDecoder(ResourceLoader resourceLoader) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:public.key");
        try (InputStream inputStream = resource.getInputStream()) {
            String publicKeyPEM = new String(inputStream.readAllBytes())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpec);

            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        }
    }
}
