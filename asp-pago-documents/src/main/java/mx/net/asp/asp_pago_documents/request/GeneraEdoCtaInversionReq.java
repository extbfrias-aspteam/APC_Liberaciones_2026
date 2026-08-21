package mx.net.asp.asp_pago_documents.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneraEdoCtaInversionReq {
    @NotNull(message = "El año no puede ser nulo")
    @Min(value = 2020, message = "El año debe ser igual o mayor a 2020")
    @Max(value = 9999, message = "El año debe tener como máximo 4 dígitos")
    private Integer anio;
    @NotNull(message = "El número de mes no puede ser nulo")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer numMes;
    @NotBlank(message = "El campo 'cuentaInversion' es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El campo 'cuentaInversion' debe contener 10 digitos numéricos.")
    private String cuentaInversion;
}