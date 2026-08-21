package mx.net.asp.asp_pago_onb.utilerias;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidacionDeCorreoReqValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidacionDeCorreoCondicional {
    String message() default "Campos obligatorios según el valor de 'accion' no están completos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
