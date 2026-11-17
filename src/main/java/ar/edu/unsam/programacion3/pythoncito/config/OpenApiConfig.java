
package ar.edu.unsam.programacion3.pythoncito.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        String esquemaSeguridad = "Bearer Authentication";

        return new OpenAPI()
                .info(new Info()
                        .title("Pythoncito API REST")
                        .version("1.0.0")
                        .description("API REST para la plataforma educativa Pythoncito (TPI Programación III - UNSAM). "
                                + "Aprendizaje de programación en Python mediante bloques lógicos bajo lineamientos DUA.")
                        .contact(new Contact()
                                .name("Equipo CSS")
                                .email("equipo-css@unsam.edu.ar")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(esquemaSeguridad))
                .components(new Components()
                        .addSecuritySchemes(esquemaSeguridad,
                                new SecurityScheme()
                                        .name(esquemaSeguridad)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
