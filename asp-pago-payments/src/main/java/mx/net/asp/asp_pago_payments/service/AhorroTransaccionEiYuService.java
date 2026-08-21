package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.AhorroDAO;
import mx.net.asp.asp_pago_payments.dao.MovimientosCajaDAO;
import mx.net.asp.asp_pago_payments.dao.ProcreaDAO;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.AhorroTransferenciaOBJ;
import mx.net.asp.asp_pago_payments.model.PosGlobalCreditoOBJ;
import mx.net.asp.asp_pago_payments.model.SpeiSimpleOBJ;
import mx.net.asp.asp_pago_payments.model.TraspasoEiYuOBJ;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresPSpeiSimple;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerarAbono;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class AhorroTransaccionEiYuService {

    private final AhorroDAO ahorroDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;
    private final AhorroDisposicionService ahorroDisposicionService;
    private final AhorroDepositoService ahorroDepositoService;
    private final MovimientosCajaDAO movimientosCajaDAO;
    private final PagoCreditoService pagoCreditoService;
    private final SpeiOutgoingService speiOutgoingService;
    private final ProcreaDAO procreaDAO;

    //MOVIMIENTOS CERO
    @Value("${clave.retiro.traspaso.facil}")
    private String retiroTraspasoFacil;
    @Value("${clave.devolucion.retiro.traspaso.facil}")
    private String devolucionRetiroTraspasoFacil;
    @Value("${clave.deposito.traspaso.facil}")
    private String depositoTraspasoFacil;

    //MOVIMIENTOS PROCREA
    @Value("${id.devolucion.retiro.traspaso.ahorro}")
    private Integer devolucionRetiroTraspasoAhorro;
    @Value("${id.deposito.traspaso.ahorro}")
    private Integer depositoTraspasoAhorro;

    @Value("${dep.trans.codi.id}")
    private Integer depTransCoDiId;
    @Value("${dep.trans.codi.cve}")
    private String depTransCoDiCve;

    @Value("${id.banco.traspaso.cuentas}")
    private Integer idBancoTraspaso;
    @Value("${id.banco.devolucion}")
    private Integer idBancoDevolucion;

    public RespuestaDTO procesaTraspasoEiYu(SpeiSimpleOBJ speiSimple, String uuidGlobal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int idRetiro = 0;
        int idDeposito;
        AhorroTransferenciaOBJ ahorroTransferenciaDeposito;
        try {

            respuesta = procesarDeposito(speiSimple, depositoTraspasoFacil, depositoTraspasoAhorro, idBancoTraspaso);

            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            if (esCuentaAhorro(speiSimple.getTipoCuentaDestino())) {
                ahorroTransferenciaDeposito = gson.fromJson(respuesta.getData(), AhorroTransferenciaOBJ.class);
                idDeposito = ahorroTransferenciaDeposito.getMovimientoId();
            } else {
                idDeposito = Integer.parseInt(respuesta.getData());
            }

            grabaTraspasoCuentas(speiSimple, idRetiro, idDeposito);
            return RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaPagoCredito(SpeiSimpleOBJ speiSimple, PosGlobalCreditoOBJ posicionGlobalCredito) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            boolean pagoCF = false;
            respuesta = switch (posicionGlobalCredito.getProductocr()) {
                case "CF" -> {
                    log.info("Pago credito CF");
                    yield pagoCreditoService.aplicaPagoCF(speiSimple, posicionGlobalCredito);
                }
                default -> {
                    log.info("Pago credito no reconocido");
                    yield RespuestaUtils.asignarError(respuesta,
                            ErroresGenerarAbono.TIPO_CREDITO, ErroresGenerarAbono.descError.get(ErroresGenerarAbono.TIPO_CREDITO));
                }
            };

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaDevolucionEiYu(SpeiSimpleOBJ speiSimple, String uuidGlobal, String clabe) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = procesarDevolucion(speiSimple, depositoTraspasoFacil, depositoTraspasoAhorro, "TRASPASO", idBancoDevolucion);

            TraspasoEiYuOBJ traspasoEiYuOBJ = new TraspasoEiYuOBJ();

            traspasoEiYuOBJ.setCveRastreo(speiSimple.getClaveRastreo());
            traspasoEiYuOBJ.setReferenciaNumerica(speiSimple.getReferenciaNumerica());
            traspasoEiYuOBJ.setCuentaOrigen(clabe);

            if (respuesta.getCodigo() == 0) {
                traspasoEiYuOBJ.setEstatus("DEV");
                respuesta = ahorroDAO.actualizaEstatusTraspasoEiYu(traspasoEiYuOBJ);
                if (respuesta.getCodigo() == 0) {
                    Long idTraspasoEiYu = Long.valueOf(respuesta.getData());
                    ahorroDAO.actualizarTraspasoEiYu("DEV", "Se devuelve el dinero a la cuenta origen: " + clabe, idTraspasoEiYu);
                    ahorroDAO.registrarHistoricoTraspasoEiYu("DEV", "Se devuelve el dinero a la cuenta origen: " + clabe, idTraspasoEiYu);
                }
                RespuestaUtils.respuestaExito(respuesta);
            }else{
                traspasoEiYuOBJ.setEstatus("ERROR-DEV");
                respuesta = ahorroDAO.actualizaEstatusTraspasoEiYu(traspasoEiYuOBJ);
                if (respuesta.getCodigo() == 0) {
                    Long idTraspasoEiYu = Long.valueOf(respuesta.getData());
                    ahorroDAO.actualizarTraspasoEiYu("ERROR-DEV", "Ocurrio un error al intentar realizar la devolución a la cuenta origen", idTraspasoEiYu);
                    ahorroDAO.registrarHistoricoTraspasoEiYu("ERROR-DEV", "Ocurrio un error al intentar realizar la devolución a la cuenta origen", idTraspasoEiYu);
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesarDeposito(SpeiSimpleOBJ speiSimple, String cve_movimiento, Integer idMovimiento, Integer bancoId) {
        speiSimple.getDatos().setTipoTransaccionId(depTransCoDiId);
        speiSimple.getDatos().setTipoClave(depTransCoDiCve);
        if (esCuentaAhorro(speiSimple.getTipoCuentaDestino())) {
            return ahorroDepositoService.procesaDepositoAhorro(speiSimple, idMovimiento, bancoId);
        } else {
            return ahorroDepositoService.procesaDepositoCtaFacil(speiSimple, cve_movimiento, bancoId);
        }
    }

    private RespuestaDTO procesarDevolucion(SpeiSimpleOBJ speiSimple, String cve_movimiento, Integer idMovimiento, String tipoDevolucion, Integer bancoId) {
        log.info("*** Se procede a hacer la devolucion a la cuenta origen: {}", speiSimple.getCuentaOrigen());
        speiSimple.getDatos().setTipoTransaccionId(depTransCoDiId);
        speiSimple.getDatos().setTipoClave(depTransCoDiCve);
        RespuestaDTO respuesta;
        if (esCuentaAhorro(speiSimple.getTipoCuentaOrigen())) {
            respuesta = ahorroDepositoService.procesaDevolucionAhorro(speiSimple, idMovimiento, bancoId, tipoDevolucion);
        } else {
            respuesta = ahorroDepositoService.procesaDevolucionCtaFacil(speiSimple, cve_movimiento, bancoId, tipoDevolucion);
        }

        if (respuesta.getCodigo() != 0) {
            log.error("*** No se logro hacer la devolucion a la cuenta de ahorro {}, monto {}", speiSimple.getCuentaOrigen(), speiSimple.getMonto());
            RespuestaUtils.asignarError(respuesta,
                    ErroresPSpeiSimple.ERROR_DEVOLUCION_CUENTA_ORIGEN, ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.ERROR_DEVOLUCION_CUENTA_ORIGEN));
        }

        return respuesta;
    }

    public RespuestaDTO grabaTraspasoCuentas(SpeiSimpleOBJ speiSimple, Integer idRetiro, Integer idDeposito) {
        RespuestaDTO respuesta = new RespuestaDTO();
        log.info("******** REGISTRA TRASPASO CUENTAS *********");
        try {
            String origen = speiSimple.getTipoCuentaOrigen().equals("AHORRO") ? "AHO" : "DEB";
            String destino = speiSimple.getTipoCuentaDestino().equals("AHORRO") ? "AHO" : "DEB";

            ahorroDAO.grabarTraspasoCuentas(origen + destino, speiSimple.getCuentaOrigen(), speiSimple.getCuentaDestino(), idRetiro, idDeposito, speiSimple.getMonto());
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private boolean esCuentaAhorro(String tipoCuenta) {
        return "AHORRO".equals(tipoCuenta);
    }
}