package mx.net.asp.vf_core_api.ws.asp.vf_credito_ws;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.utilerias.InvokeRestServiceUtil;
import mx.net.asp.vf_core_api.ws.asp.request.BusquedaClientesMorososReq;
import mx.net.asp.vf_core_api.ws.asp.request.ConsultaDeudaCreditoPorCurpReq;
import mx.net.asp.vf_core_api.ws.asp.request.ConsultaMontoLiquidacionCreditoReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSVFCoreCredito_CONSULTAS {

    @Value("${ms.vf.core.credito.host}")
    private String msVFCoreCreditoHost;
    @Value("${ms.vf.core.credito.port}")
    private String msVFCoreCreditoPort;
    @Value("${vf.core.credito.user}")
    private String msVFCoreCreditoUser;
    @Value("${vf.core.credito.pass}")
    private String msVFCoreCreditoPass;

    @Value("${ms.buscar.beneficiarios.service}")
    private String msBuscarBeneficiariosService;
    @Value("${ms.buscar.comisionistas.region.service}")
    private String msBuscarComisionistasRegionService;
    @Value("${ms.buscar.conceptos.service}")
    private String msBuscarConceptosService;
    @Value("${ms.buscar.credito.service}")
    private String msBuscarCreditoService;
    @Value("${ms.buscar.datos.recuperacion.service}")
    private String msBuscarDatosRecuperacionService;
    @Value("${ms.buscar.datos.solicitud.service}")
    private String msBuscarDatosSolicitudService;
    @Value("${ms.buscar.detalle.consulta.service}")
    private String msBuscarDetalleConsultaService;
    @Value("${ms.buscar.domiciliacion.service}")
    private String msBuscarDomiciliacionService;
    @Value("${ms.buscar.expediente.solicitud.service}")
    private String msBuscarExpedienteSolicitudService;
    @Value("${ms.buscar.lista.solicitudes.service}")
    private String msBuscarListaSolicitudesService;
    @Value("${ms.buscar.movimientos.credito.service}")
    private String msBuscarMovimientosCreditoService;
    @Value("${ms.buscar.productos.service}")
    private String msBuscarProductosService;
    @Value("${ms.buscar.saldo.no.aplicado.service}")
    private String msBuscarSaldoNoAplicadoService;
    @Value("${ms.buscar.tabla.pagos.service}")
    private String msBuscarTablaPagosService;
    @Value("${ms.buscar.tasa.service}")
    private String msBuscarTasaService;
    @Value("${ms.buscar.tipos.doctos.service}")
    private String msBuscarTiposDoctosService;
    @Value("${ms.buscar.ultima.consulta.service}")
    private String msBuscarUltimaConsultaService;
    @Value("${ms.consultar.total.pago.service}")
    private String msConsultarTotalPagoService;
    @Value("${ms.consultar.saldo.comisionista.service}")
    private String msConsultarSaldoComisionistaService;
    @Value("${ms.obtener.cuentas.curp.service}")
    private String msObtenerCuentasCurpService;
    @Value("${ms.obtener.monto.liquidacion.service}")
    private String msObtenerMontoLiquidacionService;
    @Value("${ms.calcular.amortizacion.service}")
    private String msCalcularAmortizacionService;
    @Value("${ms.calcular.proxima.fecha.service}")
    private String msCalcularProximaFechaService;
    @Value("${ms.buscar.clientes.morosos.service}")
    private String msBusquedaClientesMorososService;
    @Value("${ms.consulta.deuda.credito.curp.service}")
    private String msConsultaDeudaCreditoPorCurpService;
    @Value("${ms.consulta.monto.liquidacion.credito.service}")
    private String msConsultaMontoLiquidacionCreditoService;

    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    private String getAuthorizationHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(
                (msVFCoreCreditoUser + ":" + msVFCoreCreditoPass).getBytes()
        );
    }

    public RespuestaDTO buscarBeneficiarios(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarBeneficiariosService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarComisionistasPorRegion(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarComisionistasRegionService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarConceptos(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarConceptosService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarCredito(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarCreditoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarDatosRecuperacion(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarDatosRecuperacionService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarDatosSolicitud(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarDatosSolicitudService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarDetalleConsulta(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarDetalleConsultaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarDomiciliacion(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarDomiciliacionService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarExpedienteSolicitud(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarExpedienteSolicitudService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarListaSolicitudes(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarListaSolicitudesService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarMovimientosCredito(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarMovimientosCreditoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarProductos(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarProductosService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarSaldoNoAplicado(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarSaldoNoAplicadoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarTablaPagos(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarTablaPagosService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarTasa(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarTasaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarTiposDoctos(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarTiposDoctosService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO buscarUltimaConsulta(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBuscarUltimaConsultaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO consultarTotalPago(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msConsultarTotalPagoService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO consultarSaldoComisionista(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msConsultarSaldoComisionistaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO obtenerCuentasPorCurp(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msObtenerCuentasCurpService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO obtenerMontoLiquidacion(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msObtenerMontoLiquidacionService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO calculaAmortizacion(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msCalcularAmortizacionService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO calcularProximaFecha(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msCalcularProximaFechaService);

        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarPost(url, jsonReq, getAuthorizationHeader(), MediaType.TEXT_PLAIN);
        return respuesta;
    }

    public RespuestaDTO busquedaClientesMorosos(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msBusquedaClientesMorososService);
        BusquedaClientesMorososReq req = gson.fromJson(jsonReq, BusquedaClientesMorososReq.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("curp", Objects.toString(req.getCurp(), ""));
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, queryParams, getAuthorizationHeader());
        return respuesta;
    }

    public RespuestaDTO consultaDeudaCreditoPorCurp(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msConsultaDeudaCreditoPorCurpService);
        ConsultaDeudaCreditoPorCurpReq req = gson.fromJson(jsonReq, ConsultaDeudaCreditoPorCurpReq.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("curp", Objects.toString(req.getCurp(), ""));
        queryParams.put("fecha", Objects.toString(req.getFecha(), ""));
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, queryParams, getAuthorizationHeader());
        return respuesta;
    }

    public RespuestaDTO consultaMontoLiquidacionCredito(String jsonReq) {
        String url = InvokeRestServiceUtil.getURLRest(msVFCoreCreditoHost, msVFCoreCreditoPort, msConsultaMontoLiquidacionCreditoService);
        ConsultaMontoLiquidacionCreditoReq req = gson.fromJson(jsonReq, ConsultaMontoLiquidacionCreditoReq.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("referencia", Objects.toString(req.getReferencia(), ""));
        queryParams.put("fecha", Objects.toString(req.getFecha(), ""));
        RespuestaDTO respuesta = invokeRestServiceUtil.ejecutarGet(url, queryParams, getAuthorizationHeader());
        return respuesta;
    }
}