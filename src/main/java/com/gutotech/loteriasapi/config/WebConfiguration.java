package com.gutotech.loteriasapi.config;

// Importação das classes necessárias.
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Configuração para permitir requisições de qualquer origem.
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    // Método para adicionar mapeamentos de CORS.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }
}