package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.*;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.response.RegistroCuentaSimplificadaResponse;
import mx.net.asp.asp_pago_onb.utilerias.*;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresRegistroCtaSimp;
import mx.net.asp.asp_pago_onb.ws.asp.MSValidaCurpNubarium;
import mx.net.asp.asp_pago_onb.ws.asp.response.ValidaCurpNubResponse;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.request.RegistroDeCuentaSimplificadaReq;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RegistroDeCuentaSimplificadaService {

    private final ConceptosCuentaDAO conceptosCuentaDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final PlasticosDAO plasticosDAO;
    private final PublicCeroDAO publicCeroDAO;
    private final CoDiDAO codiDAO;
    private final ProcreaDAO procreaDAO;
    private final AhorroDAO ahorroDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final ProductosDAO productosDAO;
    private final PromocionesDAO promocionesDAO;
    private final Gson gson;
    private final Environment env;
    private final StringFormatUtils stringFormatUtils;
    private final GeneraCuentaSimplificadaService generaCtaSimplificadaService;
    private final MSValidaCurpNubarium msValidaCurpNubarium;
    private final ErrorHandler errorHandler;
    private final ValidacionNubariumAsync validacionNubariumAsync;

    @Value("${producto.id.cuenta.facil}")
    private Integer prodcutoIdCtaFacil;
    @Value("${producto.id.mi.ahorro}")
    private Integer prodcutoIdMiAhorro;

    @Value("${concepto.consulta.nubarium}")
    private String conceptoConsultaNub;
    @Value("${version.app}")
    private String versionApp;

    @Value("${cambio.tel.codi.clv}")
    private String cveCambioTel;
    @Value("${cambio.col.id.clv}")
    private String cveCambioColonia;
    @Value("${cambio.correo.clv}")
    private String cveCambioCorreo;
    @Value("${cambio.domicilio.clv}")
    private String cveCambioDomicilio;
    @Value("${cambio.cel.clv}")
    private String cveCambioCel;
    @Value("${cambio.correo.edo.cuenta}")
    private String cveCambioCorreoEdoCta;
    @Value("${cambio.domicilio.ahorro.contrato}")
    private String cveCambioDomicilioAhorroContrato;

    @Value("${obs.cambio.dato.onb}")
    private String obsCambioDatoOnb;

    private static final List<String> CLAVES_CONCEPTOS_PROCREA = ListConceptosCuenta.getListConceptosProcrea();
    private static final List<String> CLAVES_CONCEPTOS_CERO = ListConceptosCuenta.getListConceptosCero();

    public RespuestaDTO registroDeCuentaSimplificadaInicial(RegistroDeCuentaSimplificadaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Integer idOnbEjecutado = 0;
        try {
            respuesta = OnboardingLockService.validaOnboardingPendiente(req, codiDAO, gson, errorHandler);
            if (respuesta.getCodigo() == 0) {
                idOnbEjecutado = respuesta.getData() != null ? Integer.valueOf(respuesta.getData()) : null;
                respuesta = registroDeCuentaSimplificada(req);
                if (respuesta.getCodigo() == 0) {
                    //Se registran las validaciones de nubarium
                    validacionNubariumAsync.registrarValidacionNubarium(respuesta, "CURP", req.getCurp(), "vc1644362546.0923724");
                    validacionNubariumAsync.registrarValidacionNubarium(respuesta, "RFC", req.getRfc(), "vr1644362542.768451");
                }
            } else
                return respuesta;
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        } finally {
            if (idOnbEjecutado != null && idOnbEjecutado > 0) {
                try {
                    // Finaliza Onboarding Ejecutado
                    codiDAO.finalizaOnboardingEjecutado(idOnbEjecutado);
                } catch (Exception e) {
                    errorHandler.handleException(e);
                }
            }
        }
        return respuesta;
    }

    public RespuestaDTO registroDeCuentaSimplificada(RegistroDeCuentaSimplificadaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        boolean solicitanteCertificado;
        SolicitanteOBJ solicitanteOBJ;
        try {
            if (!req.getAceptaTerminos().equalsIgnoreCase("SI"))
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.TERMINOS_Y_CONDICIONES,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.TERMINOS_Y_CONDICIONES));

            solicitanteCertificado = esSolicitanteCertificado(req.getCurp());
            log.info("*** Es solicitante certificado:: {}", solicitanteCertificado);

            if (!solicitanteCertificado) {
                log.info("## Solicitante no certificado... se procede a validar Curp con Nubarium");
                respuesta = validarCurpNubarium(req);
                if (respuesta.getCodigo() != 0)
                    return respuesta;
            }

            log.info("## RFC :: {}", req.getRfc());
            log.info("## CURP :: {}", req.getCurp());
            solicitanteOBJ = solicitanteDAO.buscarSolicitanteByCurp(req.getCurp());
            String personaId = "";
            if (solicitanteOBJ != null) {
                log.info("### Solicitante registrado:: {}", solicitanteOBJ.getNumero());
                personaId = solicitanteOBJ.getNumero();
                if (solicitanteOBJ.getBloqueado() == 1) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.SOLICITANTE_BLOQUEADO,
                            ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.SOLICITANTE_BLOQUEADO));
                }
            }

            respuesta = procesarSolicitante(req, solicitanteCertificado, personaId);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private boolean esSolicitanteCertificado(String curp) {
        RespuestaDTO respuesta = solicitanteDAO.buscarSolicitanteCertificadoByCurp(curp);
        return respuesta.getCodigo() == 0;
    }

    public RespuestaDTO procesarSolicitante(RegistroDeCuentaSimplificadaReq req, boolean solicitanteCertificado, String personaId) {
        if ("".equals(personaId)) {
            return procesaSolicitanteNuevo(req);
        } else {
            return procesarSolicitanteExistente(req, solicitanteCertificado, personaId);
        }
    }

    private RespuestaDTO procesaSolicitanteNuevo(RegistroDeCuentaSimplificadaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("****** SOLICITANTE NUEVO ******");
            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getCelular());
            if (solicitanteOBJ != null) {
                log.info("### Solicitante NO registrado -> Telefono CoDi SI registrado:: {}", solicitanteOBJ.getTelefonoCoDi());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.TELEFONO_REGISTRADO,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.TELEFONO_REGISTRADO));
            } else {
                respuesta = llenaDatosSolicitanteNuevo(req);
                if (respuesta.getCodigo() == 0) {
                    SolicitanteNewOBJ solicitanteNuevo = gson.fromJson(respuesta.getData(), SolicitanteNewOBJ.class);
                    respuesta = registraNuevoSolicitante(solicitanteNuevo, req);
                    if (respuesta.getCodigo() == 0) {
                        solicitanteNuevo.setNumero(respuesta.getData());
                        solicitanteOBJ = new SolicitanteOBJ();
                        solicitanteOBJ.setNumero(solicitanteNuevo.getNumero());
                        solicitanteOBJ.setNombre_p(solicitanteNuevo.getNombre_p());
                        solicitanteOBJ.setApellido_m(solicitanteNuevo.getApellido_m());
                        solicitanteOBJ.setApellido_p(solicitanteNuevo.getApellido_m());
                        solicitanteOBJ.setNombre(solicitanteNuevo.getNombre());
                        solicitanteOBJ.setCurp(solicitanteNuevo.getCurp());
                        solicitanteOBJ.setRfc(solicitanteNuevo.getRfc());
                        solicitanteOBJ.setTelefonoCoDi(solicitanteNuevo.getTelefono());
                        solicitanteOBJ.setTPersona(solicitanteNuevo.getTPersona());

                        return crearCuentaNueva(req, solicitanteOBJ, false);
                    }
                } else {
                    log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.ERROR_DATOS_INCOMPLETOS,
                            ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_DATOS_INCOMPLETOS));
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO registraNuevoSolicitante(SolicitanteNewOBJ solicitanteNuevo, RegistroDeCuentaSimplificadaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = solicitanteDAO.registrarNuevoSolicitante(solicitanteNuevo);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ERROR_REGISTRA_SOLICITANTE,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_REGISTRA_SOLICITANTE));
            }

            String numeroSolicitanteNuevo = respuesta.getData();
            solicitanteNuevo.setNumero(numeroSolicitanteNuevo);
            DirectorioTelefonicoOBJ telefonoCoDiOBJ = new DirectorioTelefonicoOBJ();
            telefonoCoDiOBJ.setTelefono(req.getCelular());
            telefonoCoDiOBJ.setIdSolicitante(numeroSolicitanteNuevo);
            telefonoCoDiOBJ.setIdCatTelefono(env.getProperty("id.cat.telefono.codi", Integer.class));
            telefonoCoDiOBJ.setObservaciones(env.getProperty("observaciones.telefono.codi", String.class));

            respuesta = solicitanteDAO.registrarNuevoDirectorioTelefonico(telefonoCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ERROR_REGISTRA_TELEFONO_CODI,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_REGISTRA_TELEFONO_CODI));
            }

            return RespuestaUtils.respuestaExito(respuesta, numeroSolicitanteNuevo);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesarSolicitanteExistente(RegistroDeCuentaSimplificadaReq req, boolean solicitanteCertificado, String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        boolean registraTelefonoCoDi = false;
        List<CuentaOBJ> cuentaList = new ArrayList<>();
        try {
            log.info("****** SOLICITANTE EXISTENTE ******");
            SolicitanteOBJ telefonoSolicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getCelular());
            if (telefonoSolicitanteOBJ != null) {
                if (telefonoSolicitanteOBJ.getNumero().equals(personaId))
                    log.info("Solicitante existente: {}, con Telefono CoDi registrado: {}", telefonoSolicitanteOBJ.getNumero(), telefonoSolicitanteOBJ.getTelefonoCoDi());
                else {
                    log.error("*** Solicitante existente, se envio telefono de otro cliente ***");
                    log.info("personaId: {}", personaId);
                    log.info("personaIdTelefonoCoDi: {}", telefonoSolicitanteOBJ.getNumero());
                    log.info("Telefono: {}", telefonoSolicitanteOBJ.getTelefonoCoDi());
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.TELEFONO_REGISTRADO,
                            ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.TELEFONO_REGISTRADO));
                }
            } else
                registraTelefonoCoDi = true;

            cuentaList = procesarTieneCuentas(personaId);
            log.info("Cuentas del solicitante {} encontradas: {}", personaId, cuentaList);

            if (cuentaList != null && !cuentaList.isEmpty()) {
                List<String> cuentas = cuentaList.stream()
                        .map(CuentaOBJ::getCuenta)
                        .toList();
                List<RegistroCoDiOBJ> registrosCodi = codiDAO.obtenerListRegistroCoDiByCuentasAh(cuentas);
                log.info("Lista de registros CoDi encontrados: {}",
                        registrosCodi.stream()
                                .map(r -> String.format("{cuenta=%s, versionApp=%s}", r.getCuenta(), r.getRegistroNuevaApp()))
                                .collect(Collectors.toList()));

                if (registrosCodi != null && !registrosCodi.isEmpty()) {
                    if (registrosCodi.stream().anyMatch(item -> versionApp.equals(item.getRegistroNuevaApp()))) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresRegistroCtaSimp.ERROR_VERSION_CUENTA,
                                ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_VERSION_CUENTA));
                    }
                }

                if (registraTelefonoCoDi) {
                    respuesta = procesaRegistraDirectorioTelefonico(req.getCelular(), personaId);
                    if (respuesta.getCodigo() != 0)
                        return respuesta;

                }

                SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoGeneralSolicitanteById(personaId);

                log.info("Se procede a actualizar las cuentas: {}", cuentas);
                respuesta = actualizaCuenta(req, solicitanteOBJ, cuentaList);

            } else {
                if (registraTelefonoCoDi) {
                    respuesta = procesaRegistraDirectorioTelefonico(req.getCelular(), personaId);
                    if (respuesta.getCodigo() != 0)
                        return respuesta;

                }

                SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoGeneralSolicitanteById(personaId);

                respuesta = crearCuentaNueva(req, solicitanteOBJ, solicitanteCertificado);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO procesaRegistraDirectorioTelefonico(String celular, String personaId) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<RegistroCuentaSimplificadaResponse> responseList = new ArrayList<>();
        try {
            DirectorioTelefonicoOBJ telefonoCoDiOBJ = new DirectorioTelefonicoOBJ();
            telefonoCoDiOBJ.setTelefono(celular);
            telefonoCoDiOBJ.setIdSolicitante(personaId);
            telefonoCoDiOBJ.setIdCatTelefono(env.getProperty("id.cat.telefono.codi", Integer.class));
            telefonoCoDiOBJ.setObservaciones(env.getProperty("observaciones.telefono.codi", String.class));
            respuesta = solicitanteDAO.registrarNuevoDirectorioTelefonico(telefonoCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ERROR_REGISTRA_TELEFONO_CODI,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_REGISTRA_TELEFONO_CODI));
            }

            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private List<CuentaOBJ> procesarTieneCuentas(String personaId) {
        List<CuentaOBJ> cuentaList = new ArrayList<>();
        try {
            cuentaList = verificarCuenta(personaId);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return cuentaList;
        }

        return cuentaList;
    }

    private List<CuentaOBJ> verificarCuenta(String personaId) {
        List<CuentaOBJ> cuentaList = new ArrayList<>();
        List<CuentaOBJ> cuentasProcrea = tieneCuentaProcrea(personaId);
        if (cuentasProcrea != null && !cuentasProcrea.isEmpty()) {
            cuentaList.addAll(cuentasProcrea);
        }
        List<CuentaOBJ> cuentasCero = tieneCuentaCero(personaId);
        if (cuentasCero != null && !cuentasCero.isEmpty()) {
            cuentaList.addAll(cuentasCero);
        }

        return cuentaList;
    }


    public RespuestaDTO crearCuentaNueva(RegistroDeCuentaSimplificadaReq req, SolicitanteOBJ solicitanteOBJ, boolean solicitanteCertificado) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<RegistroCuentaSimplificadaResponse> responseList = new ArrayList<>();
        try {
            respuesta = validaConceptosSolicitante(solicitanteOBJ.getNumero(), String.valueOf(req.getColoniaId()));
            if (respuesta.getCodigo() != 0) {
                log.error("Codigo: {}, Mensaje: \n{}", respuesta.getCodigo(), respuesta.getMensaje());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ACTUALIZA_VERSION_APP,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTUALIZA_VERSION_APP));
            }

            log.info("----> Se procede a crear la cuenta simplificada");
            respuesta = generaCtaSimplificadaService.creaCuentaSimplificada(solicitanteOBJ, solicitanteCertificado);
            if (respuesta.getCodigo() != 0) {
                log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.CREACION_CUENTA,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.CREACION_CUENTA));
            }

            RegistroCuentaSimplificadaResponse response = gson.fromJson(respuesta.getData(), RegistroCuentaSimplificadaResponse.class);
            respuesta = generaCtaSimplificadaService.generaRegistroCodi(req, solicitanteOBJ, response.getCuenta());
            if (respuesta.getCodigo() != 0)
                return respuesta;

            RegistroCoDiOBJ registroCoDiOBJ = gson.fromJson(respuesta.getData(), RegistroCoDiOBJ.class);
            response.setFolioContrato(registroCoDiOBJ.getFolioContrato());

            //registrar conceptos del prodcuto
            respuesta = validaConceptosProductoCuenta(registroCoDiOBJ.getCuenta());
            if (respuesta.getCodigo() != 0)
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ERROR_CONCEPTOS_PRODUCTO,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_CONCEPTOS_PRODUCTO));

            //registrar conceptos
            respuesta = validaConceptosCuenta(req, registroCoDiOBJ, "DEBITO", solicitanteOBJ.getNombre());
            if (respuesta.getCodigo() != 0)
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.DATOS_ADICIONALES_CUENTA,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.DATOS_ADICIONALES_CUENTA));

            //guardamos la cuenta en la posicion global
            respuesta = generaCtaSimplificadaService.guardaPosicionGlobal(response);
            if (respuesta.getCodigo() != 0)
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.GUARDA_POS_GLOBAL,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.GUARDA_POS_GLOBAL));

            //actualizamos cuenta activa en registro codi
            respuesta = codiDAO.actualizarRegistroCoDiActivo(registroCoDiOBJ.getCuenta(), registroCoDiOBJ.getSolicitanteId());
            if (respuesta.getCodigo() == 0) {
                //actualizamos en la tabla registro_servicios_digitales el campo registro_nuevaApp a 2
                respuesta = codiDAO.actualizarVersionAppRegistroServiciosDigitales(registroCoDiOBJ.getCuenta(), versionApp);
                if (respuesta.getCodigo() == 0) {
                    response.setMensaje("Registro de cuenta simplificada exitoso.");
                    responseList.add(response);
                    RespuestaUtils.respuestaExito(respuesta, gson.toJson(responseList));
                } else {
                    log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.ACTUALIZA_VERSION_APP,
                            ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTUALIZA_VERSION_APP));
                }
            } else {
                log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ACTIVAR_CUENTA,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTIVAR_CUENTA));
            }
            if (!StringUtils.isBlank(req.getCodigoPromocion()))
                registrarCodigoReferencia(req.getCodigoPromocion(), registroCoDiOBJ.getCuenta());
            else
                log.warn("* No se ingreso un codigo de referencia *");
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO actualizaCuenta(RegistroDeCuentaSimplificadaReq req, SolicitanteOBJ solicitanteOBJ, List<CuentaOBJ> cuentaList) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<RegistroCuentaSimplificadaResponse> listaResponse = new ArrayList<>();
        try {

            //actualizar datos del solicitante
            SolicitanteOBJ solicitanteExistente = solicitanteDAO.obtenerInfoGeneralSolicitanteById(solicitanteOBJ.getNumero());
            if (solicitanteExistente == null)
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.INFO_SOLICITANTE_NOT_FOUND,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.INFO_SOLICITANTE_NOT_FOUND));

            respuesta = actualizaSolicitante(req, solicitanteExistente);

            if (respuesta.getCodigo() != 0)
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ACTUALIZA_SOLICITANTE,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTUALIZA_SOLICITANTE));

            //validar conceptos solicitante
            respuesta = validaConceptosSolicitante(solicitanteOBJ.getNumero(), String.valueOf(req.getColoniaId()));
            if (respuesta.getCodigo() != 0) {
                log.error("Codigo: {}, Mensaje: \n{}", respuesta.getCodigo(), respuesta.getMensaje());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ACTUALIZA_VERSION_APP,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTUALIZA_VERSION_APP));
            }

            log.info("Se procede a actualizar las cuentas en la nueva version de la app");
            for (CuentaOBJ cuentaOBJ : cuentaList) {
                if (!cuentaOBJ.getTipoCuenta().equals("AHORRO")) {
                    respuesta = validaConceptosProductoCuenta(cuentaOBJ.getCuenta());
                    if (respuesta.getCodigo() != 0) {
                        log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresRegistroCtaSimp.ERROR_CONCEPTOS_PRODUCTO,
                                ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_CONCEPTOS_PRODUCTO));
                    }
                }
            }

            respuesta = generaCtaSimplificadaService.generaRegistroCodi(req, solicitanteOBJ, cuentaList.getFirst().getCuenta());
            if (respuesta.getCodigo() != 0)
                return respuesta;

            RegistroCoDiOBJ registroCoDiOBJ = gson.fromJson(respuesta.getData(), RegistroCoDiOBJ.class);

            // Validar conceptos cuenta
            respuesta = validaConceptosCuenta(req, registroCoDiOBJ, cuentaList.getFirst().getTipoCuenta(), solicitanteOBJ.getNombre());
            if (respuesta.getCodigo() != 0) {
                log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.DATOS_ADICIONALES_CUENTA,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.DATOS_ADICIONALES_CUENTA));
            }

            //actualizamos cuenta activa en registro codi
            respuesta = codiDAO.actualizarRegistroCoDiActivo(registroCoDiOBJ.getCuenta(), registroCoDiOBJ.getSolicitanteId());
            if (respuesta.getCodigo() != 0) {
                log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ACTIVAR_CUENTA,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTIVAR_CUENTA));
            }

            //actualizamos en la tabla registro_servicios_digitales el campo registro_nuevaApp a 2
            respuesta = codiDAO.actualizarVersionAppRegistroServiciosDigitales(registroCoDiOBJ.getCuenta(), versionApp);
            if (respuesta.getCodigo() != 0) {
                log.error("Codigo: {}, Mensaje: {}", respuesta.getCodigo(), respuesta.getMensaje());
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ACTUALIZA_VERSION_APP,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTUALIZA_VERSION_APP));
            }

            if (cuentaList.getFirst().getEstatus().equalsIgnoreCase("BLOQ") || cuentaList.getFirst().getEstatus().equalsIgnoreCase("B")) {
                //actualizar registro codi bloqueado
                log.info("**** Se bloquea registro CoDi debido a que la cuenta tiene estatus BLOQUEADA ****");
                registroCoDiOBJ.setBloqueado(1);
                registroCoDiOBJ.setFechaBloqueo(Timestamp.from(Instant.now()));
                codiDAO.actualizarBloquearRegistroCoDi(registroCoDiOBJ);
            }

            if (!StringUtils.isBlank(req.getCodigoPromocion()))
                registrarCodigoReferencia(req.getCodigoPromocion(), registroCoDiOBJ.getCuenta());
            else
                log.warn("* No se ingreso un codigo de referencia *");

            RegistroCuentaSimplificadaResponse response = new RegistroCuentaSimplificadaResponse();
            response.setMensaje("Exito al actualizar los datos del solicitante.");
            response.setCuenta(cuentaList.getFirst().getCuenta());
            response.setClabe(cuentaList.getFirst().getClabe());
            response.setFolioContrato(registroCoDiOBJ.getFolioContrato());
            response.setPersona_id(registroCoDiOBJ.getSolicitanteId());
            listaResponse.add(response);

            //valida pos global, en caso de no existir, insertar el registro
            if (!nucleoCentralDAO.validaPosicionGlobal(cuentaList.getFirst().getCuenta())) {
                log.warn("*** La cuenta {} no existe en la posicion global ***", cuentaList.getFirst().getCuenta());
                log.info("Se procede a guardar la cuenta {} en la posicion global", cuentaList.getFirst().getCuenta());
                RespuestaDTO resPosGlobal = generaCtaSimplificadaService.guardaPosicionGlobalCuentaExistente(response, cuentaList.getFirst().getTipoCuenta());
                log.info("Resultado de guardar en pos global:: {}", resPosGlobal.getMensaje());
            }

            log.info("Lista de cuentas actualizadas: {}", listaResponse);
            RespuestaUtils.respuestaExito(respuesta, gson.toJson(listaResponse));

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO actualizaSolicitante(RegistroDeCuentaSimplificadaReq req, SolicitanteOBJ solicitanteExistente) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            String domicilioCompleto;

            Long coloniaId = req.getColoniaId().longValue();

            domicilioCompleto = obtenerDomicilioCompleto(coloniaId, req.getCodigoPostal(), req.getDomicilio());

            if (!solicitanteExistente.getDomicilioObservaciones().isEmpty())
                domicilioCompleto = solicitanteExistente.getDomicilioObservaciones() + "|" + domicilioCompleto;

            domicilioCompleto = stringFormatUtils.limitarLongitud(domicilioCompleto, 250);

            respuesta = solicitanteDAO.actualizarDatosSolicitante(solicitanteExistente.getNumero(), domicilioCompleto, req.getCelular(), req.getEmail());
            if (respuesta.getCodigo() == 0) {
                solicitanteDAO.registrarBitacoraCambio(solicitanteExistente.getNumero(), cveCambioCorreo, 9, solicitanteExistente.getCorreo(), req.getEmail(), obsCambioDatoOnb);
                solicitanteDAO.registrarBitacoraCambio(solicitanteExistente.getNumero(), cveCambioDomicilio, 9, solicitanteExistente.getDomicilio(), domicilioCompleto, obsCambioDatoOnb);
                solicitanteDAO.registrarBitacoraCambio(solicitanteExistente.getNumero(), cveCambioCel, 9, solicitanteExistente.getCelular(), req.getCelular(), obsCambioDatoOnb);
            } else
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ACTUALIZA_SOLICITANTE,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ACTUALIZA_SOLICITANTE));

            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO llenaDatosSolicitanteNuevo(RegistroDeCuentaSimplificadaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            SolicitanteNewOBJ solicitanteNuevo = new SolicitanteNewOBJ();
            String domicilioCompleto;
            String nombres = req.getSegundoNombre() != null && !req.getSegundoNombre().trim().isEmpty()
                    ? req.getPrimerNombre().trim() + " " + req.getSegundoNombre().trim()
                    : req.getPrimerNombre().trim();

            solicitanteNuevo.setCveOcupacion(env.getProperty("clave.ocupacion.cta.facil", Integer.class));
            solicitanteNuevo.setPuesto("PUBLICO GENERAL");
            solicitanteNuevo.setGiroId(env.getProperty("id.giro.cta.facil", Integer.class));
            solicitanteNuevo.setNombre(nombres + " " + req.getApellidoPaterno().trim() + " " + req.getApellidoMaterno().trim());
            solicitanteNuevo.setEdoCivil("S");
            solicitanteNuevo.setNombre_p(nombres);
            solicitanteNuevo.setApellido_p(req.getApellidoPaterno());
            solicitanteNuevo.setApellido_m(req.getApellidoMaterno());
            solicitanteNuevo.setAcepta_terminos(req.getAceptaTerminos());
            solicitanteNuevo.setAcepta_biometricos(req.getAceptaUsoBiometricos());
            solicitanteNuevo.setCurp(req.getCurp());
            solicitanteNuevo.setTPersona("F");
            solicitanteNuevo.setCorreo(req.getEmail().toLowerCase());
            solicitanteNuevo.setNumeroCasa("");
            solicitanteNuevo.setColonia(1);
            solicitanteNuevo.setCelular(req.getCelular());
            solicitanteNuevo.setTelefono(req.getCelular());

            Long coloniaId = req.getColoniaId().longValue();

            domicilioCompleto = obtenerDomicilioCompleto(coloniaId, req.getCodigoPostal(), req.getDomicilio());

            solicitanteNuevo.setDomicilio(domicilioCompleto);

            solicitanteNuevo.setDescripcionDomicilio1(stringFormatUtils.limitarLongitud(req.getDomicilio(), 100));

            // Obtener el carácter del CURP en la posición 10
            String sexoCurp = String.valueOf(req.getCurp().charAt(10));
            log.info("CURP char at 10: {}", sexoCurp);
            // Obtener y normalizar el valor del sexo de la solicitud
            // Asignar el sexo según las reglas
            String sexoFinal = switch (sexoCurp) {
                case "" -> "";           // Sexo vacío
                case "H" -> "M";         // Hombre (Masculino)
                case "M" -> "F";         // Mujer (Femenino)
                default -> sexoCurp;         // Otro caso, usar el valor original
            };
            solicitanteNuevo.setSexo(sexoFinal);
            log.info("Sexo Solicitante: {}", solicitanteNuevo.getSexo());

            String rfc = req.getRfc() == null
                    ? ""
                    : req.getRfc();

            String rfc1 = rfc.length() >= 4
                    ? rfc.substring(0, 4)
                    : "";

            String rfc2 = rfc.length() >= 10
                    ? rfc.substring(4, 10)
                    : "";

            String rfc3 = rfc.length() >= 13
                    ? rfc.substring(10, 13)
                    : "";

            solicitanteNuevo.setRfc1(rfc1);
            solicitanteNuevo.setRfc2(rfc2);
            solicitanteNuevo.setRfc3(rfc3);

            // Obtener la clave del estado (índices 11 a 13 del CURP)
            String cveCurp = req.getCurp().substring(11, 13);
            log.info("#Clave de estado del curp:{}", cveCurp);

            Integer idEdoNac = publicCeroDAO.obtenerEstadoByClave(cveCurp);
            if (idEdoNac > 0)
                solicitanteNuevo.setEdoNacId(idEdoNac);

            // Extraer la fecha de nacimiento del CURP
            String fechaCorrecta = getFechaCorrecta(req);

            solicitanteNuevo.setFecha_nacimiento(fechaCorrecta);

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(solicitanteNuevo));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @NotNull
    private static String getFechaCorrecta(RegistroDeCuentaSimplificadaReq req) {
        String curp = req.getCurp();
        String fechaNac = curp.substring(4, 10); // Tomar los caracteres del índice 4 al 9
        // Formatear la fecha como "YYYY/MM/DD"
        String fechaFormateada = fechaNac.substring(0, 2) + "/" +
                fechaNac.substring(2, 4) + "/" +
                fechaNac.substring(4, 6);
        // Reorganizar la fecha a "DD/MM/YYYY"
        String[] partes = fechaFormateada.split("/");
        String fechaCorrecta = partes[2] + "/" + partes[1] + "/" + partes[0];
        return fechaCorrecta;
    }

    private String obtenerDomicilioCompleto(Long coloniaId, String cp, String domicilio) {
        InfoDomicilioOBJ infoDomicilioOBJ;
        String domicilioCompleto = "";
        try {
            infoDomicilioOBJ = publicCeroDAO.obtenerInformacionDomicilioByColoniaId(coloniaId);
            if (infoDomicilioOBJ != null) {
                log.info("### Datos Domicilio:: {}", gson.toJson(infoDomicilioOBJ));
                domicilioCompleto = String.format(
                        "%s, Col %s, C.P %s, %s, %s, %s",
                        domicilio,
                        infoDomicilioOBJ.getColonia(),
                        infoDomicilioOBJ.getCp(),
                        infoDomicilioOBJ.getLocalidad(),
                        infoDomicilioOBJ.getMunicipio(),
                        infoDomicilioOBJ.getEstado()
                );
            } else {
                log.info("----> Se procede a obtener la colonia centro del estado del que es el cp proporcionado: {}", cp);
                coloniaId = CodigoPostalUtil.obtenerClavePorCP(cp);
                infoDomicilioOBJ = publicCeroDAO.obtenerInformacionDomicilioByColoniaId(coloniaId);
                if (infoDomicilioOBJ != null) {
                    log.info("### Datos Domicilio:: {}", gson.toJson(infoDomicilioOBJ));
                    domicilioCompleto = String.format(
                            "%s, Col %s, C.P %s, %s, %s, %s",
                            domicilio,
                            infoDomicilioOBJ.getColonia(),
                            infoDomicilioOBJ.getCp(),
                            infoDomicilioOBJ.getLocalidad(),
                            infoDomicilioOBJ.getMunicipio(),
                            infoDomicilioOBJ.getEstado()
                    );
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return stringFormatUtils.limitarLongitud(domicilioCompleto, 250);
    }

    public List<CuentaOBJ> tieneCuentaProcrea(String personaId) {
        try {
            List<CuentaOBJ> cuentasList = procreaDAO.obtenerListadoCuentasProcreaByPersonaId(personaId, prodcutoIdMiAhorro);
            List<CuentaOBJ> cuentasFinales = new ArrayList<>();
            if (cuentasList != null && !cuentasList.isEmpty()) {
                if (cuentasList.size() == 1) {
                    log.info("### Tiene cuenta procrea de producto {}", prodcutoIdMiAhorro);
                    log.info("---> cuenta:: {}", gson.toJson(cuentasList.getFirst()));
                    cuentasFinales.add(cuentasList.getFirst());
                } else {
                    log.info("Se encontraron varias cuentas con producto {}", prodcutoIdMiAhorro);
                    log.info("----> Se obtiene la cuenta con el movimiento más reciente");

                    List<String> cuentasQuery = cuentasList.stream()
                            .map(CuentaOBJ::getCuenta)
                            .toList();

                    List<CuentaOBJ> cuentasOrdenadas = procreaDAO.listarCuentasProcreaMovimientoReciente(cuentasQuery, prodcutoIdMiAhorro);
                    if (cuentasOrdenadas != null && !cuentasOrdenadas.isEmpty()) {
                        log.info("Cuenta movimiento más actual:: {}", gson.toJson(cuentasOrdenadas.getFirst()));
                        cuentasFinales.addAll(cuentasOrdenadas);
                    }
                }
            }
            List<CuentaOBJ> cuentasFacil = tieneCtaFacilProcrea(personaId);
            if (cuentasFacil != null && !cuentasFacil.isEmpty()) {
                cuentasFinales.addAll(cuentasFacil);
            }

            return cuentasFinales;

        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList();
        }
    }

    public List<CuentaOBJ> tieneCtaFacilProcrea(String personaId) {
        List<CuentaOBJ> resultado = new ArrayList<>();
        try {
            List<CuentaOBJ> cuentasList = procreaDAO.obtenerListadoCuentasProcreaByPersonaId(personaId, prodcutoIdCtaFacil);
            if (cuentasList != null && !cuentasList.isEmpty()) {
                if (cuentasList.size() == 1) {
                    log.info("### Tiene cuenta procrea de producto {}", prodcutoIdCtaFacil);
                    log.info("---> cuenta:: {}", gson.toJson(cuentasList.getFirst()));
                    resultado.add(cuentasList.getFirst());
                } else {
                    log.info("Se encontró más de una cuenta procrea con producto {}", prodcutoIdCtaFacil);
                    List<String> cuentasQuery = cuentasList.stream()
                            .map(CuentaOBJ::getCuenta)
                            .toList();
                    log.info("----> Se obtiene la cuenta con el movimiento más reciente (procrea)");
                    List<CuentaOBJ> cuentasOrdenadas = procreaDAO.listarCuentasProcreaMovimientoReciente(cuentasQuery, prodcutoIdCtaFacil);
                    if (cuentasOrdenadas != null && !cuentasOrdenadas.isEmpty()) {
                        log.info("Cuenta movimiento más actual:: {}", gson.toJson(cuentasOrdenadas.getFirst()));
                        resultado.addAll(cuentasOrdenadas);
                    }
                }
            } else {
                log.info("No tiene cuentas de tipo CtaFacil");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList();
        }
        return resultado.isEmpty() ? Collections.emptyList() : resultado;
    }

    public List<CuentaOBJ> tieneCuentaCero(String personaId) {
        List<CuentaOBJ> resultado = new ArrayList<>();
        try {
            List<CuentaOBJ> cuentasList = ahorroDAO.obtenerListadoCuentasCeroByPersonaId(personaId, prodcutoIdCtaFacil);
            if (cuentasList != null && !cuentasList.isEmpty()) {
                if (cuentasList.size() == 1) {
                    log.info("### Tiene cuenta cero de producto {}", prodcutoIdCtaFacil);
                    CuentaOBJ cuentaOBJ = cuentasList.getFirst();
                    cuentaOBJ.setTipoCuenta("DEBITO");
                    log.info("---> cuenta:: {}", gson.toJson(cuentaOBJ));
                    resultado.add(cuentaOBJ);
                } else {
                    log.info("Se encontraron varias cuentas cero con producto {}", prodcutoIdCtaFacil);
                    log.info("----> Se obtiene la cuenta con el movimiento más reciente (cero)");

                    List<String> cuentasQuery = cuentasList.stream()
                            .map(CuentaOBJ::getCuenta)
                            .toList();

                    List<CuentaOBJ> cuentasOrdenadas = ahorroDAO.listarCuentasCeroMovimientoReciente(cuentasQuery, prodcutoIdCtaFacil);

                    if (cuentasOrdenadas != null && !cuentasOrdenadas.isEmpty()) {
                        CuentaOBJ cuentaReciente = cuentasOrdenadas.getFirst();
                        cuentaReciente.setTipoCuenta("DEBITO");
                        log.info("Cuenta movimiento más actual:: {}", gson.toJson(cuentaReciente));
                        resultado.add(cuentaReciente);
                    }
                    for (CuentaOBJ cuenta : cuentasList) {
                        cuenta.setTipoCuenta("DEBITO");
                        resultado.add(cuenta);
                    }
                }
            } else {
                log.info("No se encontraron cuentas cero con producto {}", prodcutoIdCtaFacil);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList();
        }
        return resultado.isEmpty() ? Collections.emptyList() : resultado;
    }

    public RespuestaDTO validaConceptosSolicitante(String personaId, String idColonia) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<String> errores = new ArrayList<>();
        try {
            // Obtener respuesta del servicio
            List<ConceptoSolicitanteOBJ> listaConceptos = solicitanteDAO.obtenerListadoConceptosSolicitanteByPersonaId(personaId);

            Map<String, Object> conceptos = new HashMap<>(); // Inicializar vacío

            // Verificar si la respuesta es válida y contiene datos
            if (listaConceptos != null && !listaConceptos.isEmpty()) {
                // Convertir lista a mapa
                conceptos = listaConceptos.stream()
                        .collect(Collectors.toMap(
                                ConceptoSolicitanteOBJ::getClave,
                                ConceptoSolicitanteOBJ::getId,
                                (valorExistente, nuevoValor) -> valorExistente
                        ));

            } else if (respuesta.getCodigo() == -200)
                return new RespuestaDTO(-100, "Ocurrio un error al obtener el listado de conceptos", null);

            // Definir valores
            Map<String, String> valores = Map.of(
                    "SOLIC_COLONIA", idColonia,
                    "PAIS_SOL", "117"
            );

            for (var entry : valores.entrySet()) {
                String clave = entry.getKey();
                String valor = entry.getValue();
                String optext = "";

                if ("PAIS_SOL".equals(clave)) {
                    optext = "MÉXICO"; // Puedes usar otro separador si es necesario
                }

                int resultado = conceptos.containsKey(clave)
                        ? actualizarConceptoSolicitante(valor, clave, personaId, optext, (Integer) conceptos.get(clave))
                        : insertarConceptoSolicitante(valor, clave, personaId, optext);

                if (resultado < 1) {
                    errores.add("Error en " + (conceptos.containsKey(clave) ? "actualizar" : "registrar") + " el concepto: " + clave);
                }
            }

            if (!errores.isEmpty()) {
                return new RespuestaDTO(-101, String.join("\n", errores), null);
            }

            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO validaConceptosCuenta(RegistroDeCuentaSimplificadaReq req, RegistroCoDiOBJ
            registroCoDiOBJ, String tipoCuenta, String nombreCliente) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<String> errores = new ArrayList<>();
        try {
            boolean esAhorro = "AHORRO".equals(tipoCuenta);
            String cuenta = registroCoDiOBJ.getCuenta();

            // Obtener respuesta del servicio
            List<ConceptoCuentaOBJ> listaConceptos = esAhorro
                    ? conceptosCuentaDAO.obtenerListadoConceptosCuentaSimplificadaProcreaByCuentaAh(registroCoDiOBJ.getSolicitanteId(), cuenta, CLAVES_CONCEPTOS_PROCREA)
                    : conceptosCuentaDAO.obtenerListadoConceptosCuentaSimplificadaCeroByCuentaAh(registroCoDiOBJ.getSolicitanteId(), cuenta, CLAVES_CONCEPTOS_CERO);

            Map<String, Object> conceptos = new HashMap<>(); // Inicializar vacío

            // Verificar si la respuesta es válida y contiene datos
            if (listaConceptos != null && !listaConceptos.isEmpty()) {
                // Convertir lista a mapa
                conceptos = listaConceptos.stream()
                        .collect(Collectors.toMap(ConceptoCuentaOBJ::getClave, ConceptoCuentaOBJ::getId));
            } else if (respuesta.getCodigo() == -200)
                return new RespuestaDTO(-100, "Ocurrio un error al obtener el listado de conceptos", null);

            // Definir valores
            Map<String, String> valores = new HashMap<>();
            valores.put("ASPP_AT", req.getAceptaTerminos());
            valores.put("ASPP_AB", req.getAceptaUsoBiometricos());
            valores.put("ASPP_CA", req.getCodigoAutorizacion());

            // Solo agregar si no es una cuenta de ahorro
            if (!esAhorro) {
                valores.put("CON_RET_TRASP_DOCK_CARNET", "0");
                valores.put("ACTUO_CUENTA_PROPIA", "1");
                valores.put("SOL_NOM_CLIE", nombreCliente);
                valores.put("AHO_NIVEL_CUENTA", "CTA_N1");
            }

            for (var entry : valores.entrySet()) {
                String clave = entry.getKey();
                String valor = entry.getValue();

                int resultado = conceptos.containsKey(clave)
                        ? actualizarConceptoCuenta(esAhorro, valor, clave, cuenta, (Integer) conceptos.get(clave))
                        : insertarConceptoCuenta(esAhorro, valor, clave, cuenta);

                if (resultado < 1) {
                    errores.add("Error en " + (conceptos.containsKey(clave) ? "actualizar" : "registrar") + " el concepto: " + clave);
                }
            }

            if (!errores.isEmpty()) {
                return new RespuestaDTO(-101, String.join("\n", errores), null);
            }
            RespuestaUtils.respuestaExito(respuesta);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO validaConceptosProductoCuenta(String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("*** Se valida si la cuenta ya tiene asociado los conceptos del producto");
            // 1. Obtener los conceptos ya registrados en la cuenta
            respuesta = conceptosCuentaDAO.validarListadoConceptosProductoCuentaByCuenta(cuenta, prodcutoIdCtaFacil);
            if (respuesta.getCodigo() == 0) {
                log.info("*** La cuenta ya tiene registrados los conceptos del producto");
                return RespuestaUtils.respuestaExito(respuesta);
            }
            // 2. En caso de no encontrar, obtener todos los conceptos del producto para darlos de alta
            List<ProductoAhorroDatosOBJ> productoDatosList = productosDAO.obtenerListadoDatosProductoByProductoId(prodcutoIdCtaFacil);
            if (productoDatosList != null && !productoDatosList.isEmpty()) {
                log.info("*** Se dan de alta conceptos del producto en la cuenta");
                for (ProductoAhorroDatosOBJ obj : productoDatosList)
                    conceptosCuentaDAO.guardarConceptoCuentaCeroById(obj.getConcepto_id(), obj.getValor(), cuenta);
            } else
                log.error("No se encontraron conceptos del producto, no fue posible darlos de alta en la cuenta");
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }


    private int actualizarConceptoSolicitante(String valor, String clave, String personaId, String optext, Integer
            idConcepto) {
        RespuestaDTO respuesta;
        log.info("Se actualiza concepto {} con valor {} para el solicitante {}", clave, valor, personaId);
        respuesta = solicitanteDAO.actualizarConceptoSolicitanteById(idConcepto, valor, optext);

        // Verificar la respuesta y devolver un código según el resultado
        if (respuesta.getCodigo() == 0)
            return 1;  // Retornar 1 si la operación fue exitosa
        else
            return 0;  // Retornar 0 si la operación falló
    }

    private int insertarConceptoSolicitante(String valor, String clave, String personaId, String optext) {
        RespuestaDTO respuesta;
        log.info("Se registra concepto {} con valor {} para el solicitante {}", clave, valor, personaId);
        respuesta = solicitanteDAO.guardarConceptoSolicitante(clave, valor, personaId, optext);

        // Verificar la respuesta y devolver un código según el resultado
        if (respuesta.getCodigo() == 0)
            return 1;  // Retornar 1 si la operación fue exitosa
        else
            return 0;  // Retornar 0 si la operación falló
    }

    private int actualizarConceptoCuenta(boolean esAhorro, String valor, String clave, String cuenta, Integer
            idConcepto) {
        RespuestaDTO respuesta;
        log.info("Se actualiza concepto {} con valor {} para la cuenta {}", clave, valor, cuenta);
        if (esAhorro)
            respuesta = conceptosCuentaDAO.actualizarConceptoCuentaProcreaById(idConcepto, valor);
        else
            respuesta = conceptosCuentaDAO.actualizarConceptoCuentaCeroById(idConcepto, valor);

        // Verificar la respuesta y devolver un código según el resultado
        if (respuesta.getCodigo() == 0)
            return 1;  // Retornar 1 si la operación fue exitosa
        else
            return 0;  // Retornar 0 si la operación falló
    }

    private int insertarConceptoCuenta(boolean esAhorro, String valor, String clave, String cuenta) {
        RespuestaDTO respuesta;
        log.info("Se registra concepto {} con valor {} para la cuenta {}", clave, valor, cuenta);
        if (esAhorro)
            respuesta = conceptosCuentaDAO.guardarConceptoCuentaProcrea(clave, valor, cuenta);
        else
            respuesta = conceptosCuentaDAO.guardarConceptoCuentaCero(clave, valor, cuenta);

        // Verificar la respuesta y devolver un código según el resultado
        if (respuesta.getCodigo() == 0)
            return 1;  // Retornar 1 si la operación fue exitosa
        else
            return 0;  // Retornar 0 si la operación falló
    }

    private RespuestaDTO validarCurpNubarium(RegistroDeCuentaSimplificadaReq req) {
        log.info("## Solicitante no certificado... se procede a validar Curp con Nubarium");
        ConfiguracionASPPagoOBJ confAspPagoOBJ = plasticosDAO.obtenerValorConfAspPagoByConcepto(conceptoConsultaNub);
        if (confAspPagoOBJ != null && !"DISABLED".equals(confAspPagoOBJ.getValor())) {
            log.info("<--- CONSULTA A NUBARIUM HABILITADA --->");
            return consultarCurpNubarium(req.getCurp());
        }
        return RespuestaUtils.respuestaExito(new RespuestaDTO());
    }

    private RespuestaDTO consultarCurpNubarium(String curp) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = msValidaCurpNubarium.validaCurpNibarium(curp);
            if (respuesta.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresRegistroCtaSimp.ERROR_VALIDA_CURP,
                        ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_VALIDA_CURP));
            }

            if (!StringUtils.isBlank(respuesta.getData())) {
                ValidaCurpNubResponse respNub = gson.fromJson(respuesta.getData(), ValidaCurpNubResponse.class);
                if (!"0".equals(respNub.getCodigoMensaje())) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresRegistroCtaSimp.ERROR_VALIDA_CURP_NUBARIUM,
                            respNub.getMensaje());
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private void registrarCodigoReferencia(String codigo, String cuenta) {
        RespuestaDTO respuesta;
        try {
            Integer idCodigoReferencia = promocionesDAO.obtenerCodigoIdByCodigoReferencia(codigo);
            if (idCodigoReferencia != 0) {
                respuesta = promocionesDAO.registrarCodigoReferencia(idCodigoReferencia, cuenta);
                if (respuesta.getCodigo() != 0) {
                    log.info(ErroresRegistroCtaSimp.descError.get(ErroresRegistroCtaSimp.ERROR_REGISTRAR_CODIGO));
                }
            } else {
                log.info("No se encontro el código de referencia :: {}", codigo);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            log.info(ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

}