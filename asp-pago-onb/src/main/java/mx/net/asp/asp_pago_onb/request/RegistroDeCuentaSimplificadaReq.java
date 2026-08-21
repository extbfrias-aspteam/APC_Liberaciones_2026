package mx.net.asp.asp_pago_onb.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mx.net.asp.asp_pago_onb.model.GeolocalizacionOBJ;
import mx.net.asp.asp_pago_onb.model.HeaderWS;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDeCuentaSimplificadaReq {
    //	@NotNull(message = "El campo 'header' es obligatorio")
    private HeaderWS header;
    @NotBlank(message = "El campo 'primerNombre' es obligatorio")
    private String primerNombre;
    private String segundoNombre;
    @NotBlank(message = "El campo 'apellidoPaterno' es obligatorio")
    private String apellidoPaterno;
    @NotBlank(message = "El campo 'apellidoMaterno' es obligatorio")
    private String apellidoMaterno;
    @NotBlank(message = "El campo 'rfc' es obligatorio")
    private String rfc;
    @NotBlank(message = "El campo 'curp' es obligatorio")
    @Size(min = 18, max = 18, message = "El campo 'curp' debe tener 18 caracteres.")
    private String curp;
    @NotBlank(message = "El campo 'celular' es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El campo 'celular' debe contener exactamente 10 dígitos numéricos")
    private String celular;
    @NotBlank(message = "El campo 'email' es obligatorio")
    private String email;
//    @NotBlank(message = "El campo 'ine' es obligatorio")
//    private String ine;
    //@NotBlank(message = "El campo 'codigoPromocion' es obligatorio")
    private String codigoPromocion;
    private String codigoReferencia;
    @NotBlank(message = "El campo 'domicilio' es obligatorio")
    private String domicilio;
    @NotBlank(message = "El campo 'codigoPostal' es obligatorio")
    @Pattern(regexp = "^\\d{5}$", message = "El campo 'codigoPostal' debe contener exactamente 5 dígitos numéricos")
    private String codigoPostal;
    @NotNull(message = "El campo 'coloniaId' es obligatorio")
    @Min(value = 1, message = "El campo 'coloniaId' no debe ser 0")
    private Integer coloniaId;
    @NotBlank(message = "El campo 'coloniaNombre' es obligatorio")
    private String coloniaNombre;
    private Boolean ineValidado;
//    @NotBlank(message = "El campo 'localidad' es obligatorio")
//    private String localidad;
//    @NotBlank(message = "El campo 'sexo' es obligatorio")
    private String sexo;
    @NotBlank(message = "El campo 'codigoAutorizacion' es obligatorio")
    private String codigoAutorizacion;
    @NotBlank(message = "El campo 'aceptaTerminos' es obligatorio")
    @Pattern(regexp = "SI|NO", message = "El campo 'aceptaTerminos' solo puede contener 'SI' o 'NO'")
    private String aceptaTerminos;
    @NotBlank(message = "El campo 'aceptaUsoBiometricos' es obligatorio")
    @Pattern(regexp = "SI|NO", message = "El campo 'aceptaUsoBiometricos' solo puede contener 'SI' o 'NO'")
    private String aceptaUsoBiometricos;
    @NotBlank(message = "El campo 'password' es obligatorio")
    private String password;
    @NotBlank(message = "El campo 'actuoCuentaPropia' es obligatorio")
    @Pattern(regexp = "1", message = "El campo 'actuoCuentaPropia' solo puede ser '1'")
    private String actuoCuentaPropia;
    private GeolocalizacionOBJ geolocalizacion;
    private Boolean validarInfo;
}
