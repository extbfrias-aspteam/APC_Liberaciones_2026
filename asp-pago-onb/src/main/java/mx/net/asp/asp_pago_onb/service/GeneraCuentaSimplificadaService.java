package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.*;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.request.RegistroDeCuentaSimplificadaReq;
import mx.net.asp.asp_pago_onb.response.RegistroCuentaSimplificadaResponse;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresRegistroCtaSimp;
import mx.net.asp.asp_pago_onb.ws.asp.GeneraClabeIfzServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class GeneraCuentaSimplificadaService {

    private final CoDiDAO codiDAO;
    private final ConceptosCuentaDAO conceptosCuentaDAO;
    private final AhorroDAO ahorroDAO;
    private final ProductosDAO productosDAO;
    private final ProcreaDAO procreaDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final Gson gson;
    private final Environment env;
    private final StringFormatUtils stringFormatUtils;
    private final PldService pldService;
    private final GeneraClabeIfzServiceClient generaClabeIfzServiceClient;
    private final SolicitanteDAO solicitanteDAO;
    private final ErrorHandler errorHandler;

    @Value("${clave.sucursal.virtual}")
    private String claveSucVitualCoDi;
    @Value("${id.sucursal.virtual}")
    private Integer sucVitualCoDi;
    @Value("${producto.id.cuenta.facil}")
    private Integer prodcutoIdCtaFacil;
    @Value("${monto.apertura.cuenta.facil}")
    private double montoApertura;
    @Value("${estatus.id.cuenta.facil}")
    private Integer estatusId;
    @Value("${moneda.id.cuenta.facil}")
    private Integer monedaId;
    @Value("${asesor.id.cuenta.facil}")
    private Integer asesorId;
    @Value("${como.entero.id.cuenta.facil}")
    private Integer comoEnteroId;
    @Value("${como.entero.obs.cuenta.facil}")
    private String obsEntero;
    @Value("${tipo.cliente.cuenta.facil}")
    private String tipoCliente;
    @Value("${monto.max.default}")
    private BigDecimal montoMaximoDefault;
    @Value("${nivel.cuenta.simplificada}")
    private String nivelCuentaSimp;
    @Value("${concepto.nivel.cuenta}")
    private String claveNivelCuenta;
    @Value("${id.tipo.referencia}")
    private Integer tipoReferenciaCtaFacil;
    @Value("${cambio.password.clv}")
    private String cveCambioPass;

    @Value("${obs.cambio.dato.onb}")
    private String obsCambioDatoOnb;

    public RespuestaDTO creaCuentaSimplificada(SolicitanteOBJ solicitante, boolean solicitanteCertificado) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (!solicitanteCertificado) {
                log.info("### Se validara con sistema PLD");
                respuesta = pldService.validaSolicitantePLD(solicitante);
            } else {
                //Al ser solicitante certificado no vamos a validar al PLD
                RespuestaUtils.respuestaExito(respuesta);
            }
            if (respuesta.getCodigo() == 0) {
                respuesta = altaCuentaPersonaFisica(solicitante);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO altaCuentaPersonaFisica(SolicitanteOBJ solicitante) {
        RespuestaDTO respuesta = new RespuestaDTO();
        boolean cuentaExistente = false;
        boolean clabeExistente = false;
        Integer idCuenta;
        String clabe;
        try {
            String cuenta = generaReferencia(prodcutoIdCtaFacil, String.valueOf(tipoReferenciaCtaFacil));
            if (StringUtils.isBlank(cuenta)) {
                return RespuestaUtils.asignarError(respuesta,
                        -100,
                        "Ocurrio un error al generar la referencia");
            }

            log.info("Cuenta -> {}", cuenta);
            log.info("Sucursal virtual -> {}", claveSucVitualCoDi);
            log.info("Producto -> {}", prodcutoIdCtaFacil);

            clabe = generaClabeIfzServiceClient.generarClabe(prodcutoIdCtaFacil, cuenta, claveSucVitualCoDi);

            if (clabe.isEmpty()) {
                return RespuestaUtils.asignarError(respuesta,
                        -101,
                        "Ocurrio un error al generar la cuenta CLABE");
            }

            respuesta = ahorroDAO.buscaCuentaExistenteByCuentaAh(cuenta);
            if (respuesta.getCodigo() == 0)
                cuentaExistente = true;
            respuesta = ahorroDAO.buscaCuentaExistenteByCuentaClabe(clabe);
            if (respuesta.getCodigo() == 0)
                clabeExistente = true;

            if (cuentaExistente || clabeExistente) {
                return RespuestaUtils.asignarError(respuesta,
                        -102,
                        "Cuenta o Clabe ya existentes");
            }

            ProductoAhorroOBJ prodAhorroObj = productosDAO.obtenerDatosProductoByProductoId(prodcutoIdCtaFacil);
            if (prodAhorroObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        -103,
                        "Ocurrio un error al obtener datos del producto");
            }

            RendimientoOBJ rendimientoOBJ = productosDAO.obtenerRendimientoByProductoId(prodcutoIdCtaFacil, prodAhorroObj.getMontoMinimo());
            if (rendimientoOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        -104,
                        "Ocurrio un error al obtener el rendimiento del producto");
            }

            double gat = 0;
            double gatReal = 0;
            AhCuentaOBJ ahCuentaOBJ = new AhCuentaOBJ();
            ahCuentaOBJ.setCuenta(cuenta);
            ahCuentaOBJ.setClabeInterbancaria(clabe);
            ahCuentaOBJ.setCliente(solicitante.getNumero());
            ahCuentaOBJ.setEstatusId(estatusId);
            ahCuentaOBJ.setMontoApertura(montoApertura);
            ahCuentaOBJ.setProductoAhorroId(prodcutoIdCtaFacil);
            ahCuentaOBJ.setGatNominal(gat);
            ahCuentaOBJ.setGatReal(gatReal);
            ahCuentaOBJ.setMonedaId(monedaId);
            ahCuentaOBJ.setAsesorId(asesorId);
            ahCuentaOBJ.setComoEnteroId(comoEnteroId);
            ahCuentaOBJ.setComoEnteroObs(obsEntero);
            ahCuentaOBJ.setReferencia(cuenta);
            ahCuentaOBJ.setUsuarioCreacion(9);
            ahCuentaOBJ.setSucursalId(sucVitualCoDi);
            ahCuentaOBJ.setTipoCliente(tipoCliente);
            ahCuentaOBJ.setCliente_id(solicitante.getNumero());
            ahCuentaOBJ.setRendimiento(rendimientoOBJ.getPorcentaje());

            respuesta = ahorroDAO.altaCuentaAhorroSimplificada(ahCuentaOBJ);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        -106,
                        "Error al dar de alta la cuenta simplificada");
            }

            idCuenta = Integer.parseInt(respuesta.getData());
            BigDecimal montoMax;
            respuesta = ahorroDAO.obtenerMontoMaxAhorro(nivelCuentaSimp);
            if (respuesta.getCodigo() == 0)
                montoMax = new BigDecimal(respuesta.getData());
            else
                montoMax = montoMaximoDefault;
            InformacionPldCuentaAhorroOBJ info = new InformacionPldCuentaAhorroOBJ();
            info.setCuentaId(idCuenta);
            info.setEstatusId(1);
            info.setIngresosMen(montoMax);
            info.setMontoMaxAho(montoMax);
            log.info("*** Informacion pld cuenta {}: {}", cuenta, gson.toJson(info));

            ahorroDAO.registrarInformacionCuentaPLD(info);

            // Definimos los pares de concepto_id y valor
            Map<Integer, String> conceptosData = getConceptosData();

            // Iteramos sobre el Map para crear y agregar los ConceptoPld
            for (Map.Entry<Integer, String> entry : conceptosData.entrySet()) {
                ConceptoPldOBJ concepto = new ConceptoPldOBJ();
                concepto.setCuenta_id(idCuenta);
                concepto.setConceptos_id(entry.getKey());
                concepto.setValor(entry.getValue());
                ahorroDAO.registrarConceptoPld(concepto);
            }

            conceptosCuentaDAO.guardarConceptoCuentaCero(claveNivelCuenta, nivelCuentaSimp, cuenta);

            RegistroCuentaSimplificadaResponse response = new RegistroCuentaSimplificadaResponse();
            response.setCuenta_id(idCuenta.toString());
            response.setClabe(clabe);
            response.setFolioContrato(null);
            response.setPersona_id(solicitante.getNumero());
            response.setCuenta(cuenta);

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(response));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @NotNull
    private static Map<Integer, String> getConceptosData() {
        Map<Integer, String> conceptosData = new HashMap<>();
        conceptosData.put(31, "500");
        conceptosData.put(32, "ACTIVIDADES GENERALES");
        conceptosData.put(33, "4404");
        conceptosData.put(34, "CUENTA SIMPLIFICADA");
        conceptosData.put(35, "173");
        conceptosData.put(36, "AHORRO");
        conceptosData.put(39, "60");
        conceptosData.put(40, "PUBLICO GENERAL");
        return conceptosData;
    }

    public RespuestaDTO generaRegistroCodi(RegistroDeCuentaSimplificadaReq req, SolicitanteOBJ solicitante, String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        String folio;
        try {
            folio = stringFormatUtils.generaFolio();
            log.info("*** Folio generado -> {}", folio);
            RegistroCoDiOBJ registroCoDiOBJ = new RegistroCoDiOBJ();
            registroCoDiOBJ.setCuenta(cuenta);
            registroCoDiOBJ.setPassword(req.getPassword());
            registroCoDiOBJ.setEstatus(2);
            registroCoDiOBJ.setSolicitanteId(solicitante.getNumero());
            registroCoDiOBJ.setTelefono_validado("SI");
            registroCoDiOBJ.setRegistroNuevaApp("1");
            registroCoDiOBJ.setTelefonoCoDi(req.getCelular());
            registroCoDiOBJ.setFolioContrato(folio);
            //validar si tiene registroCodi
            RegistroCoDiOBJ registroCoDiAux = codiDAO.obtenerRegistroCoDiIndividualByCuentaAh(cuenta);
            if (registroCoDiAux != null) {
                //si tiene registroCodi
                registroCoDiOBJ.setId(registroCoDiAux.getId());
                //acutalizamos registro CoDi
                respuesta = codiDAO.actualizarRegistroCoDiById(registroCoDiOBJ);
                if (respuesta.getCodigo() != 0)
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.ACTUALIZAR_REGISTRO_CODI,
                            ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTUALIZAR_REGISTRO_CODI));
            } else {
                log.info("----> Se genera Registro CoDi");
                log.info("""
                                
                                ************************************
                                *         REGISTRO CODI            *
                                ************************************
                                * Cuenta:          {}      *
                                * Estatus:         {}               *
                                * Folio Contrato:  {}      *
                                * Solicitante ID:  {}          *
                                ************************************""",
                        registroCoDiOBJ.getCuenta(),
                        registroCoDiOBJ.getEstatus(),
                        registroCoDiOBJ.getFolioContrato(),
                        registroCoDiOBJ.getSolicitanteId()
                );
                respuesta = codiDAO.guardarRegistroCoDi(registroCoDiOBJ);
                if (respuesta.getCodigo() != 0)
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.CREACION_REGISTRO_CODI,
                            ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.CREACION_REGISTRO_CODI));
            }
            //validar si tiene registroServiciosDigitales
            RegistroServiciosDigitalesOBJ registroServiciosDigitalesOBJ = codiDAO.obtenerRegistroServiciosDigitalesIndividualByCuentaAh(cuenta);
            if (registroServiciosDigitalesOBJ != null) {
                //si tiene registro registroServiciosDigitales
                registroCoDiOBJ.setId(registroServiciosDigitalesOBJ.getId());
                //acutalizamos registroServiciosDigitales
                respuesta = codiDAO.actualizarRegistroServiciosDigitalesById(registroCoDiOBJ);
                if (respuesta.getCodigo() != 0) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.ACTUALIZAR_REGISTRO_CODI,
                            ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTUALIZAR_REGISTRO_CODI));
                } else {
                    solicitanteDAO.registrarBitacoraCambio(solicitante.getNumero(), cveCambioPass, 9, registroServiciosDigitalesOBJ.getPassword(), req.getPassword(), obsCambioDatoOnb);
                    RespuestaUtils.respuestaExito(respuesta, gson.toJson(registroCoDiOBJ));
                }
            } else {
                log.info("----> Se genera Registro Servicios Digitales");
                log.info("""
                                
                                ************************************
                                *   REGISTRO SERVICIOS DIGITALES   *
                                ************************************
                                * Cuenta:          {}      *
                                * Estatus:         {}               *
                                * Folio Contrato:  {}      *
                                * Telefono Validado: {}            *
                                * Registro Nueva App: {}            *
                                ************************************""",
                        registroCoDiOBJ.getCuenta(),
                        registroCoDiOBJ.getEstatus(),
                        registroCoDiOBJ.getFolioContrato(),
                        registroCoDiOBJ.getTelefono_validado(),
                        registroCoDiOBJ.getRegistroNuevaApp()
                );
                respuesta = codiDAO.guardarRegistroServiciosDigitales(registroCoDiOBJ);
                if (respuesta.getCodigo() != 0) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.CREACION_REGISTRO_CODI,
                            ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.CREACION_REGISTRO_CODI));
                }

                RespuestaUtils.respuestaExito(respuesta, gson.toJson(registroCoDiOBJ));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO guardaPosicionGlobal(RegistroCuentaSimplificadaResponse response) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            DatosPosGlobalOBJ datosPosGlobalOBJ = getDatosPosGlobalOBJ(response, "DEBITO");

            respuesta = nucleoCentralDAO.registrarCuentaPosicionGlobal(datosPosGlobalOBJ);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO guardaPosicionGlobalCuentaExistente(RegistroCuentaSimplificadaResponse response, String tipoCuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            DatosPosGlobalOBJ datosPosGlobalOBJ = getDatosPosGlobalOBJ(response, tipoCuenta);

            respuesta = nucleoCentralDAO.registrarCuentaPosicionGlobal(datosPosGlobalOBJ);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @NotNull
    private static DatosPosGlobalOBJ getDatosPosGlobalOBJ(RegistroCuentaSimplificadaResponse response, String tipoCuenta) {
        DatosPosGlobalOBJ datosPosGlobalOBJ = new DatosPosGlobalOBJ();
        datosPosGlobalOBJ.setCuenta(response.getCuenta());
        datosPosGlobalOBJ.setCuentaClabe(response.getClabe());
        datosPosGlobalOBJ.setEstatus("VIG");
        datosPosGlobalOBJ.setIdPersona(response.getPersona_id());
        datosPosGlobalOBJ.setReferencia(response.getCuenta());
        datosPosGlobalOBJ.setSaldoDisponible(new BigDecimal(0));
        datosPosGlobalOBJ.setSaldoTotal(new BigDecimal(0));
        datosPosGlobalOBJ.setTipoCuenta(tipoCuenta);
        datosPosGlobalOBJ.setTarjeta(null);
        return datosPosGlobalOBJ;
    }

    private String generaReferencia(Integer producto, String tipoReferencia) {
        String referencia;
        StringBuilder suc;
        StringBuilder consecutivo;
        boolean esInversion;
        try {
            suc = new StringBuilder(String.valueOf(producto));
            for (int x = suc.length(); x < 2; x++) {
                suc.insert(0, "0");
            }
            do {
                referencia = suc.toString();
                referencia = referencia + tipoReferencia;
                esInversion = false;
                Integer secuencia = ahorroDAO.obtenerSecuenciaReferenciaAhorro();
                consecutivo = new StringBuilder(secuencia.toString());
                boolean band;
                log.info("Secuencia obtenida {} -->", consecutivo.toString());
                do {
                    band = ahorroDAO.validaReferenciaUtilizada(Integer.valueOf(consecutivo.toString()));
                    if (band) {
                        log.warn("Secuencia {} utilizada, se genera una nueva -->", consecutivo.toString());
                        secuencia = ahorroDAO.obtenerSecuenciaReferenciaAhorro();
                        consecutivo = new StringBuilder(secuencia.toString());
                        log.info("Secuencia obtenida {} -->", consecutivo.toString());
                    }
                } while (band);

                for (int x = secuencia.toString().length(); x < 6; x++) {
                    consecutivo.insert(0, "0");
                }
                referencia = referencia + consecutivo;

                referencia = generaDigitoVerificador(referencia);
                log.info("referencia generada:: {}", referencia);
                if (referencia.length() > 10) {
                    log.error("la referencia supera los 10 caracteres");
                    return "";
                }
                //valida que exista como cuenta de inversion
                log.info("Se valida que no exista como cuenta de inversion de otra cuenta");
                esInversion = procreaDAO.validaExisteCuentaInversion(referencia);
                if(esInversion)
                    log.info("referencia {} encontrada como cuenta de inversion, se genera una nueva -->", referencia);
            } while (esInversion);

        } catch (Exception e) {
            errorHandler.handleException(e);
            return "";
        }
        return referencia;
    }

    private String generaDigitoVerificador(String referencia) {
        String preferencia;
        int itotal;
        int bytNumero;
        String sref;
        String val;
        int tmp;
        int ival;

        preferencia = referencia;
        itotal = 0;
        bytNumero = 2;
        for (int i = 0; i < preferencia.length(); i++) {
            val = preferencia.substring(i, i + 1);
            ival = Integer.parseInt(val);
            ival = (ival * bytNumero);
            if (ival >= 10)
                tmp = 1 + (ival - 10);
            else
                tmp = ival;

            itotal = (itotal + tmp);
            if (bytNumero == 2)
                bytNumero = 1;
            else
                bytNumero = 2;

        }

        if ((itotal % 10) == 0) {
            sref = preferencia + '0';
        } else {
            val = Integer.toString(itotal);
            ival = Integer.parseInt(val.substring(0, 1));
            ival = ival + 1;
            val = Integer.toString(ival) + '0';
            ival = Integer.parseInt(val);
            sref = preferencia + (ival - itotal);
        }

        return sref;
    }

    private double calcularGat(double rendimiento, int periodicidad) {
        String gat = String.valueOf(
                ((Math.pow(((rendimiento / 100) / (360.0 / periodicidad)) + 1, (360.0 / periodicidad))) - 1) * 100);

        BigDecimal g = new BigDecimal(gat);

        g = g.setScale(4, RoundingMode.HALF_EVEN);

        return g.doubleValue();
    }

    private double calcularGatReal(double gat, double inflacion) {
        String gatReal = String.valueOf((((1 + (gat / 100)) / (1 + inflacion)) - 1) * 100);
        BigDecimal g = new BigDecimal(gatReal);
        g = g.setScale(4, RoundingMode.HALF_EVEN);
        return g.doubleValue();
    }
}