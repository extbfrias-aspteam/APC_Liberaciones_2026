package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.*;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.utilerias.DateAdapter;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresPSpeiSimple;
import mx.net.asp.asp_pago_payments.ws.asp.WSFondeaTarjetaOrquestador;
import mx.net.asp.asp_pago_payments.ws.asp.WSTraspasoTarjetas;
import mx.net.asp.asp_pago_payments.ws.asp.request.TransaccionTarjetaOrquestadorReq;
import mx.net.asp.asp_pago_payments.ws.asp.request.TraspasoTarjetasReq;
import mx.net.asp.asp_pago_payments.ws.asp.response.TransaccionTarjetaOrquestadorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

@Service
@Log4j2
@RequiredArgsConstructor
public class AhorroDepositoService {

    private final MovimientosCajaDAO movimientosCajaDAO;
    private final ProcreaDAO procreaDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;
    private final WSFondeaTarjetaOrquestador wsFondeaTarjetaOrquestador;
    private final WSTraspasoTarjetas wsTraspasoTarjetas;
    private final AhorroActualizaSaldoService ahorroActualizaSaldoService;

    @Value("${ip.host.transaccion}")
    private String ipHost;
    @Value("${medio.caja.pago.asp}")
    private String medioCajaPago;

    @Value("${usuario.dep.trans.ahorro}")
    private Integer usuarioDepTransAhorro;
    @Value("${caja.dep.trans.ahorro}")
    private Integer cajaDepTransAhorro;
    @Value("${forma.pago.disp.trans.ahorro}")
    private Integer formaPagoDsipTransAhorro;

    public RespuestaDTO procesaDepositoCtaFacil(SpeiSimpleOBJ speiSimple, String cve_movimiento, Integer bancoId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("cve_movimiento:: {}", cve_movimiento);
            TransaccionTarjetaOrquestadorReq reqTra = new TransaccionTarjetaOrquestadorReq();
            HeaderWS header = new HeaderWS();
            header.setIdEmpresa(1L);
            header.setIdCanalAtencion(2L);
            header.setIdSucursal(1L);
            header.setIdUsuario(9L);
            header.setLatitud(speiSimple.getLatitud());
            header.setLongitud(speiSimple.getLongitud());
            header.setIpHost(ipHost);
            header.setIdBanco(String.valueOf(bancoId));
            header.setIdTransaccion(speiSimple.getDatos().getTipoTransaccionId());
            reqTra.setCuenta(speiSimple.getCuentaDestino());
            reqTra.setImporte(String.valueOf(speiSimple.getMonto()));
            reqTra.setMedioPago(medioCajaPago);
            reqTra.setConcepto(speiSimple.getConceptoDestino());
            reqTra.setObservaciones(speiSimple.getConceptoDestino());
            reqTra.setClave_rastreo(speiSimple.getClaveRastreo());
            reqTra.setReferenciaNumerica(speiSimple.getReferenciaNumerica());
            reqTra.setClaveMovimiento(cve_movimiento);
            reqTra.setHeader(header);

            respuesta = wsFondeaTarjetaOrquestador.fondeaTarjetaOrquestador(reqTra);

            if (respuesta.getCodigo() == 0) {
                TransaccionTarjetaOrquestadorResponse trxResponse = gson.fromJson(respuesta.getData(), TransaccionTarjetaOrquestadorResponse.class);
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(trxResponse.getMovimientoId()));
            } else if (respuesta.getCodigo() == 11) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.LIMITE_TRANSACCIONAL_MENSUAL,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.LIMITE_TRANSACCIONAL_MENSUAL));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaDevolucionCtaFacil(SpeiSimpleOBJ speiSimple, String cve_movimiento, Integer bancoId, String tipoDevolucion) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("cve_movimiento:: {}", cve_movimiento);

            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));
            String conceptoDevolucion = generaConceptoDevolucion(speiSimple, tipoDevolucion, formatoImporte);

            TransaccionTarjetaOrquestadorReq reqTra = new TransaccionTarjetaOrquestadorReq();
            HeaderWS header = new HeaderWS();
            header.setIdEmpresa(1L);
            header.setIdCanalAtencion(2L);
            header.setIdSucursal(1L);
            header.setIdUsuario(9L);
            header.setLatitud(speiSimple.getLatitud());
            header.setLongitud(speiSimple.getLongitud());
            header.setIpHost(ipHost);
            header.setIdBanco(String.valueOf(bancoId));
            header.setIdTransaccion(speiSimple.getDatos().getTipoTransaccionId());
            reqTra.setCuenta(speiSimple.getCuentaOrigen());
            reqTra.setImporte(String.valueOf(speiSimple.getMonto()));
            reqTra.setMedioPago(medioCajaPago);
            reqTra.setConcepto(conceptoDevolucion);
            reqTra.setObservaciones(conceptoDevolucion);
            reqTra.setClave_rastreo(speiSimple.getClaveRastreo());
            reqTra.setReferenciaNumerica(speiSimple.getReferenciaNumerica());
            reqTra.setClaveMovimiento(cve_movimiento);
            reqTra.setHeader(header);

            respuesta = wsFondeaTarjetaOrquestador.fondeaTarjetaOrquestador(reqTra);

            if (respuesta.getCodigo() == 0) {
                TransaccionTarjetaOrquestadorResponse trxResponse = gson.fromJson(respuesta.getData(), TransaccionTarjetaOrquestadorResponse.class);
                respuesta.setCodigo(0);
                respuesta.setMensaje("OK");
                respuesta.setData(String.valueOf(trxResponse.getMovimientoId()));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaTraspasoTarjetas(SpeiSimpleOBJ speiSimple, String cve_movimiento, Integer bancoId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            TraspasoTarjetasReq reqTra = new TraspasoTarjetasReq();
            HeaderWS header = new HeaderWS();
            header.setIdEmpresa(1L);
            header.setIdCanalAtencion(2L);
            header.setIdSucursal(1L);
            header.setIdUsuario(9L);
            header.setLatitud(speiSimple.getLatitud());
            header.setLongitud(speiSimple.getLongitud());
            header.setIpHost(ipHost);
            header.setIdBanco(String.valueOf(bancoId));
            header.setIdTransaccion(1L);
            reqTra.setImporte(speiSimple.getMonto());
            reqTra.setObservaciones(speiSimple.getConceptoDestino());
            reqTra.setClaveMovimiento(cve_movimiento);
            reqTra.setNumeroTarjetaOrigen(speiSimple.getPanOrigen());
            reqTra.setNumeroTarjetaDestino(speiSimple.getPanDestino());
            reqTra.setRefNumerica(speiSimple.getReferenciaNumerica());
            reqTra.setHeader(header);

            respuesta = wsTraspasoTarjetas.traspasoTarjetas(reqTra);

            if (respuesta.getCodigo() == 11) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.LIMITE_TRANSACCIONAL_MENSUAL,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.LIMITE_TRANSACCIONAL_MENSUAL));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaDepositoAhorro(SpeiSimpleOBJ speiSimple, Integer movId, Integer bancoId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        AhorroDisposicionesOBJ ahorroDisposicion = new AhorroDisposicionesOBJ();
        MovimientosCajaOBJ movimientoCaja = new MovimientosCajaOBJ();
        AhorroTransferenciaOBJ ahorroTransferencia = new AhorroTransferenciaOBJ();
        AhorroPagareOBJ ahorroPagares = null;
        try {
            Timestamp vFecha = Timestamp.from(Instant.now());
            respuesta = movimientosCajaDAO.obtenerAhorroMovimientoById(movId);
            if (respuesta.getCodigo() == 0) {
                AhorroMovimientoOBJ ahorroMovimiento = gson.fromJson(respuesta.getData(), AhorroMovimientoOBJ.class);
                respuesta = procreaDAO.obtenerAhorroContratoByCuentaah(speiSimple.getCuentaDestino());
                if (respuesta.getCodigo() == 0) {
                    AhorroContratoOBJ ahorroContrato = gson.fromJson(respuesta.getData(), AhorroContratoOBJ.class);

                    respuesta = procreaDAO.obtenerAhorroPagareByCunetah(speiSimple.getCuentaDestino());
                    if (respuesta.getCodigo() == 0) {
                        Gson gsonDate = new GsonBuilder()
                                .setDateFormat("MMM d, yyyy")
                                .create();

                        ahorroPagares = gsonDate.fromJson(respuesta.getData(), AhorroPagareOBJ.class);
                    }

                    if (ahorroContrato.getSaldo() == 0) {
                        AhorroPagareOBJ ahorroPagaresNuevo = new AhorroPagareOBJ();
                        ahorroPagaresNuevo.setCuenta(speiSimple.getCuentaDestino());

                        if (ahorroPagares != null) {
                            if (ahorroPagares.getNumero() != null) {
                                ahorroPagaresNuevo.setNumero(ahorroPagares.getNumero() + 1);
                            } else {
                                ahorroPagaresNuevo.setNumero((long) 1);
                            }
                        } else {
                            ahorroPagaresNuevo.setNumero((long) 1);
                        }

                        ahorroPagaresNuevo.setMonto(speiSimple.getMonto());
                        ahorroPagaresNuevo.setFechaInicio(speiSimple.getFecha());
                        ahorroPagaresNuevo.setCreadoPor(usuarioDepTransAhorro);
                        ahorroPagaresNuevo.setFechaCreacion(vFecha);
                        ahorroPagaresNuevo.setModificadoPor(usuarioDepTransAhorro);
                        ahorroPagaresNuevo.setFechaModificacion(vFecha);

                        procreaDAO.nuevoAhorroPagare(ahorroPagaresNuevo);
                    }

                    movimientoCaja.setCajaId(cajaDepTransAhorro);
                    movimientoCaja.setCajeroId(usuarioDepTransAhorro);
                    movimientoCaja.setFecha(speiSimple.getFecha());
                    movimientoCaja.setTipoMovId(movId);
                    movimientoCaja.setMonedaId(1);
                    movimientoCaja.setMonto(speiSimple.getMonto());
                    movimientoCaja.setCuenta(speiSimple.getCuentaDestino());
                    movimientoCaja.setFormaPago(formaPagoDsipTransAhorro);
                    movimientoCaja.setCreadoPor(usuarioDepTransAhorro);
                    movimientoCaja.setModificadoPor(usuarioDepTransAhorro);
                    movimientoCaja.setBancoId(bancoId);
                    movimientoCaja.setCheque("");
                    movimientoCaja.setObs(speiSimple.getConceptoDestino());
                    movimientoCaja.setEstatus("C");
                    movimientoCaja.setLatitud(speiSimple.getLatitud());
                    movimientoCaja.setLongitud(speiSimple.getLongitud());
                    movimientoCaja.setFechaCreacion(vFecha);
                    movimientoCaja.setRegionId(ahorroContrato.getSucursalId());

                    respuesta = movimientosCajaDAO.registraMovimientoCaja(movimientoCaja);
                    if (respuesta.getCodigo() == 0) {
                        movimientoCaja.setMovimientoId(Integer.valueOf(respuesta.getData()));

                        respuesta = ahorroActualizaSaldoService.ahorroActualizasaldo(speiSimple.getFecha(), speiSimple.getCuentaDestino(), speiSimple.getMonto(), ahorroMovimiento.getOperacion(), ahorroContrato);

                        ahorroTransferencia.setMovimientoId(movimientoCaja.getMovimientoId());
                        ahorroTransferencia.setDepositoId(movimientoCaja.getMovimientoId());
                        ahorroTransferencia.setCuentaDestino(speiSimple.getCuentaDestino());
                        ahorroTransferencia.setMonto(speiSimple.getMonto());

                        respuesta.setCodigo(0);
                        respuesta.setMensaje("OK");
                        respuesta.setData(gson.toJson(ahorroTransferencia));
                    } else {

                    }
                } else {
                    respuesta.setCodigo(-101);
                    respuesta.setMensaje("No se encontro información de la cuenta destino");
                    respuesta.setData(null);
                }
            } else {
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro el id del movimiento");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaDevolucionAhorro(SpeiSimpleOBJ speiSimple, Integer movId, Integer bancoId, String tipoDevolucion) {
        RespuestaDTO respuesta = new RespuestaDTO();
        AhorroDisposicionesOBJ ahorroDisposicion = new AhorroDisposicionesOBJ();
        MovimientosCajaOBJ movimientoCaja = new MovimientosCajaOBJ();
        AhorroTransferenciaOBJ ahorroTransferencia = new AhorroTransferenciaOBJ();
        AhorroPagareOBJ ahorroPagares = null;
        try {

            NumberFormat formatoImporte = NumberFormat.getCurrencyInstance(Locale.of("es", "MX"));
            String conceptoDevolucion = generaConceptoDevolucion(speiSimple, tipoDevolucion, formatoImporte);

            Timestamp vFecha = Timestamp.from(Instant.now());
            respuesta = movimientosCajaDAO.obtenerAhorroMovimientoById(movId);
            if (respuesta.getCodigo() == 0) {
                AhorroMovimientoOBJ ahorroMovimiento = gson.fromJson(respuesta.getData(), AhorroMovimientoOBJ.class);
                respuesta = procreaDAO.obtenerAhorroContratoByCuentaah(speiSimple.getCuentaOrigen());
                if (respuesta.getCodigo() == 0) {
                    AhorroContratoOBJ ahorroContrato = gson.fromJson(respuesta.getData(), AhorroContratoOBJ.class);

                    respuesta = procreaDAO.obtenerAhorroPagareByCunetah(speiSimple.getCuentaOrigen());
                    if (respuesta.getCodigo() == 0) {
                        Gson gsonDate = new GsonBuilder()
                                .registerTypeAdapter(Date.class, new DateAdapter())
                                .create();
                        ahorroPagares = gsonDate.fromJson(respuesta.getData(), AhorroPagareOBJ.class);
                    }

                    if (ahorroContrato.getSaldo() == 0) {
                        AhorroPagareOBJ ahorroPagaresNuevo = new AhorroPagareOBJ();
                        ahorroPagaresNuevo.setCuenta(speiSimple.getCuentaOrigen());

                        if (ahorroPagares != null) {
                            if (ahorroPagares.getNumero() != null) {
                                ahorroPagaresNuevo.setNumero(ahorroPagares.getNumero() + 1);
                            } else {
                                ahorroPagaresNuevo.setNumero((long) 1);
                            }
                        } else {
                            ahorroPagaresNuevo.setNumero((long) 1);
                        }

                        ahorroPagaresNuevo.setMonto(speiSimple.getMonto());
                        ahorroPagaresNuevo.setFechaInicio(speiSimple.getFecha());
                        ahorroPagaresNuevo.setCreadoPor(usuarioDepTransAhorro);
                        ahorroPagaresNuevo.setFechaCreacion(vFecha);
                        ahorroPagaresNuevo.setModificadoPor(usuarioDepTransAhorro);
                        ahorroPagaresNuevo.setFechaModificacion(vFecha);

                        procreaDAO.nuevoAhorroPagare(ahorroPagaresNuevo);
                    }

                    movimientoCaja.setCajaId(cajaDepTransAhorro);
                    movimientoCaja.setCajeroId(usuarioDepTransAhorro);
                    movimientoCaja.setFecha(speiSimple.getFecha());
                    movimientoCaja.setTipoMovId(movId);
                    movimientoCaja.setMonedaId(1);
                    movimientoCaja.setMonto(speiSimple.getMonto());
                    movimientoCaja.setCuenta(speiSimple.getCuentaOrigen());
                    movimientoCaja.setFormaPago(formaPagoDsipTransAhorro);
                    movimientoCaja.setCreadoPor(usuarioDepTransAhorro);
                    movimientoCaja.setModificadoPor(usuarioDepTransAhorro);
                    movimientoCaja.setBancoId(bancoId);
                    movimientoCaja.setCheque("");
                    movimientoCaja.setObs(conceptoDevolucion);
                    movimientoCaja.setEstatus("C");
                    movimientoCaja.setLatitud(speiSimple.getLatitud());
                    movimientoCaja.setLongitud(speiSimple.getLongitud());
                    movimientoCaja.setFechaCreacion(vFecha);
                    movimientoCaja.setRegionId(ahorroContrato.getSucursalId());

                    respuesta = movimientosCajaDAO.registraMovimientoCaja(movimientoCaja);
                    if (respuesta.getCodigo() == 0) {
                        movimientoCaja.setMovimientoId(Integer.valueOf(respuesta.getData()));

                        respuesta = ahorroActualizaSaldoService.ahorroActualizasaldo(speiSimple.getFecha(), speiSimple.getCuentaOrigen(), speiSimple.getMonto(), ahorroMovimiento.getOperacion(), ahorroContrato);

                        ahorroTransferencia.setMovimientoId(movimientoCaja.getMovimientoId());
                        ahorroTransferencia.setDisposicionId(movimientoCaja.getMovimientoId());
                        ahorroTransferencia.setCuentaOrigen(speiSimple.getCuentaOrigen());
                        ahorroTransferencia.setMonto(speiSimple.getMonto());

                        respuesta.setCodigo(0);
                        respuesta.setMensaje("OK");
                        respuesta.setData(gson.toJson(ahorroTransferencia));

                    } else {

                    }
                } else {
                    respuesta.setCodigo(-101);
                    respuesta.setMensaje("No se encontro información de la cuenta destino");
                    respuesta.setData(null);
                }
            } else {
                respuesta.setCodigo(-100);
                respuesta.setMensaje("No se encontro el id del movimiento");
                respuesta.setData(null);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private static String generaConceptoDevolucion(SpeiSimpleOBJ speiSimple, String tipoDevolucion, NumberFormat formatoImporte) {
        return String.format(
                "DEVOLUCION %s ASP INTEGRA OPC POR UN MONTO DE %s REF. %s CLAVE DE RASTREO: %s CONCEPTO: %s",
                tipoDevolucion, formatoImporte.format(speiSimple.getMonto()),
                speiSimple.getReferenciaNumerica(), speiSimple.getClaveRastreo(),
                speiSimple.getConceptoOrigen()
        );
    }
}