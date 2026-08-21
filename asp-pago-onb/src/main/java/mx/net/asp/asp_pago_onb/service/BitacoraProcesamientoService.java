package mx.net.asp.asp_pago_onb.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dao.BitacoraProcesamientoDAO;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import mx.net.asp.asp_pago_onb.model.*;
import mx.net.asp.asp_pago_onb.utilerias.ErrorHandler;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
@Log4j2
@AllArgsConstructor
public class BitacoraProcesamientoService {

    private final BitacoraProcesamientoDAO bitacoraProcesamientoDAO;
    private final Gson gson;
    private final Environment env;
    private final ErrorHandler errorHandler;

    public Integer registraBitPro(BitacoraProcesamientoOBJ bitPro) {
        try {
            bitPro.setId(bitacoraProcesamientoDAO.nuevoBitPro(bitPro));
            return bitPro.getId();
        } catch (Exception e) {
            errorHandler.handleException(e);
            return 0;
        }
    }

    public Integer sigSecProId() {
        try {
            return bitacoraProcesamientoDAO.sigSecProId();
        } catch (Exception e) {
            errorHandler.handleException(e);
            return 0;
        }
    }

    public RespuestaDTO registraBitProList(String req) {
        RespuestaDTO resp = new RespuestaDTO();
        RegistraBitacoraProcesamientoReqOBJ regBitPro = gson.fromJson(req, RegistraBitacoraProcesamientoReqOBJ.class);

        if (regBitPro != null) {
            Type listType = new TypeToken<List<BitacoraProcesamientoOBJ>>() {}.getType();
            List<BitacoraProcesamientoOBJ> bitProList = gson.fromJson(regBitPro.getData(), listType);

            if (bitProList != null) {
                try {
                    Integer idProcesamiento = sigSecProId();
                    for (BitacoraProcesamientoOBJ bitPro : bitProList) {
                        bitPro.setIdProcesamiento(idProcesamiento);
                        bitPro.setId(registraBitPro(bitPro));
                    }

                    resp.setCodigo(0);
                    resp.setMensaje("OK");
                } catch (Exception e) {
                    errorHandler.handleException(e);
                    resp.setCodigo(-1);
                    resp.setMensaje("Se presento un problema al registrar la bitacora de operaciones");
                }
            }
        }
        return resp;
    }
}
