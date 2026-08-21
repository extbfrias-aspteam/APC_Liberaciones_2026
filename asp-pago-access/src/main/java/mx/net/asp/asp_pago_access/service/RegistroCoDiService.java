package mx.net.asp.asp_pago_access.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_access.utilerias.*;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresLogIn;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresPassword;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class RegistroCoDiService {

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO validaRegistroCoDiBase(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();

        if (registroCoDiOBJ == null) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresPassword.REGISTRO_CODI_NOT_FOUND,
                    ErroresPassword.descError.get(ErroresPassword.REGISTRO_CODI_NOT_FOUND));
        }

        //Validamos que la cuente este presente en la nueva versión de la app
        if (!versionApp.equals(registroCoDiOBJ.getRegistroNuevaApp())) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresPassword.VERSION_APP,
                    ErroresPassword.descError.get(ErroresPassword.VERSION_APP));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    public RespuestaDTO validaRegistroCoDiCompleto(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();

        respuesta = validaRegistroCoDiBase(registroCoDiOBJ);
        if (respuesta.getCodigo() != 0)
            return respuesta;

        if (!registroCoDiOBJ.getTelefono_validado().equalsIgnoreCase("SI")) {
            log.info("*** Telefono NO validado");
            return RespuestaUtils.asignarError(respuesta,
                    ErroresLogIn.TELEFONO_NO_VALIDADO,
                    ErroresLogIn.descError.get(ErroresLogIn.TELEFONO_NO_VALIDADO));
        }

        if (registroCoDiOBJ.getBloqueado() == 1) {
            log.info("*** Registro Bloqueado");
            return RespuestaUtils.asignarError(new RespuestaDTO(),
                    ErroresLogIn.REG_CODI_BLOQUEADO,
                    ErroresLogIn.descError.get(ErroresLogIn.REG_CODI_BLOQUEADO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }
}