package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.*;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.request.ProcesamientoSpeiSimpleReq;
import mx.net.asp.asp_pago_payments.response.ProcesamientoSpeiSimpleResponse;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresPSpeiSimple;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class AhorroTransaccionService {

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
    @Value("${clave.retiro.spei.facil}")
    private String retiroSpeiFacil;
    @Value("${clave.devolucion.spei.facil}")
    private String devolucionSpeiFacil;
    @Value("${clave.retiro.pago.credito.facil}")
    private String retiroPagoCreditoFacil;
    @Value("${clave.devolucion.pago.credito.facil}")
    private String devolucionPagoCreditoFacil;

    //MOVIMIENTOS PROCREA
    @Value("${id.retiro.traspaso.ahorro}")
    private Integer retiroTraspasoAhorro;
    @Value("${id.devolucion.retiro.traspaso.ahorro}")
    private Integer devolucionRetiroTraspasoAhorro;
    @Value("${id.deposito.traspaso.ahorro}")
    private Integer depositoTraspasoAhorro;
    @Value("${id.retiro.spei.ahorro}")
    private Integer retiroSpeiAhorro;
    @Value("${id.devolucion.spei.ahorro}")
    private Integer devolucionSpeiAhorro;
    @Value("${id.retiro.pago.credito.ahorro}")
    private Integer retiroPagoCreditoAhorro;
    @Value("${id.devolucion.pago.credito.ahorro}")
    private Integer devolucionPagoCreditoAhorro;

    @Value("${dep.trans.codi.id}")
    private Integer depTransCoDiId;
    @Value("${dep.trans.codi.cve}")
    private String depTransCoDiCve;
    @Value("${ret.trans.codi.id}")
    private Integer retTransCoDiId;
    @Value("${ret.trans.codi.cve}")
    private String retTransCoDiCve;

    @Value("${id.banco.pago.credito.cf}")
    private Integer idBancoPagoCF;
    @Value("${id.banco.traspaso.cuentas}")
    private Integer idBancoTraspaso;
    @Value("${id.banco.spei}")
    private Integer idBancoSpei;
    @Value("${id.banco.devolucion}")
    private Integer idBancoDevolucion;

    @Value("${usuario.dep.trans.ahorro}")
    private Integer usuarioDepTransAhorro;
    @Value("${usuario.tesorero.procrea}")
    private Integer usuarioTesoreroProcrea;

    public RespuestaDTO procesaTraspasoAsp(SpeiSimpleOBJ speiSimple, String uuidGlobal) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int idRetiro;
        int idDeposito;
        AhorroTransferenciaOBJ ahorroTransferenciaRetiro = null;
        AhorroTransferenciaOBJ ahorroTransferenciaDeposito = null;
        try {
            log.info("<---- TRASPASO ASP ENTRE CUENTAS ---->");
            log.info("---> ORIGEN: {}", speiSimple.getTipoCuentaOrigen());
            log.info("---> DESTINO: {}", speiSimple.getTipoCuentaDestino());

            boolean origenTienePlastico = tieneTarjetaPlastico(speiSimple.getTienePlasticoOrigen());
            boolean destinoTienePlastico = tieneTarjetaPlastico(speiSimple.getTienePlasticoDestino());

            if (origenTienePlastico && destinoTienePlastico) {
                // Ambas cuentas tienen tarjeta, se procesa directamente el traspaso de tarjetas
                respuesta = ahorroDepositoService.procesaTraspasoTarjetas(speiSimple, "", idBancoTraspaso);
                if (respuesta.getCodigo() == 0) {
                    return generaRespuestaSpeiSimpleExito(speiSimple);
                }
                return respuesta;
            }

            // Si no son ambas tarjetas, continúa con el flujo normal

            respuesta = procesarRetiro(speiSimple, retiroTraspasoFacil, retiroTraspasoAhorro, idBancoTraspaso);

            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            if (esCuentaAhorro(speiSimple.getTipoCuentaOrigen())) {
                ahorroTransferenciaRetiro = gson.fromJson(respuesta.getData(), AhorroTransferenciaOBJ.class);
                idRetiro = ahorroTransferenciaRetiro.getMovimientoId();
            } else {
                idRetiro = Integer.parseInt(respuesta.getData());
            }

            respuesta = procesarDeposito(speiSimple, depositoTraspasoFacil, depositoTraspasoAhorro, idBancoTraspaso);

            if (respuesta.getCodigo() != 0) {

                if (respuesta.getCodigo() == ErroresPSpeiSimple.LIMITE_TRANSACCIONAL_MENSUAL) {
                    procesarDevolucion(speiSimple, devolucionRetiroTraspasoFacil, devolucionRetiroTraspasoAhorro, "TRASPASO", idBancoDevolucion);
                    return respuesta; //retornamos la respuesta original LIMITE_TRANSACCIONAL_MENSUAL
                }

                return procesarDevolucion(speiSimple, devolucionRetiroTraspasoFacil, devolucionRetiroTraspasoAhorro, "TRASPASO", idBancoDevolucion);
            }

            if (esCuentaAhorro(speiSimple.getTipoCuentaDestino())) {
                ahorroTransferenciaDeposito = gson.fromJson(respuesta.getData(), AhorroTransferenciaOBJ.class);
                idDeposito = ahorroTransferenciaDeposito.getMovimientoId();
            } else {
                idDeposito = Integer.parseInt(respuesta.getData());
            }

            if (esCuentaAhorro(speiSimple.getTipoCuentaOrigen()) && esCuentaAhorro(speiSimple.getTipoCuentaDestino()))
                grabaTraspasoCuentasAhorro(ahorroTransferenciaRetiro, ahorroTransferenciaDeposito);
            else
                grabaTraspasoCuentas(speiSimple, idRetiro, idDeposito);

            return generaRespuestaSpeiSimpleExito(speiSimple);


        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return respuesta;
    }

    public RespuestaDTO procesaTraspasoEiYu(ProcesamientoSpeiSimpleReq req, SpeiSimpleOBJ speiSimple, String uuidGlobal, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int idRetiro;
        int idDeposito = 0;
        AhorroTransferenciaOBJ ahorroTransferenciaRetiro;
        try {
            log.info("<---- TRASPASO EIYU ---->");

            respuesta = procesarRetiro(speiSimple, retiroTraspasoFacil, retiroTraspasoAhorro, idBancoTraspaso);

            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            if (esCuentaAhorro(speiSimple.getTipoCuentaOrigen())) {
                ahorroTransferenciaRetiro = gson.fromJson(respuesta.getData(), AhorroTransferenciaOBJ.class);
                idRetiro = ahorroTransferenciaRetiro.getMovimientoId();
            } else {
                idRetiro = Integer.parseInt(respuesta.getData());
            }

            TraspasoEiYuOBJ traspasoEiYuOBJ = new TraspasoEiYuOBJ();
            traspasoEiYuOBJ.setEstatus("PEND");
            traspasoEiYuOBJ.setIdRetiro((long) idRetiro);
            traspasoEiYuOBJ.setIdDeposito(0L);
            traspasoEiYuOBJ.setCuentaOrigen(datosOrdenanteOBJ.getClabe());
            traspasoEiYuOBJ.setNombreOrdenante(speiSimple.getNombreOrdenante());
            traspasoEiYuOBJ.setCuentaDestino(req.getCuentaBeneficiario());
            traspasoEiYuOBJ.setNombreBeneficiario(speiSimple.getNombreBeneficiario());
            traspasoEiYuOBJ.setMonto(speiSimple.getMonto());
            traspasoEiYuOBJ.setConceptoPago(speiSimple.getConceptoOrigen());
            traspasoEiYuOBJ.setReferenciaNumerica(speiSimple.getReferenciaNumerica());
            traspasoEiYuOBJ.setBancoOrigen(speiSimple.getIdInstitucionOrdenante());
            traspasoEiYuOBJ.setCveRastreo(speiSimple.getClaveRastreo());
            traspasoEiYuOBJ.setTipoCuentaAh(speiSimple.getTipoCuentaOrigen());
            traspasoEiYuOBJ.setLatitud(speiSimple.getLatitud());
            traspasoEiYuOBJ.setLongitud(speiSimple.getLongitud());
            traspasoEiYuOBJ.setIdTipoPago("101");

            respuesta = ahorroDAO.registraAhorroTraspasoEiYu(traspasoEiYuOBJ);

            if (respuesta.getCodigo() == 0) {
                Long idTraspasoEiYu = Long.valueOf(respuesta.getData());
                ahorroDAO.registraAhorroTraspasoEiYuHistorico(idTraspasoEiYu, "PEND", "Se registra traspaso por primera vez a espera de ser procesado");
                grabaTraspasoCuentas(speiSimple, idRetiro, idDeposito);
                return generaRespuestaSpeiSimpleExito(speiSimple);
            } else {
                respuesta = procesarDevolucion(speiSimple, devolucionRetiroTraspasoFacil, devolucionRetiroTraspasoAhorro, "TRASPASO", idBancoDevolucion);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaSpei(ProcesamientoSpeiSimpleReq req, SpeiSimpleOBJ speiSimple, String uuidGlobal, PosGlobalCuentaOBJ datosOrdenanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        int idRetiro = 0;
        int idDeposito = 0;
        AhorroTransferenciaOBJ ahorroTransferenciaRetiro;
        try {
            log.info("<---- TRANSFERENCIA SPEI ---->");
            Boolean permisoTesorero = procreaDAO.validaPermisoTesorero(usuarioTesoreroProcrea);
            if (!permisoTesorero) {
                return  RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.ERROR_TESORERO,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.ERROR_TESORERO));
            }

            respuesta = procesarRetiro(speiSimple, retiroSpeiFacil, retiroSpeiAhorro, idBancoSpei);

            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            if (esCuentaAhorro(speiSimple.getTipoCuentaOrigen())) {
                ahorroTransferenciaRetiro = gson.fromJson(respuesta.getData(), AhorroTransferenciaOBJ.class);
                idRetiro = ahorroTransferenciaRetiro.getMovimientoId();
            } else {
                idRetiro = Integer.parseInt(respuesta.getData());
            }
            respuesta = speiOutgoingService.procesaSpeiSimple(req, speiSimple, datosOrdenanteOBJ);

            if (respuesta.getCodigo() == 0) {
                return generaRespuestaSpeiSimpleExito(speiSimple);
            } else {
                respuesta = procesarDevolucion(speiSimple, devolucionSpeiFacil, devolucionSpeiAhorro, "SPEI", idBancoDevolucion);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO procesaPagoCredito(SpeiSimpleOBJ speiSimple, PosGlobalCreditoOBJ posicionGlobalCredito) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("<---- PAGO DE CREDITO ---->");

            respuesta = procesarRetiro(speiSimple, retiroPagoCreditoFacil, retiroPagoCreditoAhorro, idBancoPagoCF);

            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }
            boolean pagoCF = false;
            respuesta = switch (posicionGlobalCredito.getProductocr()) {
                case "CF" -> {
                    log.info("Pago credito CF");
                    pagoCF = true;
                    yield pagoCreditoService.aplicaPagoCF(speiSimple, posicionGlobalCredito);
                }
                default -> {
                    log.info("Pago credito procrea");

                    // Ejecutar Procrea en BACKGROUND
                    pagoCreditoService.aplicaPagoCreditoProcrea(speiSimple, posicionGlobalCredito)
                            .whenCompleteAsync((resultado, error) -> {
                                if (error != null || resultado.getCodigo() != 0) {

                                    log.error("Pago credito Procrea fallo: {}",
                                            error != null ? error.getMessage() : resultado.getMensaje());

                                    procesarDevolucion(
                                            speiSimple,
                                            devolucionPagoCreditoFacil,
                                            devolucionPagoCreditoAhorro,
                                            "PAGO DE CREDITO",
                                            idBancoDevolucion
                                    );
                                }
                            });

                    // RESPUESTA INMEDIATA AL CLIENTE
                    yield new RespuestaDTO(0, "Pago procrea Async", null);
                }
            };

            if (respuesta.getCodigo() == 0) {
                return generaRespuestaSpeiSimpleExito(speiSimple);
            }

            RespuestaDTO resCreditoWS = new RespuestaDTO();
            Set<Integer> codigosExcluidos = Set.of(-199, -200, -500, -501, -502, -503);
            boolean resCF = false;
            //Validamos la respuesta de CreditoWS
            if (pagoCF && !codigosExcluidos.contains(respuesta.getCodigo())) {
                resCF = true;
                RespuestaUtils.asignarError(resCreditoWS,
                        ErroresPSpeiSimple.ERROR_CREDITO_WS,
                        ErroresPSpeiSimple.getMensajeError(ErroresPSpeiSimple.ERROR_CREDITO_WS, respuesta.getMensaje()));
            } else
                resCF = false;

            respuesta = procesarDevolucion(speiSimple, devolucionPagoCreditoFacil, devolucionPagoCreditoAhorro, "PAGO DE CREDITO", idBancoDevolucion);

            // Se retorna la respuesta correspondiente
            return resCF ? resCreditoWS : respuesta;
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesarRetiro(SpeiSimpleOBJ speiSimple, String cve_movimiento, Integer idMovimiento, Integer bancoId) {
        speiSimple.getDatos().setTipoTransaccionId(retTransCoDiId);
        speiSimple.getDatos().setTipoClave(retTransCoDiCve);
        if (esCuentaAhorro(speiSimple.getTipoCuentaOrigen())) {
            return ahorroDisposicionService.procesaDisposicionAhorro(speiSimple, idMovimiento, bancoId);
        } else {
            return ahorroDisposicionService.procesaDisposicionCtaFacil(speiSimple, cve_movimiento, bancoId);
        }
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

        if (respuesta.getCodigo() == 0) {
            RespuestaUtils.asignarError(respuesta,
                    ErroresPSpeiSimple.DEVOLUCION_CUENTA_ORIGEN,
                    ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.DEVOLUCION_CUENTA_ORIGEN));
        } else {
            log.error("*** No se logro hacer la devolucion a la cuenta de ahorro {}, monto {}", speiSimple.getCuentaOrigen(), speiSimple.getMonto());
            RespuestaUtils.asignarError(respuesta,
                    ErroresPSpeiSimple.ERROR_DEVOLUCION_CUENTA_ORIGEN,
                    ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.ERROR_DEVOLUCION_CUENTA_ORIGEN));
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
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO grabaTraspasoCuentasAhorro(AhorroTransferenciaOBJ ahorroTransferenciaRetiro, AhorroTransferenciaOBJ ahorroTransferenciaDeposito) {
        RespuestaDTO respuesta = new RespuestaDTO();
        log.info("******** REGISTRA TRASPASO CUENTAS AHORRO *********");
        try {
            AhorroTransferenciaOBJ ahorroTransferencia = new AhorroTransferenciaOBJ();
            if (ahorroTransferenciaRetiro != null) {
                ahorroTransferencia.setCuentaOrigen(ahorroTransferenciaRetiro.getCuentaOrigen());
                ahorroTransferencia.setMonto(ahorroTransferenciaRetiro.getMonto());
                ahorroTransferencia.setMovimientoId(ahorroTransferenciaRetiro.getMovimientoId());
                ahorroTransferencia.setDisposicionId(ahorroTransferenciaRetiro.getDisposicionId());
            }

            if (ahorroTransferenciaDeposito != null) {
                ahorroTransferencia.setCuentaDestino(ahorroTransferenciaDeposito.getCuentaDestino());
                ahorroTransferencia.setDepositoId(ahorroTransferenciaDeposito.getDepositoId());
            }

            ahorroTransferencia.setFecha(Date.from(Instant.now()));
            ahorroTransferencia.setCreadoPor(usuarioDepTransAhorro);

            movimientosCajaDAO.registraAhorroTransferencia(ahorroTransferencia);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private boolean esCuentaAhorro(String tipoCuenta) {
        return "AHORRO".equals(tipoCuenta);
    }

    private boolean tieneTarjetaPlastico(String tienePlastico) {
        return tienePlastico != null && (tienePlastico.equals("SI"));
    }

    private RespuestaDTO generaRespuestaSpeiSimpleExito(SpeiSimpleOBJ speiSimple) {
        RespuestaDTO respuesta = new RespuestaDTO();

        ProcesamientoSpeiSimpleResponse speiSimpleResp = new ProcesamientoSpeiSimpleResponse();
        speiSimpleResp.setEstatusOperacion("El pago fue realizado");
        speiSimpleResp.setBeneficiario(speiSimple.getNombreBeneficiario());
        speiSimpleResp.setMonto(speiSimple.getMonto());
        speiSimpleResp.setReferencia(speiSimple.getReferenciaNumerica());
        speiSimpleResp.setClaveRastreo(speiSimple.getClaveRastreo());

        respuesta.setCodigo(0);
        respuesta.setMensaje("El pago fue realizado");
        respuesta.setData(gson.toJson(speiSimpleResp));

        return respuesta;
    }
}