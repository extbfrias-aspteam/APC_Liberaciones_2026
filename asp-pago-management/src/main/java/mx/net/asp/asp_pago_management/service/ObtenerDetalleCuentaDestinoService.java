package mx.net.asp.asp_pago_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.*;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.NombreUtils;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresDetalleCuentaDestino;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
@Log4j2
@RequiredArgsConstructor
public class ObtenerDetalleCuentaDestinoService {

    private final NucleoCentralDAO nucleoCentralDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final NombreUtils nombreUtils;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    @Value("${id.clave.banco.asp}")
    private Integer idClaveBancoAsp;
    @Value("${id.tipo.cuenta.clabe}")
    private Integer idTipoCuentaClabe;
    @Value("${id.tipo.cuenta.tarjeta}")
    private Integer idTipoCuentaTarjeta;
    @Value("${id.tipo.cuenta.cuenta}")
    private Integer idTipoCuentaCuenta;
    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO obtenerDetalle(String identificadorCuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        PosGlobalCuentaOBJ cuentaDestino = new PosGlobalCuentaOBJ();
        try {
            log.info("Iniciando procesamiento de detalle para identificadorCuenta: {}", identificadorCuenta);

            Integer tipoCuenta = obtenerIdTipoCuenta(identificadorCuenta);
            log.info("tipoCuenta: {}", tipoCuenta);
            if (tipoCuenta == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDetalleCuentaDestino.ERROR_LONGITUD,
                        ErroresDetalleCuentaDestino.getMensajeError(ErroresDetalleCuentaDestino.ERROR_LONGITUD, String.valueOf(identificadorCuenta.length())));
            }

            log.info("Tipo de identificador detectado: {}", tipoCuenta);

            respuesta = obtenerCuenta(identificadorCuenta, tipoCuenta);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            cuentaDestino = gson.fromJson(respuesta.getData(), PosGlobalCuentaOBJ.class);
            log.info("Cuenta destino obtenida: {}", cuentaDestino != null ? cuentaDestino : "null");

            log.info("Obteniendo información básica del solicitante con personaId: {}", cuentaDestino.getPersonaId());
            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteById(cuentaDestino.getPersonaId());

            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresDetalleCuentaDestino.SOLICITANTE_NOT_FOUND,
                        ErroresDetalleCuentaDestino.getMensajeError(ErroresDetalleCuentaDestino.SOLICITANTE_NOT_FOUND, cuentaDestino.getPersonaId()));
            }

            log.info("Informacion del solicitante obtenida: {}", solicitanteOBJ);

            DetalleCuentaDestinoOBJ detalleCuentaDestinoOBJ = new DetalleCuentaDestinoOBJ();
            log.info("Enmascarando nombre completo del beneficiario");

            detalleCuentaDestinoOBJ.setNombreBeneficiario(nombreUtils.enmascararNombreCompleto(solicitanteOBJ.getNombres()));
            detalleCuentaDestinoOBJ.setApellidoPatBeneficiario(nombreUtils.enmascararNombreCompleto(solicitanteOBJ.getApellidoPaterno()));
            detalleCuentaDestinoOBJ.setApellidoMatBeneficiario(nombreUtils.enmascararNombreCompleto(solicitanteOBJ.getApellidoMaterno()));
            detalleCuentaDestinoOBJ.setPersonaId(solicitanteOBJ.getNumero());

            log.info("Datos del beneficiario enmascarados correctamente");

            log.info("Finalizó exitosamente obtenerDetalle para identificadorCuenta: {}", identificadorCuenta);
            RespuestaUtils.respuestaExito(respuesta, gson.toJson(detalleCuentaDestinoOBJ));

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public Integer obtenerIdTipoCuenta(String identificadorCuenta) {
        if (identificadorCuenta == null || identificadorCuenta.trim().isEmpty()) {
            log.warn("El identificadorCuenta es nulo o vacío");
            return null;
        }

        String valor = identificadorCuenta.trim();
        log.info("Validando identificadorCuenta: {}", valor);

        if (!valor.matches("\\d+")) {
            log.warn("El identificadorCuenta contiene caracteres no numéricos: {}", valor);
            return null;
        }

        int longitud = valor.length();
        log.info("Longitud del identificadorCuenta: {}", longitud);

        switch (longitud) {
            case 10:
                log.info("Identificado como TIPO TELEFONO (10 dígitos)");
                return idTipoCuentaCuenta;
            case 16:
                log.info("Identificado como TIPO TARJETA (16 dígitos)");
                return idTipoCuentaTarjeta;
            case 18:
                log.info("Identificado como TIPO CLABE (18 dígitos)");
                return idTipoCuentaClabe;
            default:
                log.warn("Longitud no válida para identificadorCuenta: {}", longitud);
                return null;
        }
    }

    private RespuestaDTO obtenerCuenta(String cuenta, Integer idTipoCuenta) {
        PosGlobalCuentaOBJ cuentaDestino = null;
        RespuestaDTO respuesta = new RespuestaDTO();

        try {

            log.info("Iniciando búsqueda de cuenta. ID Tipo Cuenta: {}, Valor: {}", idTipoCuenta, cuenta);

            if (idTipoCuenta == idTipoCuentaClabe) {
                if (cuenta.length() == 18) {
                    log.info("Buscando por CLABE (18 dígitos): {}", cuenta);
                    cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaClabe(cuenta);
                    if (cuentaDestino == null) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresDetalleCuentaDestino.CLABE_NOT_FOUND,
                                ErroresDetalleCuentaDestino.descError.get(ErroresDetalleCuentaDestino.CLABE_NOT_FOUND));
                    }
                }
            } else if (idTipoCuenta == idTipoCuentaTarjeta) {
                if (cuenta.length() == 16) {
                    log.info("Buscando cuenta por tarjeta principal: {}", cuenta);
                    cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByTarjetaPrincipal(cuenta);
                    if (cuentaDestino == null) {
                        log.info("No se encontró tarjeta principal, buscando tarjeta adicional...");
                        cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByTarjetaAdicional(cuenta);
                    }

                    if (cuentaDestino == null) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresDetalleCuentaDestino.TARJETA_NOT_FOUND,
                                ErroresDetalleCuentaDestino.descError.get(ErroresDetalleCuentaDestino.TARJETA_NOT_FOUND));
                    }
                }
            } else if (idTipoCuenta == idTipoCuentaCuenta) {
                if (cuenta.length() == 10) {
                    log.info("Buscando por cuenta (10 dígitos): {}", cuenta);
                    cuentaDestino = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(cuenta);
                    if (cuentaDestino == null) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresDetalleCuentaDestino.CUENTA_NOT_FOUND,
                                ErroresDetalleCuentaDestino.descError.get(ErroresDetalleCuentaDestino.CUENTA_NOT_FOUND));
                    }
                }
            }

            return RespuestaUtils.respuestaExito(respuesta, gson.toJson(cuentaDestino));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

}
