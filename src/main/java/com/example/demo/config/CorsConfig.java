package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebSecurity
@Configuration
public class CorsConfig implements WebMvcConfigurer {
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			
			String origen="https://rafacm23.github.io";
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				
				registry.addMapping("/comentario")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/reserva")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/verifica")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/vacaciones")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/ocupado")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				
				registry.addMapping("/isAdmin")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/mifoto")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/misdatos")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/rechaza")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/profesionales")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/allprofesionales")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/profesional")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/whois")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/mes/")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/correoOcupado")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				registry.addMapping("/auth/login")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				
				registry.addMapping("/auth/register")
				.allowedOrigins(origen)
				.allowedHeaders("*");
				
				
				
			

			}
		};
	}


}