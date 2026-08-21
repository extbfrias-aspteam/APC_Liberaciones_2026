package mx.net.asp.asp_pago_jwt.utilerias.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mx.net.asp.asp_pago_jwt.config.JwtCanalesExternosProperties;
import mx.net.asp.asp_pago_jwt.config.JwtEndpointsExcluidosProperties;
import mx.net.asp.asp_pago_jwt.config.JwtEndpointsExternosProperties;
import mx.net.asp.asp_pago_jwt.dao.SeguridadDAO;
import mx.net.asp.asp_pago_jwt.request.ValidaJWTReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ValidaJWTValidator implements ConstraintValidator<ValidaJWTConstraint, ValidaJWTReq> {

    private final JwtEndpointsExcluidosProperties endpointsExcluidos;
    private final JwtEndpointsExternosProperties endepointsExternos;
    private final JwtCanalesExternosProperties canalesExternos;
    private final SeguridadDAO seguridadDAO;

    @Value("${path.login.version}")
    private String pathLogInV;
    @Value("${path.pwd.reset}")
    private String pathPwdReset;

    public ValidaJWTValidator(JwtEndpointsExcluidosProperties endpointsExcluidos,
                              JwtEndpointsExternosProperties endepointsExternos,
                              JwtCanalesExternosProperties canalesExternos,
                              SeguridadDAO seguridadDAO) {
        this.endpointsExcluidos = endpointsExcluidos;
        this.endepointsExternos = endepointsExternos;
        this.canalesExternos = canalesExternos;
        this.seguridadDAO = seguridadDAO;
    }

    @Override
    public boolean isValid(ValidaJWTReq request, ConstraintValidatorContext context) {
        if (request == null) return true;

        String endpoint = request.getEndpoint();
        String telefono = request.getTelefono();
        String personaId = request.getPersonaId();
        String idCanal = String.valueOf(request.getIdCanal());

        if (endpoint == null) return true;

        // Deshabilita el mensaje por defecto
        context.disableDefaultConstraintViolation();

        if (endpoint.contains(pathLogInV) || endpoint.contains(pathPwdReset)) {
            if (telefono == null || telefono.trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("El campo 'telefono' es obligatorio")
                        .addPropertyNode("telefono")
                        .addConstraintViolation();

                seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", endpoint,
                        request.getJwtToken(), "ERROR", "X-Telefono header missing.",
                        -300, null, request.getIdCanal());

                return false;
            }

            if (!telefono.matches("\\d{10}")) {
                context.buildConstraintViolationWithTemplate("El campo 'telefono' debe contener exactamente 10 dígitos numéricos")
                        .addPropertyNode("telefono")
                        .addConstraintViolation();

                seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", endpoint,
                        request.getJwtToken(), "ERROR", "X-Telefono not matches: " + telefono,
                        -301, null, null);

                return false;
            }
        } else if (canalesExternos.getExternos().stream()
                .anyMatch(canal -> canal.equals(idCanal))) {
            return true;
        } else if (endepointsExternos.getExternos().stream()
                .anyMatch(externo -> endpoint.contains(externo))) {
            return true;
        } else if (!endpointsExcluidos.getExcluidos().stream()
                .anyMatch(excluido -> endpoint.contains(excluido))) {
            if (personaId == null || personaId.trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("El campo 'personaId' es obligatorio")
                        .addPropertyNode("personaId")
                        .addConstraintViolation();

                seguridadDAO.registraJwtTokenLog(null, "ACCESS", "VALIDATION", endpoint,
                        request.getJwtToken(), "ERROR", "X-Id-Persona header missing.",
                        -300, null, request.getIdCanal());

                return false;
            }
        }

        return true;
    }
}
