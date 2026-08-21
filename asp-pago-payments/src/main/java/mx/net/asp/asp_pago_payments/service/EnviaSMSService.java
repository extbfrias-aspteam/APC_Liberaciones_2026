package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.*;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.ws.asp.MSEnviarSMS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class EnviaSMSService {

    private final SolicitanteDAO solicitanteDAO;
    private final MSEnviarSMS msEnviarSMS;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Value("${operacion.notificacion.codi.sms}")
    private String operacionCoDiSMS;

    @Async
    public CompletableFuture<RespuestaDTO> procesaSMSTraspaso(SpeiSimpleOBJ speiSimple, boolean devolucion) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //SMS origen
            enviarSMS(speiSimple.getCuentaOrigen(), "CARGO",
                    speiSimple.getMonto(), "TRASPASO", speiSimple.getIdClienteOrigen(),
                    "SI".equalsIgnoreCase(speiSimple.getTienePlasticoOrigen()));
            if (!devolucion) {
                //SMS destino
                enviarSMS(speiSimple.getCuentaDestino(), "ABONO",
                        speiSimple.getMonto(), "TRASPASO", speiSimple.getIdClienteDestino(),
                        "SI".equalsIgnoreCase(speiSimple.getTienePlasticoDestino()));
            } else {
                //SMS devolción
                enviarSMS(speiSimple.getCuentaOrigen(), "DEVOLUCION",
                        speiSimple.getMonto(), "TRASPASO", speiSimple.getIdClienteOrigen(),
                        "SI".equalsIgnoreCase(speiSimple.getTienePlasticoOrigen()));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    @Async
    public CompletableFuture<RespuestaDTO> procesaSMSSinOrigen(SpeiSimpleOBJ speiSimple, boolean devolucion, String tipoPago) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //SMS origen
            if (!devolucion) {
                respuesta = enviarSMS(speiSimple.getCuentaDestino(), "ABONO",
                        speiSimple.getMonto(), tipoPago, speiSimple.getIdClienteDestino(),
                        "SI".equalsIgnoreCase(speiSimple.getTienePlasticoDestino()));
            } else {
                //SMS devolción
                respuesta = enviarSMS(speiSimple.getCuentaDestino(), "DEVOLUCION",
                        speiSimple.getMonto(), tipoPago, speiSimple.getIdClienteDestino(),
                        "SI".equalsIgnoreCase(speiSimple.getTienePlasticoDestino()));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    @Async
    public CompletableFuture<RespuestaDTO> procesaSMSSinDestino(String cuenta, SpeiSimpleOBJ speiSimple, boolean devolucion, String tipoPago) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //SMS origen
            respuesta = enviarSMS(cuenta, "CARGO",
                    speiSimple.getMonto(), tipoPago, speiSimple.getIdClienteOrigen(),
                    "SI".equalsIgnoreCase(speiSimple.getTienePlasticoOrigen()));
            if (devolucion) {
                //SMS devolción
                respuesta = enviarSMS(cuenta, "DEVOLUCION",
                        speiSimple.getMonto(), tipoPago, speiSimple.getIdClienteOrigen(),
                        "SI".equalsIgnoreCase(speiSimple.getTienePlasticoOrigen()));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return CompletableFuture.completedFuture(respuesta);
    }

    public RespuestaDTO enviarSMS(String cuenta, String tipoMov, Double monto, String tipoPago, String personaId, Boolean plastico) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (Boolean.TRUE.equals(plastico)) {
                if (tipoPago.equals("CREDITO"))
                    log.info("Se omite el envio de SMS debido a que la cuenta asociada al credito {} tiene plastico asociado", cuenta);
                else
                    log.info("Se omite el envio de SMS debido a que la cuenta {} tiene plastico asociado", cuenta);
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(null);
                return respuesta;
            }
            // Procesar el envío de SMS
            respuesta = procesarEnvioSMS(cuenta, tipoMov, monto, tipoPago, personaId);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesarEnvioSMS(String cuenta, String tipoMov, Double monto, String tipoPago, String personaId) {
        // Lógica de procesamiento basada en el tipo de cuenta para obtener los datos del cliente
        RespuestaDTO respuesta = new RespuestaDTO();
        NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));
        String mensaje;
        String celular;
        try {
            mensaje = switch (tipoPago) {
                case "CREDITO" -> tipoMov.equals("DEVOLUCION")
                        ? generaSMSDevolucionPagoCredito(cuenta, monto, formatoImporte)
                        : generaSMSPagoCredito(cuenta, monto, formatoImporte);
                default -> generaSMSTransaccion(cuenta, tipoMov, monto, formatoImporte);
            };
            //obtenemos el celular del cliente
            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteById(personaId);
            if (solicitanteOBJ != null) {
                celular = solicitanteOBJ.getTelefonoCoDi();
            } else {
                log.info("No fue posible obtener el celular del cliente {}", personaId);
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No fue posible obtener el celular del cliente " + personaId);
                respuesta.setData(null);
                return respuesta;
            }
            log.info("Se enviara SMS de {} a {}", tipoMov, celular);
            msEnviarSMS.enviarSMS(celular, mensaje, personaId, operacionCoDiSMS);
            respuesta.setCodigo(0);
            respuesta.setMensaje("OK");
            respuesta.setData(null);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private String generaSMSTransaccion(String cuenta, String tipoMovimiento, Double monto, NumberFormat formatoImporte) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fecha = LocalDateTime.now().format(formatter);
        return String.format(
                "%s a su cuenta ******%s por %s M.N. el %s CONTACTO: 8004627373",
                tipoMovimiento, cuenta.substring(cuenta.length() - 4), formatoImporte.format(monto),
                fecha
        );
    }

    private String generaSMSPagoCredito(String contrato, Double monto, NumberFormat formatoImporte) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fecha = LocalDateTime.now().format(formatter);
        return String.format(
                "PAGO DE CREDITO a %s por %s M.N. el %s CONTACTO: 8004627373",
                contrato, formatoImporte.format(monto),
                fecha
        );
    }

    private String generaSMSDevolucionPagoCredito(String contrato, Double monto, NumberFormat formatoImporte) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fecha = LocalDateTime.now().format(formatter);
        return String.format(
                "DEVOLUCION PAGO DE CREDITO a %s por %s M.N. el %s CONTACTO: 8004627373",
                contrato, formatoImporte.format(monto),
                fecha
        );
    }
}