package com.datawave.datawaveapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;


import static java.nio.file.attribute.AclEntryPermission.DELETE;
import static javax.swing.text.html.FormSubmitEvent.MethodType.POST;
import static org.hibernate.CacheMode.GET;
import static org.hibernate.CacheMode.PUT;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

@Configuration
public class CorsConfig {
    private static final String X_REQESTED_WITH = "X-Requested-With";

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedHeaders(List.of(ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, X_REQESTED_WITH, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_ALLOW_CREDENTIALS));
        config.setExposedHeaders(List.of(ORIGIN, ACCESS_CONTROL_ALLOW_ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, X_REQESTED_WITH, ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_ALLOW_CREDENTIALS));
        config.setAllowedMethods(List.of(GET.name(), POST.name(), PUT.name(), DELETE.name(), OPTIONS.name()));

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

}
