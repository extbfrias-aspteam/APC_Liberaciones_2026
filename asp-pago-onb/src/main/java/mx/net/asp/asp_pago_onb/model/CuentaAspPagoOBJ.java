package mx.net.asp.asp_pago_onb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaAspPagoOBJ {
    private Integer cuentaId;
    private String cuenta;
    private String personaId;
    private String producto;
    private Integer productoId;
    private String descripcion;
    private String clabe;
    private String cobro;
    private String pago;
    private String telefono;
    private String estatus;
    private List<TarjetaOBJ> tarjetas;
    private Integer mostrarTarjetaAPP;
    private String tarjetaFisicaSolicitud;
    private String nivelCuenta;
    private Double udis;
    private Double udisAproxPesos;
    private Boolean bannerSubirNivel;
}
