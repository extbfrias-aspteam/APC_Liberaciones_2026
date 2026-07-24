package mx.net.asp.asp_pago_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("mx.net.asp.asp_pago_management")
@SpringBootApplication
public class AspPagoManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspPagoManagementApplication.class, args);
	}

}