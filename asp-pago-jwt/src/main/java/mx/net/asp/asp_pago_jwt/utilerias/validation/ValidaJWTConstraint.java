package mx.net.asp.asp_pago_jwt.utilerias.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidaJWTValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidaJWTConstraint {
    String message() default "Datos inválidos para el tipo de endpoint";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
