package mx.net.asp.asp_pago_documents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("mx.net.asp.asp_pago_documents")
@SpringBootApplication
public class AspPagoDocumentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AspPagoDocumentsApplication.class, args);
	}

}