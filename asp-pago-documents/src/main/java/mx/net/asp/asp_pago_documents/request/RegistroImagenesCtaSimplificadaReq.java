package mx.net.asp.asp_pago_documents.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroImagenesCtaSimplificadaReq {
    @NotBlank(message = "El campo 'numeroCuenta' es obligatorio")
    private String numeroCuenta;
    
    @NotEmpty(message = "Debe proporcionar al menos una ruta de imagen INE")
    private List<@NotBlank(message = "Las rutas no pueden estar vacías") String> rutasIne;

    @NotBlank(message = "El campo 'fotoSelfie' es obligatorio")
    private String fotoSelfie;
}
