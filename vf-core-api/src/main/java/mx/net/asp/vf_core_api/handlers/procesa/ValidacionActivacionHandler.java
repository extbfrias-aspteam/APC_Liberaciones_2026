package mx.net.asp.vf_core_api.handlers.procesa;

import lombok.RequiredArgsConstructor;
import mx.net.asp.vf_core_api.ws.asp.request.WsVfReq;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ValidacionActivacionHandler extends AbstractProcesaHandler {

    @Override
    protected String getCodigo() {
        return "850200";
    }

    @Override
    public void handle(Map<String, Object> mapa, WsVfReq req) {
        req.setTokenCliente((String) mapa.get("2facom"));
        req.setTokenCliente2((String) mapa.get("2faevf"));
        req.setReferencia((String) mapa.get("cuenta"));
    }
}
