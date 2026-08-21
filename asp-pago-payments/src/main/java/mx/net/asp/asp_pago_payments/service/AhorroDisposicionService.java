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
import mx.net.asp.asp_pago_payments.ws.asp.WSRetiraTarjetaOrquestador;
import mx.net.asp.asp_pago_payments.ws.asp.request.TransaccionTarjetaOrquestadorReq;
import mx.net.asp.asp_pago_payments.ws.asp.response.TransaccionTarjetaOrquestadorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AhorroDisposicionService {

    private final MovimientosCajaDAO movimientosCajaDAO;
    private final ProcreaDAO procreaDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;
    private final WSRetiraTarjetaOrquestador wsRetiraTarjetaOrquestador;
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

    public RespuestaDTO procesaDisposicionCtaFacil(SpeiSimpleOBJ speiSimple, String cve_movimiento, Integer bancoId) {
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
            reqTra.setCuenta(speiSimple.getCuentaOrigen());
            reqTra.setImporte(String.valueOf(speiSimple.getMonto()));
            reqTra.setMedioPago(medioCajaPago);
            reqTra.setConcepto(speiSimple.getConceptoOrigen());
            reqTra.setObservaciones(speiSimple.getConceptoOrigen());
            reqTra.setClave_rastreo(speiSimple.getClaveRastreo());
            reqTra.setReferenciaNumerica(speiSimple.getReferenciaNumerica());
            reqTra.setClaveMovimiento(cve_movimiento);
            reqTra.setHeader(header);

            respuesta = wsRetiraTarjetaOrquestador.retiraTarjetaOrquestador(reqTra);

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

    public RespuestaDTO procesaDisposicionAhorro(SpeiSimpleOBJ speiSimple, Integer movId, Integer bancoId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        AhorroDisposicionesOBJ ahorroDisposicion = new AhorroDisposicionesOBJ();
        MovimientosCajaOBJ movimientoCaja = new MovimientosCajaOBJ();
        AhorroTransferenciaOBJ ahorroTransferencia = new AhorroTransferenciaOBJ();
        try {
            respuesta = procreaDAO.obtenerAhorroContratoByCuentaah(speiSimple.getCuentaOrigen());
            if (respuesta.getCodigo() == 0) {
                AhorroContratoOBJ ahorroContrato = gson.fromJson(respuesta.getData(), AhorroContratoOBJ.class);
                respuesta = movimientosCajaDAO.obtenerAhorroMovimientoById(movId);
                if (respuesta.getCodigo() == 0) {
                    AhorroMovimientoOBJ ahorroMovimiento = gson.fromJson(respuesta.getData(), AhorroMovimientoOBJ.class);

                    respuesta = ahorroActualizaSaldoService.ahorroActualizasaldo(speiSimple.getFecha(), speiSimple.getCuentaOrigen(), speiSimple.getMonto(), ahorroMovimiento.getOperacion(), ahorroContrato);

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
                    movimientoCaja.setObs(speiSimple.getConceptoOrigen());
                    movimientoCaja.setEstatus("C");
                    movimientoCaja.setLatitud(speiSimple.getLatitud());
                    movimientoCaja.setLongitud(speiSimple.getLongitud());
                    movimientoCaja.setRegionId(ahorroContrato.getSucursalId());

                    respuesta = movimientosCajaDAO.registraMovimientoCaja(movimientoCaja);
                    if (respuesta.getCodigo() == 0) {
                        movimientoCaja.setMovimientoId(Integer.valueOf(respuesta.getData()));
                        movimientosCajaDAO.registraGeolocalizacionMovimientoCaja(movimientoCaja);
                        ahorroDisposicion.setCuenta(speiSimple.getCuentaOrigen());
                        ahorroDisposicion.setFecha(speiSimple.getFecha());
                        ahorroDisposicion.setMonto(speiSimple.getMonto());
                        ahorroDisposicion.setFormaPagoId(formaPagoDsipTransAhorro);
                        ahorroDisposicion.setBancoId(bancoId);
                        ahorroDisposicion.setCheque("");
                        ahorroDisposicion.setMovtoId(movId);
                        ahorroDisposicion.setCreadoPor(usuarioDepTransAhorro);

                        respuesta = movimientosCajaDAO.registraAhorroDisposicion(ahorroDisposicion);
                        if (respuesta.getCodigo() == 0) {
                            ahorroDisposicion.setId(Integer.valueOf(respuesta.getData()));
                            ahorroTransferencia.setMovimientoId(movimientoCaja.getMovimientoId());
                            ahorroTransferencia.setDisposicionId(ahorroDisposicion.getId());
                            ahorroTransferencia.setCuentaOrigen(speiSimple.getCuentaOrigen());
                            ahorroTransferencia.setMonto(speiSimple.getMonto());

                            respuesta.setCodigo(0);
                            respuesta.setMensaje("OK");
                            respuesta.setData(gson.toJson(ahorroTransferencia));
                        } else {

                        }
                    }
                } else {
                    respuesta.setCodigo(-100);
                    respuesta.setMensaje("No se encontro el id del movimiento");
                    respuesta.setData(null);
                }
            } else {

            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}