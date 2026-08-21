package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.*;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.request.RegistroDeCuentaSimplificadaReq;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresRegistroCtaSimp;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Log4j2
public class OnboardingLockService {

    private static final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    public static RespuestaDTO validaOnboardingPendiente(RegistroDeCuentaSimplificadaReq req, CoDiDAO codiDAO, Gson gson, ErrorHandler errorHandler) {
        RespuestaDTO respuesta = new RespuestaDTO();
        // Clave única basada en RFC, CURP y Celular
        String lockKey = req.getRfc() + "_" + req.getCurp() + "_" + req.getCelular();
        ReentrantLock lock = lockMap.computeIfAbsent(lockKey, key -> new ReentrantLock());
        lock.lock(); // Bloquea solo para esta clave
        try {
            // Verificar si ya hay un operacion pendiente
            OnboardingEjecutadoOBJ onboarding = codiDAO.buscaOnboardingEjecutadoPendiente(req.getRfc(), req.getCurp(), req.getCelular());
            if (onboarding != null) {
                try {
                    Instant tiempoActual = Instant.now();
                    Instant tiempoCreacion = onboarding.getFechaCreacion().toInstant();
                    // Verifica si ya pasaron 15 minutos
                    if (tiempoCreacion.plusSeconds(15 * 60).isBefore(tiempoActual)) {
                        log.info("*** Han pasado mas de 15 minutos desde la ultima operacion ejecutada, se procede a finalizarla");
                        // Finalizar operacion anterior
                        codiDAO.finalizaOnboardingEjecutado(onboarding.getId());
                        // Registrar nueva operacion
                        respuesta = codiDAO.registraOnboardingEjecutado(req.getRfc(), req.getCurp(), req.getCelular());
                        if (respuesta.getCodigo() == 0) {
                            Integer idOnbEjecutado = Integer.valueOf(respuesta.getData());
                            RespuestaUtils.respuestaExito(respuesta, String.valueOf(idOnbEjecutado));
                        } else {
                            //No se logro registrar la operacion pendiente, pero dejamos continuar el flujo
                            RespuestaUtils.respuestaExito(respuesta);
                        }
                    } else {
                        // Si no han pasado 15 minutos, devolver error
                        RespuestaUtils.asignarError(respuesta,
                                ErroresRegistroCtaSimp.ONBOARDING_PENDIENTE,
                                ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ONBOARDING_PENDIENTE));
                    }
                } catch (Exception e) {
                    errorHandler.handleException(e);
                    RespuestaUtils.asignarError(respuesta,
                            ErroresGenerales.ERROR_INTERNO,
                            ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
                }
            } else {
                // Si no hay onboarding pendiente, registrar uno nuevo
                respuesta = codiDAO.registraOnboardingEjecutado(req.getRfc(), req.getCurp(), req.getCelular());
                if (respuesta.getCodigo() == 0) {
                    Integer idOnbEjecutado = Integer.valueOf(respuesta.getData());
                    RespuestaUtils.respuestaExito(respuesta, String.valueOf(idOnbEjecutado));
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