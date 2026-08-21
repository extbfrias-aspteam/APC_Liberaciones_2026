package mx.net.asp.asp_pago_jwt.utilerias;

import lombok.Getter;

import java.util.List;

@Getter
public class DatosInvalidosException extends RuntimeException {
    private final List<String> errores;

    public DatosInvalidosException(List<String> errores) {
        super("Error de validación en Request");
        this.errores = errores;
    }

}
