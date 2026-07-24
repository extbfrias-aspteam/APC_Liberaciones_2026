package mx.net.asp.asp_pago_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.CoDiDAO;
import mx.net.asp.asp_pago_management.dao.ConceptosCuentaDAO;
import mx.net.asp.asp_pago_management.dao.NucleoCentralDAO;
import mx.net.asp.asp_pago_management.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.ConceptoCuentaOBJ;
import mx.net.asp.asp_pago_management.model.PosGlobalCuentaOBJ;
import mx.net.asp.asp_pago_management.model.RegistroCoDiOBJ;
import mx.net.asp.asp_pago_management.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_management.request.RegistraTokenHuellaReq;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresTokenHuella;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class TokenHuellaService {

    private final CoDiDAO codiDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final ConceptosCuentaDAO conceptosCuentaDAO;
    private final ErrorHandler errorHandler;

    @Value("${clave.biometrico}")
    private String claveBiometrico;
    @Value("${version.app}")
    private String versionApp;
    @Value("${cambio.cod.autorizacion.bio.clv}")
    private String cveCambioBiometrico;
    @Value("${obs.cambio.cod.autorizacion.bio.asp.pago}")
    private String obsCambioBiometricoAspPago;

    public RespuestaDTO registrarTokenHuella(RegistraTokenHuellaReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //Se busca el solicitante mediante el telefono registrado para ASP Pago
            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoBasicaSolicitanteByTelefonoCoDi(req.getTelefono());
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresTokenHuella.SOLICITANTE_NOT_FOUND,
                        ErroresTokenHuella.getMensajeError(ErroresTokenHuella.SOLICITANTE_NOT_FOUND, req.getTelefono()));
            }

            //Se obtiene el registro CoDi mediante el solicitante id
            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(solicitanteOBJ.getNumero());
            respuesta = validaRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0)
                return respuesta;

            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(regCoDiOBJ.getCuenta());
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresTokenHuella.CUENTA_NOT_FOUND,
                        ErroresTokenHuella.descError.get(ErroresTokenHuella.CUENTA_NOT_FOUND));
            }

            log.info("tipoCuentaAh:: " + posGlobCuentaObj.getTipoCuenta());
            // Determinar el tipo de cuenta y el metodo a usar
            boolean esAhorro = posGlobCuentaObj.getTipoCuenta().equals("AHORRO");

            ConceptoCuentaOBJ conceptoCuentaOBJ = esAhorro
                    ? conceptosCuentaDAO.obtenerConceptoCuentaProcreaByClave(claveBiometrico, regCoDiOBJ.getSolicitanteId(), posGlobCuentaObj.getCuentaah())
                    : conceptosCuentaDAO.obtenerConceptoCuentaCeroByClave(claveBiometrico, regCoDiOBJ.getSolicitanteId(), posGlobCuentaObj.getCuentaah());

            if (conceptoCuentaOBJ == null) {
                log.info("---> Se registra nuevo biometrico para cliente :: " + regCoDiOBJ.getSolicitanteId());
                respuesta = esAhorro
                        ? conceptosCuentaDAO.guardarConceptoCuentaProcrea(claveBiometrico, req.getData(), regCoDiOBJ.getCuenta())
                        : conceptosCuentaDAO.guardarConceptoCuentaCero(claveBiometrico, req.getData(), regCoDiOBJ.getCuenta());

                if (respuesta.getCodigo() != 0) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresTokenHuella.ERROR_REGISTRA_BIOMETRICO,
                            ErroresTokenHuella.descError.get(ErroresTokenHuella.ERROR_REGISTRA_BIOMETRICO));
                }
                return RespuestaUtils.respuestaExito(respuesta, 0, "Biométrico registrado con éxito.");
            } else {
                log.info("---> Se actualiza nuevo biometrico para cliente :: " + regCoDiOBJ.getCuenta());

                RespuestaDTO respCero = conceptosCuentaDAO.actualizarValorConceptoCuentaCeroByPersonaId(regCoDiOBJ.getSolicitanteId(), claveBiometrico, req.getData());
                RespuestaDTO respProcrea = conceptosCuentaDAO.actualizarValorConceptoCuentaProcreaByPersonaId(regCoDiOBJ.getSolicitanteId(), claveBiometrico, req.getData());

                if (respCero.getCodigo() == 0 || respProcrea.getCodigo() == 0) {
                    solicitanteDAO.registrarBitacoraCambio(regCoDiOBJ.getSolicitanteId(), cveCambioBiometrico, 9, conceptoCuentaOBJ.getValor(), req.getData(), obsCambioBiometricoAspPago);
                    return RespuestaUtils.respuestaExito(respuesta, 0, "Biométrico actualizado con éxito.");
                } else {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresTokenHuella.ERROR_ACTUALIZA_BIOMETRICO,
                            ErroresTokenHuella.descError.get(ErroresTokenHuella.ERROR_ACTUALIZA_BIOMETRICO));
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

    private RespuestaDTO validaRegistroCoDi(RegistroCoDiOBJ registroCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();

        if (registroCoDiOBJ == null) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresTokenHuella.REGISTRO_CODI_NOT_FOUND,
                    ErroresTokenHuella.descError.get(ErroresTokenHuella.REGISTRO_CODI_NOT_FOUND));
        }

        //Validamos que la cuente este presente en la nueva versión de la app
        if (!versionApp.equals(registroCoDiOBJ.getRegistroNuevaApp())) {
            return RespuestaUtils.asignarError(respuesta,
                    ErroresTokenHuella.VERSION_APP,
                    ErroresTokenHuella.descError.get(ErroresTokenHuella.VERSION_APP));
        }

        if (!("SI").equalsIgnoreCase(registroCoDiOBJ.getTelefono_validado())) {
            log.info("*** Telefono NO validado");
            return RespuestaUtils.asignarError(respuesta,
                    ErroresTokenHuella.TELEFONO_NO_VALIDADO,
                    ErroresTokenHuella.descError.get(ErroresTokenHuella.TELEFONO_NO_VALIDADO));
        }

        return RespuestaUtils.respuestaExito(respuesta);
    }
}
