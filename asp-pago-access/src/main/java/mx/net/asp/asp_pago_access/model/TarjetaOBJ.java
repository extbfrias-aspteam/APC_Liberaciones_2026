package mx.net.asp.asp_pago_access.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaOBJ {
    private String proveedor; // Nombre de proveedor
    private String numeroTarjeta; // Numero de tarjeta
    private String tipoTarjeta; // Tipo_tarjeta V o F
    //private String tarjetaPrincipal; // true o false
    private Integer mostrarTarjetaAPP; //puede contener 0 o 1
}
