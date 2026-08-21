package mx.net.asp.asp_pago_documents.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnexoDetalleBeneficiarioOBJ {
    private String cuenta;
    private String personaId;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String fechaNacimiento;
    private String porcentaje;
    private String rfc;
    private String curp;
    private String calle;
    private String noExterior;
    private String noInterior;
    private String colonia;
    private String municipio;
    private String entidadFederativa;
    private String cp;
    private String telefono;
    private String correo;
    private String nombreSolicitante;
    private String apellidoPaternoSolicitante;
    private String apellidoMaternoSolicitante;
    private String fechaNacimientoSolicitante;
    private String porcentajeSolicitante;
    private String rfcSolicitante;
    private String curpSolicitante;
    private String calleSolicitante;
    private String noExtSolicitante;
    private String noIntSolicitante;
    private String coloniaSolicitante;
    private String municipioSolicitante;
    private String entidadFederativaSolicitante;
    private String cpSolicitante;
    private String telefonoSolicitante;
    private String correoSolicitante;
    private List<String> nombres;
    private List<String> porcentajes;
    private String nombreBeneficiario;
    private String porcentajeBeneficiario;
    private String recaFacil;
    private String pRutaImagen;
    private String domicilioCompleto;
    private String localidad;
    private String localidadSolicitante;
    private String nombreBeneficiarioDetalle;
    private String curpBeneficiarioDetalle;
    private String rfcBeneficiarioDetalle;
    private String cpBeneficiarioDetalle;
    private String coloniaBeneficiarioDetalle;
    private String ciudadBeneficiarioDetalle;
    private String municipioBeneficiarioDetalle;
    private String estadoBeneficiarioDetalle;
    private String calleBeneficiarioDetalle;
    private String noExtBeneficiarioDetalle;
    private String noIntBeneficiarioDetalle;
    private String telefonoBeneficiarioDetalle;
    private String correoBeneficiarioDetalle;
    private String fechaNacimientoBeneficiarioDetalle;
    private String porcentajeBeneficiarioDetalle;
    private String parentescoBeneficiarioDetalle;
}
