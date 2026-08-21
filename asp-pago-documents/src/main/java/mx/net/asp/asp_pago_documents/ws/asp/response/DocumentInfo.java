package mx.net.asp.asp_pago_documents.ws.asp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentInfo {
    private String QR;
    private String calle;
    private String cic;
    private String ciudad;
    private String claveElector;
    private String codigoValidacion;
    private String colonia;
    private String curp;
    private String emision;
    private String estado;
    private String fechaNacimiento;
    private String identificadorCiudadano;
    private String localidad;
    private String mrz;
    private String municipio;
    private String nombres;
    private String ocr;
    private String primerApellido;
    private String registro;
    private String seccion;
    private String segundoApellido;
    private String sexo;
    private String subTipo;
    private String tipo;
    private String vigencia;
}
