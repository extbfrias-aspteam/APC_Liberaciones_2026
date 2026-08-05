package mx.net.asp.vf_core_api.handlers.procesa;

import mx.net.asp.vf_core_api.ws.asp.request.WsVfReq;

import java.util.Map;

public interface ProcesaHandler {
    boolean canHandle(String proceso);
    void handle(Map<String,Object> mapa, WsVfReq req);
}
