package mx.net.asp.asp_pago_access.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_access.dao.*;
import mx.net.asp.asp_pago_access.dto.RespuestaDTO;
import mx.net.asp.asp_pago_access.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_access.utilerias.BitacoraProcesamiento;
import mx.net.asp.asp_pago_access.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_access.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_access.utilerias.errores.ErroresGenerales;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Service
@Log4j2
@AllArgsConstructor
public class VerificacionService {

    private final CoDiDAO codiDAO;
    private final Gson gson;
    private final Environment env;
    private final BitacoraProcesamiento bitacoraProcesamiento;
    private final ErrorHandler errorHandler;

    public RespuestaDTO procesaVerificacion(RegistroCoDiOBJ registroCoDi, Boolean correcto, Integer origenValidacion) {
        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            switch (origenValidacion) {
                case 2:
                    if (correcto) {
                        RegistroCoDiOBJ registroCoDiAux = new RegistroCoDiOBJ();
                        registroCoDiAux.setBloqueado(0);
                        registroCoDiAux.setIntentos(0);
                        registroCoDiAux.setFechaBloqueo(null);
                        registroCoDiAux.setBloqueosTemporales(0);
                        registroCoDiAux.setCuenta(registroCoDi.getCuenta());
                        registroCoDiAux.setSolicitanteId(registroCoDi.getSolicitanteId());

                        codiDAO.actualizarBloqueoAccesoAspPago(registroCoDiAux);
                    } else {
                        registroCoDi = actualizaEstatusBloqueo(registroCoDi);
                        /*/////////////////REGISTRO DE BITACORA\\\\\\\\\\\\\\\\\\\\\\\\*/
                        String uuidGlobal = UUID.randomUUID().toString();
                        String uuidIndividual = UUID.randomUUID().toString();
                        bitacoraProcesamiento.inicializaRegistroBitacora(uuidIndividual, registroCoDi.getCuenta(), env.getProperty("datos.incorrectos.login", Integer.class));
                        /*/////////////////REGISTRO DE BITACORA\\\\\\\\\\\\\\\\\\\\\\\\*/
                        bitacoraProcesamiento.finalizaRegistroBitacora(uuidIndividual, uuidGlobal);
                        bitacoraProcesamiento.registraBitacoraProcesamiento(uuidGlobal, registroCoDi.getCuenta());
                        /*/////////////////REGISTRO DE BITACORA\\\\\\\\\\\\\\\\\\\\\\\\*/
                        respuesta.setCodigo(-100);
                    }
                    break;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RegistroCoDiOBJ actualizaEstatusBloqueo(RegistroCoDiOBJ registroCoDi) {
        registroCoDi.setIntentos(registroCoDi.getIntentos() + 1);
        //Valida si ya llego a los 5 intentos
        if (registroCoDi.getIntentos() > 4) {
            //Valida si llego al 6to intento, significa que ya supero los 5 permitidos y se le reinicia a 1 (previamente ya tiene un bloqueo temporal)
            //En caso de que Intentos sea igual a 5, se le sumara +1 al bloqueo temporal
            if (registroCoDi.getIntentos() == 6) {
                registroCoDi.setIntentos(1);
            } else {
                registroCoDi.setBloqueosTemporales(registroCoDi.getBloqueosTemporales() + 1);
                registroCoDi.setFechaBloqueo(Timestamp.from(Instant.now()));
                //Si es el 2do bloqueo temporal, se le bloquea el acceso de forma permantente a la app
                if (registroCoDi.getBloqueosTemporales() > 1) {
                    registroCoDi.setBloqueado(registroCoDi.getBloqueado() + 1);
                }
            }
        }
        codiDAO.actualizarBloqueoAccesoAspPago(registroCoDi);
        return registroCoDi;
    }
}
