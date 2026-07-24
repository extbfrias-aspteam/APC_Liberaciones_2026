package mx.net.asp.asp_pago_management.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.AhorroDAO;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.model.*;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresConsultaSaldo;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_management.ws.asp.WSConsultarTarjeta;
import mx.net.asp.asp_pago_management.ws.asp.request.ConsultarTarjetaReq;
import mx.net.asp.asp_pago_management.ws.asp.response.ConsultarTarjetaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class SaldoFacilService {

    private final AhorroDAO ahorroDAO;
    private final ErrorHandler errorHandler;
    private final WSConsultarTarjeta wsConsultarTarjeta;

    @Value("${ip.host.transaccion}")
    private String ipHost;

    private final Gson gson;

    public RespuestaDTO consultaSaldoFacil(PosGlobalCuentaOBJ posGlobCuentaObj) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Double saldoAhorro;
        try {
            if (Boolean.TRUE.equals(posGlobCuentaObj.isTienePlastico())) {
                ConsultarTarjetaReq req;
                if (!"".equals(posGlobCuentaObj.getTarjetaVirtual()))
                    req = buildConsultaTarjetaReq(posGlobCuentaObj.getTarjetaVirtual(), "V");
                else if ("".equals(posGlobCuentaObj.getTarjetaFisica()))
                    req = buildConsultaTarjetaReq(posGlobCuentaObj.getTarjetaFisica(), "F");
                else
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresConsultaSaldo.TARJETA_NO_VALIDA,
                            ErroresConsultaSaldo.descError.get(ErroresConsultaSaldo.TARJETA_NO_VALIDA));

                respuesta = wsConsultarTarjeta.consultarTarjeta(req);
                if (respuesta.getCodigo() == 0) {
                    ConsultarTarjetaResponse consultarTarjetaResponse = gson.fromJson(respuesta.getData(), ConsultarTarjetaResponse.class);
                    saldoAhorro = consultarTarjetaResponse.getSaldoActual().stream()
                            .filter(saldo -> "Cuenta Autorización".equals(saldo.getDescripcionTipoCuenta()))
                            .map(saldo -> Double.parseDouble(saldo.getSaldo()))
                            .findFirst()
                            .orElse(0.0);
                } else
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresConsultaSaldo.ERROR_ADMIN_PLASTICOS,
                            ErroresConsultaSaldo.getMensajeError(ErroresConsultaSaldo.ERROR_ADMIN_PLASTICOS, respuesta.getMensaje()));

            } else
                saldoAhorro = ahorroDAO.obtenerSaldoByCuentaAh(posGlobCuentaObj.getCuentaah());

            if (saldoAhorro == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresConsultaSaldo.ERROR_CONSULTA_SALDO,
                        ErroresConsultaSaldo.descError.get(ErroresConsultaSaldo.ERROR_CONSULTA_SALDO));
            }

            RespuestaUtils.respuestaExito(respuesta, String.valueOf(saldoAhorro));
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private ConsultarTarjetaReq buildConsultaTarjetaReq(String tarjeta, String tipoTarjeta) {
        ConsultarTarjetaReq req = new ConsultarTarjetaReq();
        HeaderWS header = new HeaderWS();
        header.setIdEmpresa(1L);
        header.setIdCanalAtencion(2L);
        header.setIdSucursal(1L);
        header.setIdUsuario(9L);
        header.setIpHost(ipHost);
        header.setIdTransaccion(2);

        req.setHeader(header);
        req.setNumeroTarjeta(tarjeta);
        req.setTipoTarjeta(tipoTarjeta);

        return req;
    }
}
