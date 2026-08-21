package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.ProcreaDAO;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.*;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresPSpeiSimple;
import mx.net.asp.asp_pago_payments.ws.asp.WSAplicaPagoCF;
import mx.net.asp.asp_pago_payments.ws.asp.request.AplicaPagoReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class PagoCreditoService {

    private final Gson gson;
    private final ErrorHandler errorHandler;
    private final WSAplicaPagoCF wsAplicaPagoCF;
    private final ProcreaDAO procreaDAO;

    @Value("${id.banco.pago.credito.cf}")
    private Integer idBancoPagoCF;
    @Value("${app.pago.credito.cf}")
    private Integer appagoCreditoCF;
    @Value("${usuario.pago.credito.cf}")
    private Integer usuarioPagoCreditoCF;
    @Value("${forma.pago.credito.cf}")
    private Integer formaPagoCreditoCF;
    @Value("${canal.pago.cf}")
    private Integer canalPagoCreditoCF;
    @Value("${obs.pago.credito.cf}")
    private String observacionPagoCF;

    public RespuestaDTO aplicaPagoCF(SpeiSimpleOBJ speiSimple, PosGlobalCreditoOBJ posicionGlobalCredito) {
        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            AplicaPagoReq aplicaPagoReq = new AplicaPagoReq();
            HeaderWS header = new HeaderWS();

            header.setIdUsuario(usuarioPagoCreditoCF);
            aplicaPagoReq.setApp(appagoCreditoCF);
            aplicaPagoReq.setBanco(idBancoPagoCF);
            aplicaPagoReq.setCuenta(posicionGlobalCredito.getContratocr());
            aplicaPagoReq.setFormaPago(formaPagoCreditoCF);
            aplicaPagoReq.setHeader(header);
            aplicaPagoReq.setImportacionId(0);
            aplicaPagoReq.setMonto(speiSimple.getMonto());
            aplicaPagoReq.setReferencia(posicionGlobalCredito.getContratocr());
            aplicaPagoReq.setTarjetaId("");
            aplicaPagoReq.setTransaccionId("");
            aplicaPagoReq.setValidarImportacion(false);
            aplicaPagoReq.setCanalId(canalPagoCreditoCF);
            aplicaPagoReq.setObservaciones(observacionPagoCF);
            aplicaPagoReq.setHeader(header);

            respuesta = wsAplicaPagoCF.aplicaPagoCf(aplicaPagoReq);

            if (respuesta.getCodigo() <= -501 && respuesta.getCodigo() >= -503) {
                respuesta.setCodigo(ErroresGenerales.ERROR_WS);
                respuesta.setMensaje(ErroresGenerales.descError.get(ErroresGenerales.ERROR_WS));
                respuesta.setData(null);
            }

        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO, ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return respuesta;
    }

    @Async
    public CompletableFuture<RespuestaDTO> aplicaPagoCreditoProcrea(SpeiSimpleOBJ speiSimple, PosGlobalCreditoOBJ posicionGlobalCredito) {
        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            Boolean credConsolidado = procreaDAO.validaCreditoConsolidado(posicionGlobalCredito.getContratocr(), "ELI_PAG");
            log.info("Consolidado :: {}", credConsolidado);
            if (Boolean.TRUE.equals(credConsolidado)) {
                Integer accesoId = procreaDAO.obtenerSecuenciaAccesoId();

                log.info("Cuenta :: {}", speiSimple.getCuentaOrigen());
                log.info("Credito :: {}", posicionGlobalCredito.getContratocr());
                log.info("Monto :: {}", speiSimple.getMonto());
                log.info("Acceso id :: {}", accesoId);

                respuesta = procreaDAO.realizarPagoCreditoProcrea(speiSimple.getCuentaOrigen(),
                        posicionGlobalCredito.getContratocr(), speiSimple.getMonto(), accesoId, idBancoPagoCF,
                        formaPagoCreditoCF);
            } else {
                RespuestaUtils.asignarError(respuesta,
                        ErroresPSpeiSimple.CREDITO_CONSOLIDADO,
                        ErroresPSpeiSimple.descError.get(ErroresPSpeiSimple.CREDITO_CONSOLIDADO));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return CompletableFuture.completedFuture(respuesta);
    }
}