package mx.net.asp.vf_core_api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaCOF {
    private Integer resultado;
    private String respuesta;
    private String msgHost;
    private String referencia;
    private String numAutorizacion;
    private Object objeto;
    private Integer resultadoCatel;
}
