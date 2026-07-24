package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiciosTercerosOBJ {
    private Long id;
    private String servicio;
    private String tipo_producto;
    private String url;
    private String nememp;
    private String subemp;
    private Double comision;
    private List<ServiciosMontosOBJ> montos;
    private String codigo;
    private boolean esFavorito;
}
