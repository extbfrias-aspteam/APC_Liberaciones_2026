package mx.net.asp.asp_pago_documents.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentacionCtaFacilOBJ {
    private String cuenta;
    private String cuentaClabe;
    private String referencia;
    private Integer sucursalId;
    private Integer productoId;
    private String empresa;
    private String nombreSolicitanteCompleto;
    private String nombreSolicitante;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correo;
    private String telefono;
    private String rfc;
    private String curp;
    private String fechaNaciemiento;
    private String noExterior;
    private String noInterior;
    private String calle;
    private String personaId;
    private String colonia;
    private String cp;
    private String municipio;
    private String estado;
    private String localidad;
    private String nombreBeneficiario;
    private String parentescoBeneficiario;
    private String porcentajeBeneficiario;
    private String recaFacil;
}
