package mx.net.asp.asp_pago_management.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresBeneficiario;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresCuenta;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.core.env.Environment;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mx.net.asp.asp_pago_management.dao.AhorroDAO;
import mx.net.asp.asp_pago_management.dao.CoDiDAO;
import mx.net.asp.asp_pago_management.dao.CreditosDAO;
import mx.net.asp.asp_pago_management.dao.InversionesDAO;
import mx.net.asp.asp_pago_management.dao.NucleoCentralDAO;
import mx.net.asp.asp_pago_management.dao.ProductosDAO;
import mx.net.asp.asp_pago_management.dao.SeguridadDAO;

@Service
@Log4j2
@RequiredArgsConstructor
public class DatosCuentaService {

    private final CoDiDAO coDiDAO;
    private final AhorroDAO ahorroDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final CreditosDAO creditosDAO;
    private final ProductosDAO productosDAO;
    private final SeguridadDAO seguridadDAO;
    private final InversionesDAO inversionesDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;
    private final Environment env;

    @Value("${version.app}")
    private String versionApp;

    public RespuestaDTO obtenerDetalleCuenta(String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        CuentaAspPagoOBJ cuentaAspPagoObj = null;
        try {
            //Se valida que la cuenta sea una cuenta ASP Pago V2
            //Se obtiene el registro codi de la cuenta
//            RegistroCoDiOBJ regCoDiOBJ = coDiDAO.obtenerRegistroCoDiByCuentaAh(cuenta);
//            respuesta = validaRegistroCoDi(regCoDiOBJ);
//            if(respuesta.getCodigo() != 0)
//                return respuesta;

            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(cuenta);
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCuenta.CUENTA_NOT_FOUND,
                        ErroresCuenta.descError.get(ErroresCuenta.CUENTA_NOT_FOUND));
            }

            Boolean existeInversiones = inversionesDAO.validaInversionesActivasyVigentes(cuenta);
            log.info("Existen Inversiones Activas: {}", existeInversiones);
            
            log.info("Posición global de tipo {}", posGlobCuentaObj.getTipoCuenta());
            if (posGlobCuentaObj.getTipoCuenta().equals("AHORRO")) {
                log.info("Tipo cuenta: {}", posGlobCuentaObj.getTipoCuenta());

                List<TarjetaOBJ> tarjetas = new ArrayList<>();
                
                cuentaAspPagoObj = new CuentaAspPagoOBJ();
                cuentaAspPagoObj.setCuenta(posGlobCuentaObj.getCuentaah());
                cuentaAspPagoObj.setClabe(posGlobCuentaObj.getClabe());
                cuentaAspPagoObj.setCobro(posGlobCuentaObj.getCuenta_cobro());
                cuentaAspPagoObj.setPago(posGlobCuentaObj.getCuenta_pago());
                cuentaAspPagoObj.setDescripcion("MI AHORRO");
                cuentaAspPagoObj.setProducto("AHORRO");
                cuentaAspPagoObj.setEstatus(posGlobCuentaObj.getEstatus());
                cuentaAspPagoObj.setMostrarTarjetaAPP(0);
                cuentaAspPagoObj.setBannerSubirNivel(false);
                cuentaAspPagoObj.setTarjetas(tarjetas);
                cuentaAspPagoObj.setTipoCuenta(posGlobCuentaObj.getTipoCuenta());
                cuentaAspPagoObj.setBannerInversiones((!existeInversiones) ? true : false);
            } else {
                log.info("Tipo cuenta: {}", posGlobCuentaObj.getTipoCuenta());
                cuentaAspPagoObj = ahorroDAO.obtenerDatosCuentaAhByCuentaAh(cuenta);
                log.info("cuentaAspPagoObj: {}", gson.toJson(cuentaAspPagoObj));
                if (cuentaAspPagoObj == null) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresCuenta.CUENTA_NOT_FOUND,
                            ErroresCuenta.descError.get(ErroresCuenta.CUENTA_NOT_FOUND));
                }

                cuentaAspPagoObj.setTipoCuenta(posGlobCuentaObj.getTipoCuenta());

                cargarNivelCuenta(cuentaAspPagoObj);

                NivelCuentaOBJ ncuentaOBJ = ahorroDAO.obtenerNivelCuentaAh(cuentaAspPagoObj.getCuenta());
                if (ncuentaOBJ == null) {
                    log.error("Ocurrió un Error al consultar Nivel de la cuenta");
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresCuenta.NIVEL_CUENTA_NOT_FOUND,
                            ErroresCuenta.descError.get(ErroresCuenta.NIVEL_CUENTA_NOT_FOUND));
                }

                if (StringUtils.isBlank(ncuentaOBJ.getNivelCuenta())) {
                    log.error("La cuenta no tiene nivel");
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresCuenta.NIVEL_CUENTA_NOT_FOUND,
                            ErroresCuenta.descError.get(ErroresCuenta.NIVEL_CUENTA_NOT_FOUND));
                }

                cuentaAspPagoObj.setNivelCuenta(ncuentaOBJ.getNivelCuenta());
                
                if ("N4".equals(cuentaAspPagoObj.getNivelCuenta())) {
                	cuentaAspPagoObj.setBannerInversiones((!existeInversiones) ? true : false);
                } else {
                    cuentaAspPagoObj.setBannerInversiones(false);
                }
            }

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(cuentaAspPagoObj));

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO obtenerListadoCreditos(String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //Se valida que la cuenta sea una cuenta ASP Pago V2
            //Se obtiene el registro codi de la cuenta
            RegistroCoDiOBJ regCoDiOBJ = coDiDAO.obtenerRegistroCoDiByCuentaAh(cuenta);
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if(respuesta.getCodigo() != 0)
                return respuesta;


            log.info("### Se consulta el listado de Creditos de la cuenta");
            RespuestaDTO respuestaCreditos = creditosDAO.obtenerListadoCreditosByPersonaId(regCoDiOBJ.getSolicitanteId());

            if (respuestaCreditos.getCodigo() != 0) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCuenta.CREDITOS_ACTIVOS_NOT_FOUND,
                        ErroresCuenta.descError.get(ErroresCuenta.CREDITOS_ACTIVOS_NOT_FOUND));
            }

            Type creditosListType = new TypeToken<List<CreditoOBJ>>() {}.getType();
            List<CreditoOBJ> creditosList = gson.fromJson(respuestaCreditos.getData(), creditosListType);

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(creditosList));

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO obtenerTarjetas(String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<TarjetaOBJ> tarjetas = new ArrayList<>();
        try {
            //Se valida que la cuenta sea una cuenta ASP Pago V2
            //Se obtiene el registro codi de la cuenta
            RegistroCoDiOBJ regCoDiOBJ = coDiDAO.obtenerRegistroCoDiByCuentaAh(cuenta);
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if(respuesta.getCodigo() != 0)
                return respuesta;

            tarjetas = ahorroDAO.obtenerTarjetasByCuentaAh(cuenta);

            if (tarjetas.isEmpty()) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresCuenta.TARJETAS_NOT_FOUND,
                        ErroresCuenta.descError.get(ErroresCuenta.TARJETAS_NOT_FOUND));
            }

            log.info("Se encontraron {} tarjetas", tarjetas.size());

            return RespuestaUtils.respuestaExito(respuesta, gson.toJson(tarjetas));

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validaRegistroCoDi(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();

        if (registroCoDiOBJ == null) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresBeneficiario.REGISTRO_CODI_NOT_FOUND,
                    ErroresBeneficiario.descError.get(ErroresBeneficiario.REGISTRO_CODI_NOT_FOUND));
        }

        //Validamos que la cuente este presente en la nueva versión de la app
        if (!versionApp.equals(registroCoDiOBJ.getRegistroNuevaApp())) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresBeneficiario.VERSION_APP,
                    ErroresBeneficiario.descError.get(ErroresBeneficiario.VERSION_APP));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    private void cargarNivelCuenta(CuentaAspPagoOBJ cuentaAspPagoObj) {
        NivelCuentaOBJ ncuentaOBJ = ahorroDAO.obtenerNivelCuentaAh(cuentaAspPagoObj.getCuenta());
        try {
            if (ncuentaOBJ == null) {
                log.error("Ocurrió un Error al consultar Nivel de la cuenta");
                return;
            }

            if (StringUtils.isBlank(ncuentaOBJ.getNivelCuenta())) {
                log.error("La cuenta no tiene nivel");
                return;
            }

            cuentaAspPagoObj.setNivelCuenta(ncuentaOBJ.getNivelCuenta());

            // Obtener UDIS por nivel y producto
            Double udis = ahorroDAO.obtenerUdisByNivelCuentaProducto(cuentaAspPagoObj.getNivelCuenta(), cuentaAspPagoObj.getProductoId());
            if (udis == null) {
                log.error("Ocurrió un Error al obtener las UDIS por nivel de cuenta y producto");
                return;
            }

            cuentaAspPagoObj.setUdis(udis);

            // Calcular UDIS aproximadas en pesos
            Double valorUdi = ahorroDAO.obtenerValorUdis(LocalDate.now());
            BigDecimal udisAprox = BigDecimal.valueOf(udis * valorUdi)
                    .setScale(2, RoundingMode.HALF_EVEN);
            cuentaAspPagoObj.setUdisAproxPesos(udisAprox.doubleValue());

            // Validar si el producto permite subir de nivel
            RespuestaDTO respuesta = productosDAO.obtenerConceptoProductoByClave(
                    env.getProperty("producto.id.cuenta.facil", Integer.class),
                    env.getProperty("concepto.cta.sube.nivel", String.class)
            );

            if (respuesta.getCodigo() != 0) {
                log.error("Ocurrió un Error al validar si el producto permite subir de nivel:: {}", respuesta.getMensaje());
                return;
            }

            boolean subeNivel = "SI".equals(respuesta.getData());
            cuentaAspPagoObj.setBannerSubirNivel(false);
            if (subeNivel) {
                log.info("El producto SI permite subir de nivel");
                if ("N4".equals(cuentaAspPagoObj.getNivelCuenta())) {
                    log.info("La cuenta ya es una cuenta N4");
                } else {
                    SesionIncodeOBJ sesionIncodeEstatusFailOBJ = seguridadDAO.obtenerSesionIncodeByEstatus(cuentaAspPagoObj.getCuenta());
                    log.info("Resultado de obtenerSesionIncodeByEstatus para cuenta {}: {}", cuentaAspPagoObj.getCuenta(), sesionIncodeEstatusFailOBJ);
                    if (sesionIncodeEstatusFailOBJ != null) {
                        log.info("Existe una sesión fallida con estatus '{}'", sesionIncodeEstatusFailOBJ.getEstatus());
                        if(sesionIncodeEstatusFailOBJ.getEstatus().equals("RETRY")){
                            log.info("La sesión fallida tiene estatus RETRY, se mostrará el banner de subir nivel.");
                            cuentaAspPagoObj.setBannerSubirNivel(true);
                        }
                    } else {
                        log.info("No existe sesión fallida, se buscará una sesión general.");
                        SesionIncodeOBJ sesionIncodeOBJ = seguridadDAO.obtenerSesionIncode(cuentaAspPagoObj.getCuenta());
                        log.info("Resultado de obtenerSesionIncode para cuenta {}: {}", cuentaAspPagoObj.getCuenta(), sesionIncodeOBJ);
                        if (sesionIncodeOBJ == null) {
                            log.info("No existe ninguna sesión, se mostrará el banner de subir nivel.");
                            cuentaAspPagoObj.setBannerSubirNivel(true);
                        } else {
                            log.info("Existe una sesión general con estatus '{}'", sesionIncodeOBJ.getEstatus());
                            if("MANUAL".equals(sesionIncodeOBJ.getEstatus())){
                                log.info("La sesión tiene estatus MANUAL, no se mostrará el banner.");
                            } else {
                                log.info("La sesión tiene estatus distinto a MANUAL, se mostrará el banner.");
                                cuentaAspPagoObj.setBannerSubirNivel(true);
                            }
                        }
                    }
                    
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
    }
    
}