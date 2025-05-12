package com.java.demo.ecommerceapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EcommerceApiApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

	@Test
	void testHealthEndpoint() {
		ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/health", Map.class); // Cambia String.class a Map.class
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("UP", response.getBody().get("status")); // Verifica el valor del campo "status"
	}

	@Test
    void testMain() {
        // Capturamos la salida estándar (System.out) para verificar si se imprime algo.
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Ejecutamos el método main de la aplicación.
        ConfigurableApplicationContext context = SpringApplication.run(EcommerceApiApplication.class, new String[]{});

        // Verificamos que el contexto de la aplicación esté activo.
        assertTrue(context.isActive());

        // Aquí podrías agregar más aserciones para verificar el estado de la aplicación,
        // logs capturados, etc., si es necesario.
        String output = outContent.toString();
        // Por ejemplo: assertTrue(output.contains("Started EcommerceApiApplication"));

        // Cerramos el contexto de la aplicación después de la prueba.
        context.close();
    }
}
