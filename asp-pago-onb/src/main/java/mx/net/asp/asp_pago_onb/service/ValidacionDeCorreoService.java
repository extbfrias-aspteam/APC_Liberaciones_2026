package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.CoDiDAO;
import mx.net.asp.asp_pago_onb.dao.PlasticosDAO;
import mx.net.asp.asp_pago_onb.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresValidacionCorreo;
import mx.net.asp.asp_pago_onb.ws.asp.MSEnviarEmail;
import mx.net.asp.asp_pago_onb.request.ValidacionDeCorreoReq;
import mx.net.asp.asp_pago_onb.utilerias.CifradoUtil;
import mx.net.asp.asp_pago_onb.utilerias.CorreoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidacionDeCorreoService {

    private final CoDiDAO codiDAO;
    private final PlasticosDAO plasticosDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final Gson gson;
    private final Environment env;
    private final CifradoUtil cifradoUtil;
    private final CorreoUtils correoUtil;
    private final MSEnviarEmail msEnviarEmail;
    private final ErrorHandler errorHandler;

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO validaCorreo(ValidacionDeCorreoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            log.info("-----> Accion recibida: {}", req.getAccion());
            respuesta = switch (req.getAccion()) {
                case "0" ->
                    // Acción para validar el correo mediante la URL
                        validacionUrl(req);
                case "1" ->
                    // Acción para enviar el correo de validación al usuario
                        envioDeCorreo(req);
                case "2" ->
                    // Acción para verificar si el correo ya fue validado
                        validacionEstatus(req);
                default -> RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionCorreo.PARAMETRO_ACCION,
                        ErroresValidacionCorreo.getMensajeError(ErroresValidacionCorreo.PARAMETRO_ACCION, req.getAccion()));
            };
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO envioDeCorreo(ValidacionDeCorreoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = solicitanteDAO.obtenerSolicitanteByCorreo(req.getCorreoElectronico());

            if (respuesta.getCodigo() == 0) {
                log.info("### Se encontro solicitante con el correo {}:: {}", req.getCorreoElectronico(), respuesta.getData());
                RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(respuesta.getData());
                if (regCoDiOBJ != null) {
                    //Validamos que la cuente este presente en la nueva versión de la app
                    log.info("### Versión de la app del solicitante:: {}", regCoDiOBJ.getRegistroNuevaApp());
                    if (versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresValidacionCorreo.CORREO_REGISTRADO,
                                ErroresValidacionCorreo.descError.get(ErroresValidacionCorreo.CORREO_REGISTRADO));
                    }
                }
            } else
                log.info("### Correo electronico {} no registrado", req.getCorreoElectronico());

            // Obtencion de registro de validacion de correo
            ValidacionDeCorreoOBJ vCorreoOBJ = plasticosDAO.obtenerValidacionDeCorreo(req.getCurp(), req.getRfc());
            req.setAccion("0");
            req.setEstado("0");
            // Validacion de respuesta obtenida del registro
            if (vCorreoOBJ != null) {
                req.setId(vCorreoOBJ.getId());
                // Se realiza el update del registro
                respuesta = plasticosDAO.actualizarValidacionDeCorreo(req);
            } else {
                // Se realiza el insert del registro
                respuesta = plasticosDAO.guardarValidacionDeCorreo(req);
            }

            if (respuesta.getCodigo() == 0) {
                String keySource = cifradoUtil.generaKeySource(env.getProperty("aes.key.general", String.class));
                String data = cifradoUtil.encriptaInformacionB64(keySource, gson.toJson(req));
                String url;

                url = env.getProperty("url.pagina.web", String.class) + env.getProperty("url.validacion.correo", String.class) + "?data=" + data;

                String bodyCorreo = correoUtil.generaCorreoValidacion(url, req.getNombre());
                log.info("Se enviara correo Validacion de correo a {}", req.getCorreoElectronico());
                msEnviarEmail.enviarCorreo(req.getCorreoElectronico(), "Validación de correo", bodyCorreo);

                RespuestaUtils.respuestaExito(respuesta, 0, "Correo enviado");
            } else
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionCorreo.ENVIO_CORREO,
                        ErroresValidacionCorreo.descError.get(ErroresValidacionCorreo.ENVIO_CORREO));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validacionUrl(ValidacionDeCorreoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            boolean continuar = false;
            // Obtencion de registro de validacion de correo
            ValidacionDeCorreoOBJ vCorreoOBJ = plasticosDAO.obtenerValidacionDeCorreo(req.getCurp(), req.getRfc());
            // Validacion de respuesta obtenida del registro
            if (vCorreoOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionCorreo.REGISTRO_NOT_FOUND,
                        ErroresValidacionCorreo.descError.get(ErroresValidacionCorreo.REGISTRO_NOT_FOUND));
            }

            if (req.getCorreoElectronico().equalsIgnoreCase(vCorreoOBJ.getCorreoElectronico())) {
                if (!vCorreoOBJ.getEstado().equals("1")) {
                    req.setId(vCorreoOBJ.getId());
                    req.setEstado("1");
                    respuesta = plasticosDAO.actualizarValidacionDeCorreo(req);
                    if (respuesta.getCodigo() == 0)
                        continuar = true;
                } else
                    continuar = true;

                if (continuar) {
                    RespuestaUtils.respuestaExito(respuesta, 0, "Correo verificado");
                } else
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidacionCorreo.VALIDACION_CORREO,
                            ErroresValidacionCorreo.descError.get(ErroresValidacionCorreo.VALIDACION_CORREO));

            } else
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionCorreo.CORREO_NO_COINCIDE,
                        ErroresValidacionCorreo.descError.get(ErroresValidacionCorreo.CORREO_NO_COINCIDE));

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validacionEstatus(ValidacionDeCorreoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            // Obtencion de registro de validacion de correo
            ValidacionDeCorreoOBJ vCorreoOBJ = plasticosDAO.obtenerValidacionDeCorreo(req.getCurp(), req.getRfc());
            // Validacion de respuesta obtenida del registro
            if (vCorreoOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionCorreo.REGISTRO_NOT_FOUND,
                        ErroresValidacionCorreo.descError.get(ErroresValidacionCorreo.REGISTRO_NOT_FOUND));
            }

            if (req.getCorreoElectronico().equalsIgnoreCase(vCorreoOBJ.getCorreoElectronico())) {
                if (vCorreoOBJ.getEstado().equals("1")) {
                    RespuestaUtils.respuestaExito(respuesta, 0, "Correo verificado");
                } else
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresValidacionCorreo.VERIFICACION_CORREO,
                            ErroresValidacionCorreo.descError.get(ErroresValidacionCorreo.VERIFICACION_CORREO));
            } else
                return RespuestaUtils.asignarError(respuesta,
                        ErroresValidacionCorreo.CORREO_NO_COINCIDE,
                        ErroresValidacionCorreo.descError.get(ErroresValidacionCorreo.CORREO_NO_COINCIDE));

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
}