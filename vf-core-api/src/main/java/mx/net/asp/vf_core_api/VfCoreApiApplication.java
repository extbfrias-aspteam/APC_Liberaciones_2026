package mx.net.asp.vf_core_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("mx.net.asp.vf_core_api")
@SpringBootApplication
public class VfCoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(VfCoreApiApplication.class, args);
	}

}