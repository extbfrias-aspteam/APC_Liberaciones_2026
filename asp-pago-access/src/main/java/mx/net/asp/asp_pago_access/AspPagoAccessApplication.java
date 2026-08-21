package mx.net.asp.asp_pago_access;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("mx.net.asp.asp_pago_access")
@SpringBootApplication
public class AspPagoAccessApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspPagoAccessApplication.class, args);
	}

}
