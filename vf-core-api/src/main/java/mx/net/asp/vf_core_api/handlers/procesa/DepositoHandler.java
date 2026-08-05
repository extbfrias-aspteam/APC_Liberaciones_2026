package mx.net.asp.vf_core_api.handlers.procesa;

import lombok.RequiredArgsConstructor;
import mx.net.asp.vf_core_api.ws.asp.request.WsVfReq;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DepositoHandler extends AbstractProcesaHandler {

    @Override
    protected String getCodigo() {
        return "211000";
    }

    @Override
    public void handle(Map<String, Object> mapa, WsVfReq req) {
        req.setMonto(Double.valueOf((String) mapa.get("monto")));
    }
}
