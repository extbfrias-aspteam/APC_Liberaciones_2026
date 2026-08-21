package mx.net.asp.asp_pago_payments.ws.asp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FraudeCheckFields {

    @JsonProperty("CodigoEntidad")
    private String CodigoEntidad;

    @JsonProperty("IdCliente")
    private String IdCliente;

    @JsonProperty("UsuarioBancaDigital")
    private String UsuarioBancaDigital;

    @JsonProperty("CodigoClienteTitular")
    private String CodigoClienteTitular;

    @JsonProperty("TipoPersona")
    private String TipoPersona;

    @JsonProperty("OrigenDeTransaccion")
    private String OrigenDeTransaccion;

    @JsonProperty("AccionDentroSesion")
    private String AccionDentroSesion;

    @JsonProperty("CodigoTransaccion")
    private String CodigoTransaccion;

    @JsonProperty("CodigoRespuestaHost")
    private String CodigoRespuestaHost;

    @JsonProperty("FechaTrx")
    private String FechaTrx;

    @JsonProperty("HoraTrx")
    private String HoraTrx;

    @JsonProperty("MonedaTrx")
    private String MonedaTrx;

    @JsonProperty("MontoTotalTrxLocal")
    private String MontoTotalTrxLocal;

    @JsonProperty("MontoTotalTrxDolares")
    private String MontoTotalTrxDolares;

    @JsonProperty("TipodeCambio")
    private String TipodeCambio;

    @JsonProperty("LocalInternacional")
    private String LocalInternacional;

    @JsonProperty("TipoTransaccion")
    private String TipoTransaccion;

    @JsonProperty("NumeroReferencia")
    private String NumeroReferencia;

    @JsonProperty("ClaveTransferInterbancaria")
    private String ClaveTransferInterbancaria;

    @JsonProperty("IDRastreoTransferencias")
    private String IDRastreoTransferencias;

    @JsonProperty("Reversa")
    private String Reversa;

    @JsonProperty("DatosRetoMFA")
    private String DatosRetoMFA;

    @JsonProperty("EnviaPreguntaReto")
    private String EnviaPreguntaReto;

    @JsonProperty("RespuestasCorrectas")
    private String RespuestasCorrectas;

    @JsonProperty("MFARetoSolicitado")
    private String MFARetoSolicitado;

    @JsonProperty("MFATipoReto")
    private String MFATipoReto;

    @JsonProperty("MFAAprobado")
    private String MFAAprobado;

    @JsonProperty("MFAPrioridad")
    private String MFAPrioridad;

    @JsonProperty("CodigoProductoTitular")
    private String CodigoProductoTitular;

    @JsonProperty("CodigoSubproductoTitular")
    private String CodigoSubproductoTitular;

    @JsonProperty("NumerodeCuentaTitular")
    private String NumerodeCuentaTitular;

    @JsonProperty("FechaAperturaCtaTitular")
    private String FechaAperturaCtaTitular;

    @JsonProperty("TipoVIPCuentaTitular")
    private String TipoVIPCuentaTitular;

    @JsonProperty("FechaVinculacionCliente")
    private String FechaVinculacionCliente;

    @JsonProperty("EstatusCuentaProductoTitular")
    private String EstatusCuentaProductoTitular;

    @JsonProperty("SignoValorSaldoCuentaTitular")
    private String SignoValorSaldoCuentaTitular;

    @JsonProperty("SaldoDisponibleCuentaTitular")
    private String SaldoDisponibleCuentaTitular;

    @JsonProperty("EmpleadoCuentaTitular")
    private String EmpleadoCuentaTitular;

    @JsonProperty("SucursalAperturaCuentaTitular")
    private String SucursalAperturaCuentaTitular;

    @JsonProperty("NombreCompletoClienteCuentaTitular")
    private String NombreCompletoClienteCuentaTitular;

    @JsonProperty("TelefonoLaboralCuentaTitular")
    private String TelefonoLaboralCuentaTitular;

    @JsonProperty("TelefonoCelularCuentaTitular")
    private String TelefonoCelularCuentaTitular;

    @JsonProperty("TelefonoResidenciaCuentaTitular")
    private String TelefonoResidenciaCuentaTitular;

    @JsonProperty("SegmentoClienteCuentaTitular")
    private String SegmentoClienteCuentaTitular;

    @JsonProperty("CorreoElectronicoCuentaTitular")
    private String CorreoElectronicoCuentaTitular;

    @JsonProperty("EjecutivoCuentaTitular")
    private String EjecutivoCuentaTitular;

    @JsonProperty("DominioEMailCuentaTitular")
    private String DominioEMailCuentaTitular;

    @JsonProperty("CodigoClienteDestino")
    private String CodigoClienteDestino;

    @JsonProperty("FechaAperturaCuentaDestino")
    private String FechaAperturaCuentaDestino;

    @JsonProperty("EjecutivoCuentaDestino")
    private String EjecutivoCuentaDestino;

    @JsonProperty("SucursalAperturaCuentaDestino")
    private String SucursalAperturaCuentaDestino;

    @JsonProperty("TelefonoCuentaDestino")
    private String TelefonoCuentaDestino;

    @JsonProperty("EmailCuentaDestino")
    private String EmailCuentaDestino;

    @JsonProperty("DominioEmailDestino")
    private String DominioEmailDestino;

    @JsonProperty("CodigoPaisDestino")
    private String CodigoPaisDestino;

    @JsonProperty("CodClienteDestino")
    private String CodClienteDestino;

    @JsonProperty("EmpleadoCtaDestino")
    private String EmpleadoCtaDestino;

    @JsonProperty("BancoDestino")
    private String BancoDestino;

    @JsonProperty("TipoProductoDestino")
    private String TipoProductoDestino;

    @JsonProperty("NumeroCuentaDestino")
    private String NumeroCuentaDestino;

    @JsonProperty("NombreClienteTitularCtaDestino")
    private String NombreClienteTitularCtaDestino;

    @JsonProperty("ProveedorServicio")
    private String ProveedorServicio;

    @JsonProperty("CategoriaPagoServicio")
    private String CategoriaPagoServicio;

    @JsonProperty("ReferenciaPagoServicio1")
    private String ReferenciaPagoServicio1;

    @JsonProperty("ReferenciaPagoServicio2")
    private String ReferenciaPagoServicio2;

    @JsonProperty("ReferenciaPagoServicio3")
    private String ReferenciaPagoServicio3;

    @JsonProperty("Origendealerta")
    private String Origendealerta;

    @JsonProperty("IdentTrxFraude")
    private String IdentTrxFraude;

    @JsonProperty("IndicadordeFraude")
    private String IndicadordeFraude;

    @JsonProperty("IndicadorRealTime")
    private String IndicadorRealTime;

    @JsonProperty("IPConexion")
    private String IPConexion;

    @JsonProperty("Correlativo")
    private String Correlativo;

    @JsonProperty("SessionID")
    private String SessionID;

    @JsonProperty("fingerPrint")
    private FingerPrint fingerPrint;
}
