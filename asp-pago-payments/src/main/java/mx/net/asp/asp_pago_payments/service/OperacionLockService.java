package mx.net.asp.asp_pago_payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.*;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresPSpeiSimple;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Log4j2
@RequiredArgsConstructor
public class OperacionLockService {

    private static final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public static RespuestaDTO validaOperacionPendiente(String cuenta, CoDiDAO codiDAO, ErrorHandler errorHandler) {
        RespuestaDTO respuesta = new RespuestaDTO();
        // Clave única basada en la cuenta
        String lockKey = cuenta;
        ReentrantLock lock = lockMap.computeIfAbsent(lockKey, key -> new ReentrantLock());
        lock.lock(); // Bloquea solo para esta clave
        try {
            // Verificar si ya hay un operacion pendiente
            OperacionEjecutadaOBJ opearcion = codiDAO.buscaOperacionEjecutadaPendiente(cuenta);
            if (opearcion != null) {
                try {
                    Instant tiempoActual = Instant.now();
                    Instant tiempoCreacion = opearcion.getFechaCreacion().toInstant();
                    // Verifica si ya pasaron 15 minutos
                    if (tiempoCreacion.plusSeconds(15 * 60).isBefore(tiempoActual)) {
                        log.info("*** Han pasado mas de 15 minutos desde la ultima operacion ejecutada, se procede a finalizarla");
                        // Finalizar operacion anterior
                        codiDAO.finalizaOperacionEjecutada(opearcion.getId());
                        OperacionEjecutadaOBJ opearcionNueva = new OperacionEjecutadaOBJ();
                        opearcionNueva.setCuenta(cuenta);
                        opearcionNueva.setIdCatOperacionEjecutada(1);
                        opearcionNueva.setIdOperacion(0);
                        opearcionNueva.setCompletado(0);
                        // Registrar nueva operacion
                        respuesta = codiDAO.registraOperacionEjecutada(opearcionNueva);
                        if (respuesta.getCodigo() == 0) {
                            Integer idOpPendiente = Integer.valueOf(respuesta.getData());
                            RespuestaUtils.respuestaExito(respuesta, String.valueOf(idOpPendiente));
                        } else {
                            //No se logro registrar la operacion pendiente, pero dejamos continuar el flujo
                            RespuestaUtils.respuestaExito(respuesta);
                        }
                    } else {
                        // Si no han pasado 15 minutos, devolver error
                        RespuestaUtils.asignarError(respuesta,
                                ErroresPSpeiSimple.OPERACION_PENDIENTE,
                                ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.OPERACION_PENDIENTE));
                    }
                } catch (Exception e) {
                    errorHandler.handleException(e);
                    RespuestaUtils.asignarError(respuesta,
                            ErroresGenerales.ERROR_INTERNO,
                            ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
                }
            } else {
                // Si no hay operacion pendiente, registrar una nueva
                OperacionEjecutadaOBJ opearcionNueva = new OperacionEjecutadaOBJ();
                opearcionNueva.setCuenta(cuenta);
                opearcionNueva.setIdCatOperacionEjecutada(1);
                opearcionNueva.setIdOperacion(0);
                opearcionNueva.setCompletado(0);
                respuesta = codiDAO.registraOperacionEjecutada(opearcionNueva);
                if (respuesta.getCodigo() == 0) {
                    Integer idOpPendiente = Integer.valueOf(respuesta.getData());
                    RespuestaUtils.respuestaExito(respuesta, String.valueOf(idOpPendiente));
                } else {
                    //No se logro registrar la operacion pendiente, pero dejamos continuar el flujo
                    RespuestaUtils.respuestaExito(respuesta);
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        } finally {
            lock.unlock();
            lockMap.compute(lockKey, (key, existingLock) ->
                    (existingLock != null && existingLock.hasQueuedThreads()) ? existingLock : null
            );
        }
        return respuesta;
    }
}