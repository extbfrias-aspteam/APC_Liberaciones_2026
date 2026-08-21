package mx.net.asp.asp_pago_access.utilerias;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mx.net.asp.asp_pago_access.request.GeneraPinOTPReq;

public class GeneraPinOTPValidator implements ConstraintValidator<ValidacionGeneraPinOTP, GeneraPinOTPReq> {

    @Override
    public boolean isValid(GeneraPinOTPReq req, ConstraintValidatorContext context) {
        if (req == null || req.getClave() == null) return false;

        String clave = req.getClave().trim();
        String valor = req.getValor();

        context.disableDefaultConstraintViolation(); // Desactiva el mensaje por defecto

        switch (clave) {
            case "TEL" -> {
                if (valor == null || !valor.matches("\\d{10}")) {
                    context.buildConstraintViolationWithTemplate("El campo 'valor' debe ser un teléfono de 10 dígitos numéricos.")
                            .addPropertyNode("valor").addConstraintViolation();
                    return false;
                }
            }
            case "CTA" -> {
                if (valor == null || !valor.matches("\\d{10}")) {
                    context.buildConstraintViolationWithTemplate("El campo 'valor' debe ser una cuenta de 10 dígitos numéricos.")
                            .addPropertyNode("valor").addConstraintViolation();
                    return false;
                }
            }
            default -> {
                context.buildConstraintViolationWithTemplate("Clave no reconocida.")
                        .addPropertyNode("clave").addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}