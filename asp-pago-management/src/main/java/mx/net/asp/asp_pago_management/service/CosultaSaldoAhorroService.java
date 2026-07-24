package mx.net.asp.asp_pago_management.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.*;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.response.ObtenerSaldoAhorroSAV2Response;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresConsultaSaldo;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class CosultaSaldoAhorroService {
    private final NucleoCentralDAO nucleoCentralDAO;
    private final SaldoAhorroService saldoAhorroService;
    private final Environment env;
    private final SaldoFacilService saldoFacilService;
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public RespuestaDTO obtenerSaldoAhorro(String cuenta) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            //Se obtiene si la cuenta es de cero o procrea
            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(cuenta);
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresConsultaSaldo.CUENTA_NOT_FOUND,
                        ErroresConsultaSaldo.descError.get(ErroresConsultaSaldo.CUENTA_NOT_FOUND));
            }

            if ("AHORRO".equals(posGlobCuentaObj.getTipoCuenta())) {
                respuesta = saldoAhorroService.consultaSaldoAhorro(cuenta);
                if (respuesta.getCodigo() != 0) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresConsultaSaldo.ERROR_CONSULTA_SALDO,
                            ErroresConsultaSaldo.descError.get(ErroresConsultaSaldo.ERROR_CONSULTA_SALDO));
                }
            } else {
                respuesta = saldoFacilService.consultaSaldoFacil(posGlobCuentaObj);
            }

            if (respuesta.getCodigo() == 0) {
                ObtenerSaldoAhorroSAV2Response saldoOBJ = new ObtenerSaldoAhorroSAV2Response();
                saldoOBJ.setSaldo(Double.parseDouble(respuesta.getData()));
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(saldoOBJ));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public Double obtenerSaldoAhorroCuenta(String cuenta) {
        RespuestaDTO respuesta;
        Double saldo = 0d;
        try {
            //Se obtiene si la cuenta es de cero o procrea
            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(cuenta);
            if (posGlobCuentaObj == null) {
                return 0d;
            }

            if ("AHORRO".equals(posGlobCuentaObj.getTipoCuenta())) {
                respuesta = saldoAhorroService.consultaSaldoAhorro(cuenta);
                if (respuesta.getCodigo() != 0) {
                    return 0d;
                }
            } else {
                respuesta = saldoFacilService.consultaSaldoFacil(posGlobCuentaObj);
            }

            if (respuesta.getCodigo() == 0) {
                saldo = Double.parseDouble(respuesta.getData());
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return 0d;
        }
        return saldo;
    }
}
