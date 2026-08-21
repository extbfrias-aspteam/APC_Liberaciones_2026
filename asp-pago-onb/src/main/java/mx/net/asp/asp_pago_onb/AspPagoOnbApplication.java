package mx.net.asp.asp_pago_onb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("mx.net.asp.asp_pago_onb")
@SpringBootApplication
public class AspPagoOnbApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspPagoOnbApplication.class, args);
	}

}