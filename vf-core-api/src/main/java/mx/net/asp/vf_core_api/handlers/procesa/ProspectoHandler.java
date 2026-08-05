package mx.net.asp.vf_core_api.handlers.procesa;

import lombok.RequiredArgsConstructor;
import mx.net.asp.vf_core_api.utilerias.HelpersUtil;
import mx.net.asp.vf_core_api.ws.asp.request.ProspectoOBJ;
import mx.net.asp.vf_core_api.ws.asp.request.WsVfReq;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProspectoHandler extends AbstractProcesaHandler {

    private final HelpersUtil helpersUtil;

    @Override
    protected String getCodigo() {
        return "800100";
    }

    @Override
    public void handle(Map<String, Object> mapa, WsVfReq req) {

        ProspectoOBJ p = new ProspectoOBJ();

        p.setApellidoMaterno(helpersUtil.trim((String) mapa.get("materno")));
        p.setApellidoPaterno(helpersUtil.trim((String) mapa.get("paterno")));
        p.setCalle1(helpersUtil.trim((String) mapa.get("calle")));
        p.setCalle2(helpersUtil.trim((String) mapa.get("entre")));
        p.setCalle3(helpersUtil.trim((String) mapa.get("yCalle")));

        Double estado = helpersUtil.asDouble(mapa.get("estado"));
        Double municipio = helpersUtil.asDouble(mapa.get("municipio"));

        p.setEstado(String.format("%02d", estado.intValue()));
        p.setMunicipio(String.format("%03d", municipio.intValue()));

        p.setLocalidad(helpersUtil.trim((String) mapa.get("localidad")));
        p.setNombre(helpersUtil.trim((String) mapa.get("nombre")));
        p.setTelefono(helpersUtil.trim((String) mapa.get("telefono")));

        String monto = (String) mapa.get("monto");
        p.setMonto(Double.valueOf(monto));

        p.setTipoProducto(helpersUtil.asInt(mapa.get("tipoProducto")));
        p.setTipoTelefono(helpersUtil.asInt(mapa.get("tipoTel")));
        p.setAdministradorComisionistasId(helpersUtil.asInt(mapa.get("cofContratoId")));

        req.setProspecto(p);
    }
}

