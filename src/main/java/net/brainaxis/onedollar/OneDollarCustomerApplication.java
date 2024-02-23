package net.brainaxis.onedollar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@SpringBootApplication
public class OneDollarCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OneDollarCustomerApplication.class, args);
	}

}
