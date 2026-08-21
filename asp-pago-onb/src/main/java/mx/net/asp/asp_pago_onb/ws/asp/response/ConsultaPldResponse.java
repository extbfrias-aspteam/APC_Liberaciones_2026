package mx.net.asp.asp_pago_onb.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaPldResponse {
    private Integer codigo;
    private Long idConsulta;
    private String mensaje;
    private LinkedHashMap<String, String> lista;
}
