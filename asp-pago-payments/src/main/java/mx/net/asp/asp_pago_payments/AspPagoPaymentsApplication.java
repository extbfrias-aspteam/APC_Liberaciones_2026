package mx.net.asp.asp_pago_payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("mx.net.asp.asp_pago_payments")
@SpringBootApplication
public class AspPagoPaymentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspPagoPaymentsApplication.class, args);
	}

}