package mx.net.asp.vf_core_api.service.vf_core_credito;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.dto.ServiceResponse;
import mx.net.asp.vf_core_api.utilerias.ErrorHandler;
import mx.net.asp.vf_core_api.utilerias.RespuestaUtils;
import mx.net.asp.vf_core_api.utilerias.errores.ErroresGenerales;
import mx.net.asp.vf_core_api.ws.asp.vf_credito_ws.WSVFCoreCredito_CONSULTAS;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class VFCoreCreditoService_CONSULTAS {

    private final WSVFCoreCredito_CONSULTAS wsVFCoreCreditoConsultas;

    private final ErrorHandler errorHandler;
    private final Gson gson;

    public ServiceResponse buscarBeneficiarios(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarBeneficiarios(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarComisionistasPorRegion(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarComisionistasPorRegion(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarConceptos(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarConceptos(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarCredito(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarCredito(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarDatosRecuperacion(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarDatosRecuperacion(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarDatosSolicitud(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarDatosSolicitud(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarDetalleConsulta(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarDetalleConsulta(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarDomiciliacion(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarDomiciliacion(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarExpedienteSolicitud(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarExpedienteSolicitud(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarListaSolicitudes(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarListaSolicitudes(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarMovimientosCredito(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarMovimientosCredito(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarProductos(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarProductos(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarSaldoNoAplicado(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarSaldoNoAplicado(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarTablaPagos(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarTablaPagos(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarTasa(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarTasa(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarTiposDoctos(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarTiposDoctos(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse buscarUltimaConsulta(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.buscarUltimaConsulta(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultarTotalPago(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.consultarTotalPago(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultarSaldoComisionista(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.consultarSaldoComisionista(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse obtenerCuentasPorCurp(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.obtenerCuentasPorCurp(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse obtenerMontoLiquidacion(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.obtenerMontoLiquidacion(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse calculaAmortizacion(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.calculaAmortizacion(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse calcularProximaFecha(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.calcularProximaFecha(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse busquedaClientesMorosos(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.busquedaClientesMorosos(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultaDeudaCreditoPorCurp(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.consultaDeudaCreditoPorCurp(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ServiceResponse consultaMontoLiquidacionCredito(String jsonReq) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = wsVFCoreCreditoConsultas.consultaMontoLiquidacionCredito(jsonReq);
            return RespuestaUtils.evaluaRespuesta(respuesta, respuesta.getData(), false);
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));

            return new ServiceResponse(gson.toJson(respuesta), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}