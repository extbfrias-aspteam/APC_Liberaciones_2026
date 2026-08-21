package mx.net.asp.asp_pago_services_v2.config;

public class ServicioException extends RuntimeException {
    private int codigo;
    private String mensaje;

    public ServicioException(int codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
