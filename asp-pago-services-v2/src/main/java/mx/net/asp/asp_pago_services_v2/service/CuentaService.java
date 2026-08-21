package mx.net.asp.asp_pago_services_v2.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_services_v2.config.ServicioException;
import mx.net.asp.asp_pago_services_v2.dao.*;
import mx.net.asp.asp_pago_services_v2.dto.RespuestaDTO;
import mx.net.asp.asp_pago_services_v2.model.NivelCuentaOBJ;
import mx.net.asp.asp_pago_services_v2.model.PosGlobalCuentaOBJ;
import mx.net.asp.asp_pago_services_v2.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_services_v2.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_services_v2.requests.SubirNivelCuentaRemotaReq;
import mx.net.asp.asp_pago_services_v2.requests.ValidarCurpCuentaReq;
import mx.net.asp.asp_pago_services_v2.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_services_v2.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_services_v2.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_services_v2.utilerias.errores.ErroresSubeCuentaNivel;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CuentaService {

    private final CoDiService codiService;
    private final AhorroService ahorroService;

    private final SubirNivelCuentaDAO subirNivelCuentaDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final CoDiDAO coDiDAO;
    private final NucleoCentralDAO nucleoCentralDAO;

    private final Gson gson;
    private final ErrorHandler errorHandler;
    private final Environment env;

    public RespuestaDTO subirNivelCuentaRemota(SubirNivelCuentaRemotaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {

            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(req.getCuentaah());
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresSubeCuentaNivel.CUENTA_NOT_FOUND,
                        ErroresSubeCuentaNivel.getMensajeError(ErroresSubeCuentaNivel.CUENTA_NOT_FOUND, req.getCuentaah()));
            }

            if ("AHORRO".equalsIgnoreCase(posGlobCuentaObj.getTipoCuenta())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresSubeCuentaNivel.TIPO_CUENTA_INVALIDO,
                        ErroresSubeCuentaNivel.descError.get(ErroresSubeCuentaNivel.TIPO_CUENTA_INVALIDO));
            }

            //Se obtiene el registro codi de la cuenta
            RegistroCoDiOBJ regCoDiOBJ = coDiDAO.consultarRegistroCoDiCliente(posGlobCuentaObj.getPersonaId());
            respuesta = validarRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            //Se obtiene el nivel de la cuenta
            respuesta = ahorroService.obtenerNivelCuentaAh(req.getCuentaah());
            if (respuesta.getCodigo() == 0) {
                NivelCuentaOBJ nvlCuentaOBJ = gson.fromJson(respuesta.getData(), NivelCuentaOBJ.class);
                log.info("nvlCuentaOBJ:: " + gson.toJson(nvlCuentaOBJ));
                //Validamos que la cuenta sea N1, N2 o N3 para seguir con el proceso y que el producto sea 6 (CUENTA FACIL)
                if (!nvlCuentaOBJ.getNivelCuenta().equals("N4") && nvlCuentaOBJ.getProductoId().equals(env.getProperty("PRODUCTO_ID_CUENTA_FACIL", Integer.class))) {
                    // Actualizar nivel cuenta
                    respuesta = actualizarNivel(req);

                    if (respuesta.getCodigo() == 0) {
                        //Actualizamos el nivel de la cuenta a N4
                        log.info("NIVEL DE CUENTA ACTUALIZADA EXITOSAMENTE, CUENTA {}, NIVEL {}", req.getCuentaah(), env.getProperty("CLAVE_CUENTA_FACIL_N4", String.class));
                        //Registrar cambio en bitacora
                        log.info("Se procede a realizar el registro en bitacora");
                        String valoranterior = "";
                        switch (nvlCuentaOBJ.getNivelCuenta()) {
                            case "N1":
                                valoranterior = "CTA_N1";
                                break;
                            case "N2":
                                valoranterior = "CTA_N2";
                                break;
                            case "N3":
                                valoranterior = "CTA_N";
                                break;
                            default:
                                valoranterior = nvlCuentaOBJ.getNivelCuenta();
                        }
                        StringBuilder observaciones = new StringBuilder();
                        observaciones.append("valor anterior:")
                                .append(valoranterior)
                                .append("|valor nuevo:")
                                .append("CTA_N4|")
                                .append("cuenta:")
                                .append(req.getCuentaah());

                        ahorroService.registraBitacoraNcBitacora(req.getCuentaah(), observaciones.toString(), env.getProperty("CLAVE_CUENTA_SUBE_NIVEL", String.class));
                    }
                } else {
                    return RespuestaUtils.asignarError(new RespuestaDTO(),
                            ErroresSubeCuentaNivel.NIVEL_CUENTA,
                            ErroresSubeCuentaNivel.descError.get(ErroresSubeCuentaNivel.NIVEL_CUENTA));
                }
            } else if (respuesta.getCodigo() == -100)
                respuesta.setCodigo(-102);

        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO actualizarNivel(SubirNivelCuentaRemotaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            log.info("Se procede a actualizar el nivel de la cuenta");
            respuesta = subirNivelCuentaDAO.actualizarNivelCuenta(req.getCuentaah(),
                    env.getProperty("CLAVE_CUENTA_FACIL_N4", String.class));

            log.info("Nivel de cuenta actualizado exitosamente");

        } catch (ServicioException e) {
            log.error("ServicioException Error:");
            errorHandler.handleException(e);
            // Devolvemos el mensaje y código de la excepción personalizada
            respuesta.setCodigo(e.getCodigo());
            respuesta.setMensaje(e.getMensaje());
            respuesta.setData(null);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return respuesta;
    }

    public RespuestaDTO validarCurpCuenta(ValidarCurpCuentaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //Se obtiene el registro codi de la cuenta
            respuesta = codiService.obtenerRegistroCoDi(req.getCuentaah());
            if (respuesta.getCodigo() == 0) {
                RegistroCoDiOBJ regCoDiOBJ = gson.fromJson(respuesta.getData(), RegistroCoDiOBJ.class);
                //Validamos que la cuente este presente en la nueva versión de la app
                if (regCoDiOBJ.getRegistroNuevaApp().equals("2")) {
                    respuesta = solicitanteDAO.obtenerInfoSolicitanteById(regCoDiOBJ.getSolicitanteId());
                    if (respuesta.getCodigo() == 0) {
                        SolicitanteOBJ solicitanteOBJ = gson.fromJson(respuesta.getData(), SolicitanteOBJ.class);
                        log.info("CURP Req:: {}", req.getCurp());
                        log.info("CURP BD:: {}", solicitanteOBJ.getCurp());
                        if (solicitanteOBJ.getCurp().equals(req.getCurp())) {
                            log.info("El curp enviado SI coincide con el que se tiene registrado para la cuenta");
                            respuesta.setCodigo(0);
                            respuesta.setMensaje("OK");
                            respuesta.setData(null);
                        } else {
                            log.error("El curp enviado NO coincide con el que se tiene registrado para la cuenta");
                            respuesta.setCodigo(-103);
                            respuesta.setMensaje("El curp no coincide con el que se tiene registrado para la cuenta");
                            respuesta.setData(null);
                        }
                    } else {
                        if (respuesta.getCodigo() == -100)
                            respuesta.setCodigo(-102);
                    }
                } else {
                    respuesta.setCodigo(-101);
                    respuesta.setMensaje("La cuenta no se encuentra en la nueva versión de la app");
                    respuesta.setData(null);
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validarRegistroCoDi(RegistroCoDiOBJ regCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (regCoDiOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresSubeCuentaNivel.REGISTRO_CODI_NOT_FOUND,
                        ErroresSubeCuentaNivel.descError.get(ErroresSubeCuentaNivel.REGISTRO_CODI_NOT_FOUND));
            }

            //Validamos que la cuente este presente en la nueva versión de la app
            if (!"2".equals(regCoDiOBJ.getRegistroNuevaApp())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresSubeCuentaNivel.VERSION_APP,
                        ErroresSubeCuentaNivel.descError.get(ErroresSubeCuentaNivel.VERSION_APP));
            }

            if (regCoDiOBJ.getBloqueado() == 1) {
                log.info("*** Registro Bloqueado");
                return RespuestaUtils.asignarError(new RespuestaDTO(),
                        ErroresSubeCuentaNivel.REG_CODI_BLOQUEADO,
                        ErroresSubeCuentaNivel.descError.get(ErroresSubeCuentaNivel.REG_CODI_BLOQUEADO));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }
}