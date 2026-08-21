package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosEstadoMovimientoOBJ {
    private String titulos;
    private String clabe;
    private String tipoCuenta;
    private String colonia;
    private String ciudad;
    private String cuenta;
    private String rfc;
    private String nombre;
    private String calle;
    private String fechaInicio;
    private String fechaFin;
    private String fechaEmision;
}
