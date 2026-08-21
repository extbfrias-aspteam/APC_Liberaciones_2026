package mx.net.asp.asp_pago_management.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.*;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.request.EliminarFavoritoReq;
import mx.net.asp.asp_pago_management.request.RegistraFavoritoReq;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresFavoritos;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FavoritosService {

    private final CoDiDAO codiDAO;
    private final IzelStiDAO izelStiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final ErrorHandler errorHandler;
    private final Gson gson;
    private final StringFormatUtils stringFormatUtils;

    @Value("${id.clave.banco.asp}")
    private Integer idClaveBancoAsp;
    @Value("${id.tipo.cuenta.clabe}")
    private Integer idTipoCuentaClabe;
    @Value("${id.tipo.cuenta.telefono}")
    private Integer idTipoCuentaTelefono;
    @Value("${id.tipo.cuenta.cuenta}")
    private Integer idTipoCuentaCuenta;
    @Value("${id.tipo.cuenta.tarjeta}")
    private Integer idTipoCuentaTarjeta;
    @Value("${version.app}")
    private String versionApp;
    
    // Longitudes esperadas
    private static final int LONGITUD_CLABE = 18;
    private static final int LONGITUD_TELEFONO = 10;
    private static final int LONGITUD_TARJETA = 16;


    public RespuestaDTO registrarFavoritoCuenta(RegistraFavoritoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            req.setNumeroCuentaBeneficiario(stringFormatUtils.limpiarEspacios(req.getNumeroCuentaBeneficiario()));

            //Limpiamos caracteres especiales del nombre del beneficiario
            if (req.getPersonaId() != null && !req.getPersonaId().equals("")) {
                respuesta = solicitanteDAO.obtenerInfoGeneralSolicitanteById(req.getPersonaId());
                if (respuesta.getCodigo() == 0) {
                    SolicitanteOBJ solicitanteOBJ = gson.fromJson(respuesta.getData(), SolicitanteOBJ.class);
                    req.setNombreBeneficiario(stringFormatUtils.limpiarCaracteresEspeciales(solicitanteOBJ.getNombre()));
                } else {
                    req.setNombreBeneficiario(stringFormatUtils.limpiarCaracteresEspeciales(req.getNombreBeneficiario())); 
                }

            } else {
                req.setNombreBeneficiario(stringFormatUtils.limpiarCaracteresEspeciales(req.getNombreBeneficiario()));
            }

            log.info("Nombre del beneficiario: {}", req.getNombreBeneficiario());
            
            if (req.getIdInstitucionBeneficiario().equals(idClaveBancoAsp)) {
                respuesta = obtenerCuentaFavorito(req);
                if (respuesta.getCodigo() != 0)
                    return respuesta;
            }

            //validamos si el favorito se va a guardar
            if (req.isGuardarFavorito()) {
                boolean favoritoRegistrado;
                favoritoRegistrado = codiDAO.validarFavoritoExistente(req.getNumeroCuentaCoDi(), req.getNumeroCuentaBeneficiario());

                if (favoritoRegistrado) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresFavoritos.FAVORITO_EXISTENTE,
                            ErroresFavoritos.descError.get(ErroresFavoritos.FAVORITO_EXISTENTE));
                }

                //valida institucion bancaria
                CatInstiOBJ catInstiOBJ = izelStiDAO.obtenerInstitucionBancariaById(req.getIdInstitucionBeneficiario());
                if (catInstiOBJ == null) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresFavoritos.INSITUCION_NO_VALIDA,
                            ErroresFavoritos.getMensajeError(ErroresFavoritos.INSITUCION_NO_VALIDA, String.valueOf(req.getIdInstitucionBeneficiario())));
                }

                req.setNombreInstitucion(catInstiOBJ.getNombreCorto());
                
                respuesta = codiDAO.registraFavoritoCuenta(req);

                if (respuesta.getCodigo() == 0 || respuesta.getCodigo() == 1)
                    return respuesta;
                else
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresFavoritos.ERROR_REGISTRA_FAVORITO,
                            ErroresFavoritos.descError.get(ErroresFavoritos.ERROR_REGISTRA_FAVORITO));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO obtenerListadoFavoritosCuenta(String cuentaah) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            List<FavoritoOBJ> favoritosList = codiDAO.obtenerListadoFavoritosCuentaByCuentaAh(cuentaah);
            if (favoritosList != null && !favoritosList.isEmpty()) {
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(favoritosList));
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresFavoritos.LISTA_FAVORITOS_NOT_FOUND,
                        ErroresFavoritos.getMensajeError(ErroresFavoritos.LISTA_FAVORITOS_NOT_FOUND, cuentaah));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO eliminarFavorito(EliminarFavoritoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            FavoritoOBJ favoritoOBJ = codiDAO.obtenerFavoritoCuentaById(req.getId());

            if (favoritoOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresFavoritos.FAVORITO_NOT_FOUND,
                        ErroresFavoritos.descError.get(ErroresFavoritos.FAVORITO_NOT_FOUND));
            }

            respuesta = codiDAO.eliminaFavoritoCuentaById(favoritoOBJ.getId());
            if (respuesta.getCodigo() == 0)
                return RespuestaUtils.respuestaExito(respuesta, 0, "Se ha eliminado el favorito exitosamente.");
            else
                return RespuestaUtils.asignarError(respuesta,
                        ErroresFavoritos.ERROR_ELIMINA_FAVORITO,
                        ErroresFavoritos.descError.get(ErroresFavoritos.ERROR_ELIMINA_FAVORITO));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO obtenerCuentaFavorito(RegistraFavoritoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        PosGlobalCuentaOBJ cuentaPosGlobal = null;
        try {
            if (req.getTipoCuentaBeneficiario().equals(idTipoCuentaClabe)) {
                //TRASPASO MEDIANTE CLABE INTERBANCARIA
                log.info("Se busca la cuenta mediante la cuenta clabe: {}", req.getTipoCuentaBeneficiario());
                cuentaPosGlobal = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaClabe(req.getNumeroCuentaBeneficiario());
                if (cuentaPosGlobal == null) {
                    log.info("### No se encontró CLABE, se tomara como cuenta de EiYu");
                    return RespuestaUtils.respuestaExito(respuesta);
                }
            } else if (req.getTipoCuentaBeneficiario().equals(idTipoCuentaTarjeta)) {
                //TRASPASO MEDIANTE NUMERO DE TARJETA
                log.info("Se busca la cuenta mediante la tarjeta virtual: {}", req.getTipoCuentaBeneficiario());
                cuentaPosGlobal = nucleoCentralDAO.obtenerCuentaPosicionGlobalByTarjetaPrincipal(req.getNumeroCuentaBeneficiario());
                if (cuentaPosGlobal == null) {
                    log.info("No se encontro tarjeta virtual, se procede a buscar la cuenta clabe mediante la tarjeta fisica: {}", req.getTipoCuentaBeneficiario());
                    cuentaPosGlobal = nucleoCentralDAO.obtenerCuentaPosicionGlobalByTarjetaAdicional(req.getNumeroCuentaBeneficiario());
                }
            } else if (req.getTipoCuentaBeneficiario().equals(idTipoCuentaCuenta)) {
                //TRASPASO MEDIANTE NUMERO TELEFONICO ASP PAGO
                log.info("Se busca la cuenta mediante el numero de cuenta: {}", req.getTipoCuentaBeneficiario());
                RegistroCoDiOBJ regCoDiBeneficiario = codiDAO.obtenerRegistroCoDiByCuentaAh(req.getNumeroCuentaBeneficiario());
                if (regCoDiBeneficiario != null) {
                    //Validamos que la cuente este presente en la nueva versión de la app
                    if (!versionApp.equals(regCoDiBeneficiario.getRegistroNuevaApp())) {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresFavoritos.VERSION_APP_BEN,
                                ErroresFavoritos.descError.get(ErroresFavoritos.VERSION_APP_BEN));
                    }
                    cuentaPosGlobal = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(regCoDiBeneficiario.getCuenta());
                }
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresFavoritos.ERROR_ID_TIPO_CUENTA,
                        ErroresFavoritos.descError.get(ErroresFavoritos.ERROR_ID_TIPO_CUENTA));
            }

            if (cuentaPosGlobal == null) {
                if (req.getTipoCuentaBeneficiario().equals(idTipoCuentaTarjeta)) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresFavoritos.TARJETA_NOT_FOUND,
                            ErroresFavoritos.descError.get(ErroresFavoritos.TARJETA_NOT_FOUND));
                } else if (req.getTipoCuentaBeneficiario().equals(idTipoCuentaTelefono)) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresFavoritos.CUENTA_NOT_FOUND,
                            ErroresFavoritos.descError.get(ErroresFavoritos.CUENTA_NOT_FOUND));
                } else {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresFavoritos.CLABE_NOT_FOUND,
                            ErroresFavoritos.descError.get(ErroresFavoritos.CLABE_NOT_FOUND));
                }
            }

            return RespuestaUtils.respuestaExito(respuesta, gson.toJson(cuentaPosGlobal));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }
    
    public RespuestaDTO validarCuentaBeneficiario(String numeroCuenta, Integer idTipoCuenta) {
    	 RespuestaDTO respuesta = new RespuestaDTO();
        if (numeroCuenta == null || idTipoCuenta == null) {
            throw new IllegalArgumentException("Número de cuenta y tipo de cuenta son obligatorios");
        }

        if(idTipoCuenta == idTipoCuentaClabe) {
        	respuesta = validarClabe(numeroCuenta);
        }else if(idTipoCuenta == idTipoCuentaTelefono) {
        	respuesta = validarTelefono(numeroCuenta);
        }else if(idTipoCuenta == idTipoCuentaTarjeta) {
        	respuesta = validarTarjeta(numeroCuenta);
        }else {
        	 respuesta = RespuestaUtils.asignarError(respuesta,
                     ErroresFavoritos.ERROR_REGISTRA_FAVORITO,
                     "Tipo de cuenta beneficiario no válido");
        }
        if(respuesta.getCodigo()!=0) return respuesta;
        return RespuestaUtils.respuestaExito(respuesta);
    }

    private RespuestaDTO validarClabe(String clabe) {
    	 RespuestaDTO respuesta = new RespuestaDTO();
        if (clabe.length() != LONGITUD_CLABE) {
            return respuesta = RespuestaUtils.asignarError(respuesta,
                    ErroresFavoritos.ERROR_REGISTRA_FAVORITO,
                    "La clabe debe tener "+LONGITUD_CLABE+" dígitos.");
        }

        if (!clabe.matches("\\d+")) {
        	return respuesta = RespuestaUtils.asignarError(respuesta,
                    ErroresFavoritos.ERROR_REGISTRA_FAVORITO,
                    "La CLABE solo debe contener números.");
            
        }

        return RespuestaUtils.respuestaExito(respuesta);
    }

    private RespuestaDTO validarTelefono(String telefono) {
    	RespuestaDTO respuesta = new RespuestaDTO();
        if (telefono.length() != LONGITUD_TELEFONO) {
        	return respuesta = RespuestaUtils.asignarError(respuesta,
                    ErroresFavoritos.ERROR_REGISTRA_FAVORITO,
                    "El teléfono debe tener "+LONGITUD_TELEFONO+" dígitos.");
        }

        if (!telefono.matches("\\d+")) {
        	return respuesta = RespuestaUtils.asignarError(respuesta,
                    ErroresFavoritos.ERROR_REGISTRA_FAVORITO,
                    "El teléfono solo debe contener números.");
        }

        return RespuestaUtils.respuestaExito(respuesta);
    }

    private RespuestaDTO validarTarjeta(String tarjeta) {
    	RespuestaDTO respuesta = new RespuestaDTO();
        if (tarjeta.length() != LONGITUD_TARJETA) {
        	return respuesta = RespuestaUtils.asignarError(respuesta,
                    ErroresFavoritos.ERROR_REGISTRA_FAVORITO,
                    "La tarjeta debe tener "+LONGITUD_TARJETA+" dígitos.");
        }

        if (!tarjeta.matches("\\d+")) {
        	return respuesta = RespuestaUtils.asignarError(respuesta,
                    ErroresFavoritos.ERROR_REGISTRA_FAVORITO,
                    "La tarjeta solo debe contener números.");
            
        }

        return RespuestaUtils.respuestaExito(respuesta);
    }
}