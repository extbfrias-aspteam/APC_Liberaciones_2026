package mx.net.asp.asp_pago_management.config;

public class ApiException extends RuntimeException {

    private final int codigo;

    public ApiException(int codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}