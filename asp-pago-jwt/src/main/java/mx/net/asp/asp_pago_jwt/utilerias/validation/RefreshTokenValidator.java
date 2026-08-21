package mx.net.asp.asp_pago_jwt.utilerias.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mx.net.asp.asp_pago_jwt.config.JwtEndpointsExternosProperties;
import mx.net.asp.asp_pago_jwt.request.RefreshTokenReq;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenValidator implements ConstraintValidator<RefreshTokenConstraint, RefreshTokenReq> {

    private final JwtEndpointsExternosProperties externos;

    public RefreshTokenValidator(JwtEndpointsExternosProperties externos) {
        this.externos = externos;
    }

    @Override
    public boolean isValid(RefreshTokenReq request, ConstraintValidatorContext context) {
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
