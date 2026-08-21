package mx.net.asp.asp_pago_jwt.service;

import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_jwt.dao.CoDiDAO;
import mx.net.asp.asp_pago_jwt.dao.SeguridadDAO;
import mx.net.asp.asp_pago_jwt.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_jwt.dto.RespuestaDTO;
import mx.net.asp.asp_pago_jwt.model.*;
import mx.net.asp.asp_pago_jwt.reponse.JwtTokenResp;
import mx.net.asp.asp_pago_jwt.request.LoginAuthReq;
import mx.net.asp.asp_pago_jwt.request.RefreshTokenReq;
import mx.net.asp.asp_pago_jwt.request.RevocaJwtTokenClienteReq;
import mx.net.asp.asp_pago_jwt.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_jwt.utilerias.JwtUtil;
import mx.net.asp.asp_pago_jwt.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresJwtToken;
import mx.net.asp.asp_pago_jwt.utilerias.errores.ErroresRevocaJwtToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class RevocaJwtTokenService {

    private final SeguridadDAO seguridadDAO;
    private final CoDiDAO codiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final ErrorHandler errorHandler;

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO revocaJwtTokenCliente(RevocaJwtTokenClienteReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getTelefono());
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRevocaJwtToken.SOLICITANTE_NOT_FOUND,
                        ErroresRevocaJwtToken.getMensajeError(ErroresRevocaJwtToken.SOLICITANTE_NOT_FOUND, req.getTelefono()));
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(solicitanteOBJ.getNumero());
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            seguridadDAO.revocaAccessTokenPorTelefono(req.getTelefono());
            seguridadDAO.revocaRefreshTokenPorTelefono(req.getTelefono());

            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }
    private RespuestaDTO validaRegistroCoDi(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO RespuestaDTO = new RespuestaDTO();

        if (registroCoDiOBJ == null) {
            return RespuestaUtils.asignarError(RespuestaDTO,
                    ErroresRevocaJwtToken.REGISTRO_CODI_NOT_FOUND,
                    ErroresRevocaJwtToken.descError.get(ErroresRevocaJwtToken.REGISTRO_CODI_NOT_FOUND));
        }

        //Validamos que la cuente este presente en la nueva versión de la app
        if (!versionApp.equals(registroCoDiOBJ.getRegistroNuevaApp())) {
            return RespuestaUtils.asignarError(RespuestaDTO,
                    ErroresRevocaJwtToken.VERSION_APP,
                    ErroresRevocaJwtToken.descError.get(ErroresRevocaJwtToken.VERSION_APP));
        }

        return RespuestaUtils.respuestaExito(RespuestaDTO);
    }
}
