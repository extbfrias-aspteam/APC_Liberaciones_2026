package mx.net.asp.asp_pago_management.utilerias;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mx.net.asp.asp_pago_management.request.ValidaTelefonoReq;

public class ValidaTelefonoReqValidator implements ConstraintValidator<ValidacionTelefonoCondicional, ValidaTelefonoReq> {

    private final String campoString = "El campo '";

    @Override
    public boolean isValid(ValidaTelefonoReq req, ConstraintValidatorContext context) {
        if (req == null || req.getValidaCuenta() == null) {
            return false;
        }

        boolean valido = true;
        context.disableDefaultConstraintViolation();

        if (req.getValidaCuenta() == 1) {
            valido &= validarLength(req.getCurp(), "curp", context);
        } else if (req.getValidaCuenta() == 2) {
            valido &= validarCampo(req.getIdDispositivo(), "idDispositivo", context);
        }

        return valido;
    }

    private boolean validarCampo(String valor, String nombreCampo, ConstraintValidatorContext context) {
        if (valor == null || valor.trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate(campoString + nombreCampo + "' es obligatorio")
                    .addPropertyNode(nombreCampo)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private boolean validarLength(String curp, String nombreCampo, ConstraintValidatorContext context) {
        if (curp == null || curp.trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate(campoString + nombreCampo + "' es obligatorio")
                    .addPropertyNode(nombreCampo)
                    .addConstraintViolation();
            return false;
        }

        if (curp.length() != 18) {
            context.buildConstraintViolationWithTemplate(campoString + nombreCampo + "' debe tener 18 caracteres.")
                    .addPropertyNode(nombreCampo)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
