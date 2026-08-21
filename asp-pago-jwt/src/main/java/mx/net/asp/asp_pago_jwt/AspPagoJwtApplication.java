package mx.net.asp.asp_pago_jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("mx.net.asp.asp_pago_jwt")
@SpringBootApplication
public class AspPagoJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspPagoJwtApplication.class, args);
	}

}
