package mx.net.asp.asp_pago_services_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("mx.net.asp.asp_pago_services_v2")
@SpringBootApplication
public class AspPagoServicesV2Application {

	public static void main(String[] args) {
		SpringApplication.run(AspPagoServicesV2Application.class, args);
	}

}
