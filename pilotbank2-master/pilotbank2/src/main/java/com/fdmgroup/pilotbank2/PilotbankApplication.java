package com.fdmgroup.pilotbank2;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PilotbankApplication {

	public static void main(String[] args) {
		SpringApplication.run(PilotbankApplication.class, args);
	}

	@Bean
	public OpenAPI springPilotBankOpenAPI(){
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("jwt-bearer",
							new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
							.in(SecurityScheme.In.HEADER).name("Authorization")))
				.info(new Info().title("Pilot Bank API")
				.description("High flying banking application")
				.version("v0.0.1")
				.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}

}
