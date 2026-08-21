package mx.net.asp.asp_pago_onb.utilerias;

import mx.net.asp.asp_pago_onb.request.ValidacionDeCorreoReq;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidacionDeCorreoReqValidator implements ConstraintValidator<ValidacionDeCorreoCondicional, ValidacionDeCorreoReq> {

    @Override
    public boolean isValid(ValidacionDeCorreoReq req, ConstraintValidatorContext context) {
        if (req == null || req.getAccion() == null) {
            return false;
        }

        boolean valido = true;
        context.disableDefaultConstraintViolation();

        if ("1".equals(req.getAccion())) {
            valido &= validarCampo(req.getToken(), "token", context);
            valido &= validarCampo(req.getNombre(), "nombre", context);
        }

        return valido;
    }

    private boolean validarCampo(String valor, String nombreCampo, ConstraintValidatorContext context) {
        if (valor == null || valor.trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("El campo '" + nombreCampo + "' es obligatorio")
                    .addPropertyNode(nombreCampo)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
