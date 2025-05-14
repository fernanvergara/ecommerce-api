package com.java.demo.ecommerceapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "API para ecommerce",
                version = "v1",
                description = "Esta es la documentación de la API para un gran proyecto de spring.",
                termsOfService = "Términos de servicio",
                contact = @Contact(
                        name = "Fernan Vergara",
                        email = "fernanvergara@gmail.com"
                ),
                license = @License(
                        name = "MIT"
                )
        )
)
public class EcommerceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApiApplication.class, args);
	}

}
