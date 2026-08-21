package mx.net.asp.asp_pago_access.utilerias;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = GeneraPinOTPValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidacionGeneraPinOTP {
    String message() default "Solicitud inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

