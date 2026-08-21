package mx.net.asp.asp_pago_access.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_access.model.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp {
    private Integer pinPago;
    //private String referenciaAhorro;
    //private List<ValoresPropertiesOBJ> properties;
    private String idCPU;
    private String idCPV;
    private Integer indicadorAdquiriente;
    private String nombreAdquiriente;
    private List<CatInstiOBJ> listCatinsti;
    //private String urlEncuesta;
    //private List<UrlServiciosOBJ> urlAsp;
    private List<UrlServiciosOBJ> urlCoDi;
    // private List<CreditoOBJ> creditosActivos;
    private List<ConfiguracionASPPagoOBJ> configuracionesASPpago;
    //private List<FavoritoOBJ> favoritosTransferencias;
    private List<CostoOBJ> costos;
    //private Integer numeroNotificacionesPendientes;
    // private String tipoCuenta;
    /// private CuentaAspPagoOBJ cuenta;
    private List<String> cuentas;
    private String nombre;
    private String id;
    private String correo;
    private String lastLogin;
    private String telefono;
    private boolean solicitarDocumentacion;
}


