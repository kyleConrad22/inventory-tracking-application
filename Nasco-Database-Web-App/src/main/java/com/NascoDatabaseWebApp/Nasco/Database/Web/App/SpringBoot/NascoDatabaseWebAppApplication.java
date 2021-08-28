package com.NascoDatabaseWebApp.Nasco.Database.Web.App.SpringBoot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

//@SpringBootApplication is convenience tag which adds the following:
//@Configuration: tags class as source of bean definitions for application context
//@EnableAutoConfigure: tells SpringBoot to start adding beans based on classpath settings, other beans, and various property settings.
//@ComponentScan: tells Spring to look for other components, configurations, and services in the package, letting it find the controllers
@SpringBootApplication
public class NascoDatabaseWebAppApplication {

	//main() uses SpringApplication.run() to launch an application. Note no xml file => web app is 100% Java
	public static void main(String[] args) {
		SpringApplication.run(NascoDatabaseWebAppApplication.class, args);
	}

}
