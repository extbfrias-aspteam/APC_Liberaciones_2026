package mx.net.asp.vf_core_api.handlers.procesa;

import lombok.RequiredArgsConstructor;
import mx.net.asp.vf_core_api.ws.asp.request.WsVfReq;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class Envio2FAActivacionHandler extends AbstractProcesaHandler {

    @Override
    protected String getCodigo() {
        return "850100";
    }

    @Override
    public void handle(Map<String, Object> mapa, WsVfReq req) {
        req.setCelular((String) mapa.get("celcom"));
        req.setCelular2((String) mapa.get("celevf"));
        req.setReferencia((String) mapa.get("cuenta"));
    }
}