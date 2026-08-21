package mx.net.asp.asp_pago_jwt.utilerias;

import mx.net.asp.asp_pago_jwt.config.JwtCanalesExternosProperties;
import mx.net.asp.asp_pago_jwt.config.JwtEndpointsExcluidosProperties;
import mx.net.asp.asp_pago_jwt.config.JwtEndpointsExternosProperties;
import mx.net.asp.asp_pago_jwt.request.ValidaJWTReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdentificadorHelper {

    private final JwtEndpointsExcluidosProperties excluidos;
    private final JwtEndpointsExternosProperties externos;
    private final JwtCanalesExternosProperties canalesExternos;

    @Value("${path.login.version}")
    private String pathLogInV;
    @Value("${path.pwd.reset}")
    private String pathPwdReset;

    public IdentificadorHelper(JwtEndpointsExcluidosProperties excluidos, JwtEndpointsExternosProperties externos,
                               JwtCanalesExternosProperties canalesExternos) {
        this.excluidos = excluidos;
        this.externos = externos;
        this.canalesExternos = canalesExternos;
    }

    public enum TipoIdentificador {
        TELEFONO,
        PERSONA_ID,
        ID_CANAL,
        NINGUNO
    }

    public TipoIdentificador determinarIdentificador(ValidaJWTReq req) {
        String endpoint = req.getEndpoint();
        String idCanal = String.valueOf(req.getIdCanal());

        if (endpoint == null) {
            return TipoIdentificador.NINGUNO;
        }

        if (endpoint.contains(pathLogInV) || endpoint.contains(pathPwdReset)) {
            return TipoIdentificador.TELEFONO;
        }

        if (externos.getExternos()
                .stream()
                .anyMatch(externo -> endpoint.contains(externo))) {
            return TipoIdentificador.ID_CANAL;
        }

        if (canalesExternos.getExternos()
                .stream()
                .anyMatch(canal -> idCanal.equals(canal))) {
            return TipoIdentificador.ID_CANAL;
        }

        if (!excluidos.getExcluidos()
                .stream()
                .anyMatch(excluido -> endpoint.contains(excluido))) {
            return TipoIdentificador.PERSONA_ID;
        }

        return TipoIdentificador.NINGUNO;
    }
}
