package mx.net.asp.asp_pago_jwt.utilerias.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mx.net.asp.asp_pago_jwt.config.JwtEndpointsExternosProperties;
import mx.net.asp.asp_pago_jwt.request.LoginAuthReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LoginAuthValidator implements ConstraintValidator<LoginAuthConstraint, LoginAuthReq> {

    private final JwtEndpointsExternosProperties externos;

    @Value("${id.canal.asp.pago}")
    private Integer idCanalAspPago;

    public LoginAuthValidator(JwtEndpointsExternosProperties externos) {
        this.externos = externos;
    }

    @Override
    public boolean isValid(LoginAuthReq request, ConstraintValidatorContext context) {
        if (request == null) return true;

        String endpoint = request.getPath();
        String dispositivoId = request.getDispositivoId();
        String telefono = request.getTelefono();
        String tenantId = request.getTenantId();

        if (endpoint == null) return true;

        // Deshabilita el mensaje por defecto
        context.disableDefaultConstraintViolation();

        if (externos.getExternos()
                .stream()
                .anyMatch(externo -> endpoint.contains(externo))) {
            if (tenantId == null || tenantId.trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("El campo 'tenantId' es obligatorio")
                        .addPropertyNode("tenantId")
                        .addConstraintViolation();
                return false;
            }
        } else if (!idCanalAspPago.equals(request.getIdCanal())){
            if (tenantId == null || tenantId.trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("El campo 'tenantId' es obligatorio")
                        .addPropertyNode("tenantId")
                        .addConstraintViolation();
                return false;
            }
        } else {
            if (telefono == null || telefono.trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("El campo 'telefono' es obligatorio")
                        .addPropertyNode("telefono")
                        .addConstraintViolation();
                return false;
            }

            if (!telefono.matches("\\d{10}")) {
                context.buildConstraintViolationWithTemplate("El campo 'telefono' debe contener exactamente 10 dígitos numéricos")
                        .addPropertyNode("telefono")
                        .addConstraintViolation();
                return false;
            }

            if (dispositivoId == null || dispositivoId.trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("El campo 'dispositivoId' es obligatorio")
                        .addPropertyNode("dispositivoId")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
