package mx.net.asp.asp_pago_access.utilerias;

public enum EventoControl {

    CREATE("CREATE"),
    INCREMENT("INCREMENTO"),
    BLOQUEO("BLOQUEO"),
    BLOQUEADO("BLOQUEADO"),
    DESBLOQUEO("DESBLOQUEO"),
    RESET_VENTANA("RESET_VENTANA");

    private final String clave;

    EventoControl(String clave) {
        this.clave = clave;
    }

    public String getClave() {
        return clave;
    }
}
