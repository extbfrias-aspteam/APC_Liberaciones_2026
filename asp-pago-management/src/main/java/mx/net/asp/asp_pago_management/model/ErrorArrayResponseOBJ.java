package mx.net.asp.asp_pago_management.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorArrayResponseOBJ {
    private List<String> detail;
}
