package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.*;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_onb.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_onb.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_onb.utilerias.StringFormatUtils;
import mx.net.asp.asp_pago_onb.ws.asp.WSConsultaPLD;
import mx.net.asp.asp_pago_onb.ws.asp.request.ConsultaPldReq;
import mx.net.asp.asp_pago_onb.ws.asp.response.ConsultaPldResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@AllArgsConstructor
public class PldService {

    private final CoDiDAO codiDAO;
    private final AhorroDAO ahorroDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final Gson gson;
    private final Environment env;
    private final WSConsultaPLD wsConsultaPLD;
    private final StringFormatUtils stringFormatUtils;
    private final ErrorHandler errorHandler;

    public RespuestaDTO validaSolicitantePLD(SolicitanteOBJ solicitante) {
        RespuestaDTO respuesta = new RespuestaDTO();
        ConsultaPldResponse pldResponse = new ConsultaPldResponse();
        pldResponse.setIdConsulta(123L);
        try {
            ConsultaPldReq consultaPldReq = getConsultaPldReq(solicitante);

            respuesta = wsConsultaPLD.consultaPld(consultaPldReq);

            if (respuesta.getCodigo() == 0) {
                //deserealizamos la respuesta de PLD guardado en el campo data del objeto respuesta
                pldResponse = gson.fromJson(respuesta.getData(), ConsultaPldResponse.class);
                //Se valida que no se haya encontrado en lista PLD
                if ((pldResponse.getCodigo() == 0 && pldResponse.getMensaje().equals("NO_ENCONTRADO")) ||
                        (pldResponse.getCodigo() == 1 && pldResponse.getIdConsulta() != null)) {
                    return RespuestaUtils.respuestaExito(respuesta);
                } else {
                    return RespuestaUtils.asignarError(respuesta,
                            -101,
                            "Es posible que el cliente se encuente en lista PLD");
                }
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        -100,
                        "Ocurrio un error al validar con sistema PLD");
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    @NotNull
    private static ConsultaPldReq getConsultaPldReq(SolicitanteOBJ solicitante) {
        ConsultaPldReq consultaPldReq = new ConsultaPldReq();
        consultaPldReq.setCurp(solicitante.getCurp());
        consultaPldReq.setApellidoPaterno(solicitante.getApellido_p());
        consultaPldReq.setApellidoMaterno(solicitante.getApellido_m());
        consultaPldReq.setNombre(solicitante.getNombre_p());
        consultaPldReq.setTipoPersona(solicitante.getTPersona());
        consultaPldReq.setPaisResidencia("MEXICO");
        consultaPldReq.setClaveAplicativo("ASP_PAGO");
        consultaPldReq.setClaveUsuario("SISTEMAS");
        consultaPldReq.setIdUsuario(9L);
        consultaPldReq.setIdPersona(solicitante.getNumero());
        return consultaPldReq;
    }
}