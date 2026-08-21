package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.*;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.utilerias.DateAdapter;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class AhorroActualizaSaldoService {

    private final Gson gson;
    private final Environment env;
    private final ErrorHandler errorHandler;
    private final ProcreaDAO procreaDAO;
    private final MovimientosCajaDAO movimientosCajaDAO;

    public RespuestaDTO ahorroActualizasaldo(Date fecha, String cuenta, Double monto, String operacion, AhorroContratoOBJ ahorroContrato) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            boolean continua = false;
            Boolean actualiza;

            AhorroSaldosOBJ saldos = null;
            AhorroRendimientoVigentesOBJ ahorroRendimientoVigentes;

            respuesta = movimientosCajaDAO.obtenerAhorroSaldoByCuentaAh(cuenta);
            if (respuesta.getCodigo() == 0) {
                saldos = gson.fromJson(respuesta.getData(), AhorroSaldosOBJ.class);
            }

            if (saldos != null)
                if (!StringUtils.isBlank(saldos.getCuenta()))
                    continua = true;

            if ((!continua) && operacion.equals("+")) {
                saldos = new AhorroSaldosOBJ();
                saldos.setCuenta(cuenta);
                saldos.setSolicitanteId(ahorroContrato.getSolicitante());
                saldos.setSaldoReal(monto);
                saldos.setSaldoPromedio((double) 0);
                saldos.setSaldoAcumulado((double) 0);
                saldos.setDias(0);
                saldos.setIntereses((double) 0);
                saldos.setIva((double) 0);
                saldos.setIsr((double) 0);
                saldos.setRetenciones((double) 0);
                saldos.setSaldoDisponible(monto);
                saldos.setFechaDeposito(fecha);

                respuesta = movimientosCajaDAO.registraAhorroSaldos(saldos);
                if (respuesta.getCodigo() == 0) {
                    saldos.setAhorroSaldosId(Integer.valueOf(respuesta.getData()));

                    ahorroContrato.setSaldo(ahorroContrato.getSaldo() + monto);

                    procreaDAO.actualizarSaldoAhorroContrato(ahorroContrato.getCuenta(), ahorroContrato.getSaldo());
                    respuesta = procreaDAO.obtenerRendimientoVigenteByCuentaAh(cuenta);
                    if (respuesta.getCodigo() == 0) {
                        ahorroRendimientoVigentes = gson.fromJson(respuesta.getData(), AhorroRendimientoVigentesOBJ.class);
                        if (ahorroRendimientoVigentes != null) {
                            if (ahorroRendimientoVigentes.getEstatus() != null) {
                                if (ahorroRendimientoVigentes.getEstatus().equals("P")) {
                                    ahorroRendimientoVigentes.setFechaDeposito(fecha);
                                    ahorroRendimientoVigentes.setCapital(monto);
                                    ahorroRendimientoVigentes.setFechaInicio(fecha);
                                    Date fechaFinal = diaHabil(fecha, ahorroRendimientoVigentes.getPlazo(), "D");
                                    ahorroRendimientoVigentes.setFechaFinal(fechaFinal);
                                    ahorroRendimientoVigentes.setEstatus("A");

                                    procreaDAO.actualizarRendimientoVigenteById(ahorroRendimientoVigentes);
                                }
                            }
                        }
                    }
                } else {
                    respuesta.setCodigo(-100);
                    respuesta.setMensaje("Ocurrio un error al registrar el saldo de la cuenta");
                    respuesta.setData(null);
                }
            } else {
                if (operacion.equals("+")) {
                    ahorroContrato.setSaldo(ahorroContrato.getSaldo() + monto);
                    procreaDAO.actualizarSaldoAhorroContrato(ahorroContrato.getCuenta(), ahorroContrato.getSaldo());

                    saldos.setSaldoDisponible(saldos.getSaldoDisponible() + monto);
                    saldos.setSaldoReal(saldos.getSaldoReal() + monto);
                    saldos.setSaldoAcumulado(saldos.getSaldoAcumulado() + monto);

                    respuesta = movimientosCajaDAO.actualizaAhorroSaldos(saldos);
                    if (respuesta.getCodigo() == 0) {
                        respuesta = procreaDAO.obtenerRendimientoVigenteByCuentaAh(cuenta);
                        if (respuesta.getCodigo() == 0) {
                            Gson gsonDate = new GsonBuilder()
                                    .registerTypeAdapter(Date.class, new DateAdapter())
                                    .create();
                            ahorroRendimientoVigentes = gsonDate.fromJson(respuesta.getData(), AhorroRendimientoVigentesOBJ.class);
                            if (ahorroRendimientoVigentes.getEstatus() != null) {
                                if (ahorroRendimientoVigentes.getEstatus().equals("P")) {
                                    ahorroRendimientoVigentes.setFechaDeposito(fecha);
                                    ahorroRendimientoVigentes.setCapital(monto);
                                    ahorroRendimientoVigentes.setFechaInicio(fecha);
                                    Date fechaFinal = diaHabil(fecha, ahorroRendimientoVigentes.getPlazo(), "D");
                                    ahorroRendimientoVigentes.setFechaFinal(fechaFinal);
                                    ahorroRendimientoVigentes.setEstatus("A");

                                    procreaDAO.actualizarRendimientoVigenteById(ahorroRendimientoVigentes);
                                }
                            }
                        }
                    } else {
                        respuesta.setCodigo(-101);
                        respuesta.setMensaje("Ocurrio un error al actualizar el saldo de la cuenta");
                        respuesta.setData(null);
                    }
                } else {
                    ahorroContrato.setSaldo(ahorroContrato.getSaldo() - monto);
                    procreaDAO.actualizarSaldoAhorroContrato(ahorroContrato.getCuenta(), ahorroContrato.getSaldo());

                    saldos.setSaldoDisponible(saldos.getSaldoDisponible() - monto);
                    saldos.setSaldoReal(saldos.getSaldoReal() - monto);
                    saldos.setSaldoAcumulado(saldos.getSaldoAcumulado() - monto);
                    respuesta = movimientosCajaDAO.actualizaAhorroSaldos(saldos);
                    if (respuesta.getCodigo() == 0) {

                    } else {
                        respuesta.setCodigo(-101);
                        respuesta.setMensaje("Ocurrio un error al actualizar el saldo de la cuenta");
                        respuesta.setData(null);
                    }
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private Date diaHabil(Date fecha, Integer plazo, String string) {
        Calendar calendar = Calendar.getInstance();
        List<Date> fechasInhabiles = new ArrayList<>();
        calendar.setTime(fecha);
        boolean continua = false;
        while (!continua) {
            if (calendar.get(calendar.DAY_OF_WEEK) == 6) {
                calendar.add(Calendar.DAY_OF_YEAR, (2));
            } else if (calendar.get(calendar.DAY_OF_WEEK) == 0) {
                calendar.add(Calendar.DAY_OF_YEAR, (1));
            }
            if (fechasInhabiles.contains(calendar.getTime())) {
                calendar.add(Calendar.DAY_OF_YEAR, (1));
            } else {
                continua = true;
            }
        }
        return calendar.getTime();
    }
}