package mx.net.asp.asp_pago_management.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultarTarjetaResponse {
    private String IDSolicitud;
    private String CodRespuesta;
    private String DescRespuesta;
    private String Tarjeta;
    private String TipoManufactura;
    private String Cuenta;
    private String CLABE;
    private String CuentaCacao;
    private String FechaVigencia;
    private String Status;
    private String DescripcionStatus;
    private String Nombre;
    private String PrimerApellido;
    private ArrayList<SaldoActual> SaldoActual;
}
