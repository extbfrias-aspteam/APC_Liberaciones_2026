package mx.net.asp.asp_pago_management.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistraFavoritoReq {
    private Integer id;
    @NotBlank(message = "El campo 'numeroCuentaCoDi' es obligatorio")
    private String numeroCuentaCoDi;
    @NotBlank(message = "El campo 'nombreBeneficiario' es obligatorio")
    private String nombreBeneficiario;
    @NotBlank(message = "El campo 'numeroCuentaBeneficiario' es obligatorio")
    private String numeroCuentaBeneficiario;
    @NotNull(message = "El campo 'tipoCuentaBeneficiario' es obligatorio")
    @Min(value = 1, message = "El campo 'tipoCuentaBeneficiario' no debe ser 0")
    private Integer tipoCuentaBeneficiario;
    @NotNull(message = "El campo 'idInstitucionBeneficiario' es obligatorio")
    @Min(value = 1, message = "El campo 'idInstitucionBeneficiario' no debe ser 0")
    private Integer idInstitucionBeneficiario;
    //@NotBlank(message = "El campo 'nombreInstitucion' es obligatorio")
    private String nombreInstitucion;
    private String correoBeneficiario;
    private Integer idEstatus;
    private String alias;
    private boolean guardarFavorito;
    private String personaId;
}
