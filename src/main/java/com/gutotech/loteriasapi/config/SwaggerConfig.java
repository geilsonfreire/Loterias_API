// Importações do pacote SwaggerConfig
package com.gutotech.loteriasapi.config;

// Importações do pacote OpenAPI
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


// Importações do pacote Info e Contact e License do pacote OpenAPI
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loterias API REST")
                        .description("API Gratuita de resultados de jogos das Loterias CAIXA.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Geilson Freire")
                                .email("Geilsonfreireleite@gmail.com")
                                .url("https://github.com/geilsonfreire")
                            )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")
                        )
                );
                
    }
}
