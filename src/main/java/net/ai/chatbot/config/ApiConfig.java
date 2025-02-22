package net.ai.chatbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class ApiConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Specify frontend origin (DO NOT USE "*")
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Frontend URL

        // ✅ Allow necessary HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ✅ Allow Authorization and other headers
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // ✅ Allow sending cookies & credentials
        config.setAllowCredentials(true);

        // ✅ Allow CORS for all API routes
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
