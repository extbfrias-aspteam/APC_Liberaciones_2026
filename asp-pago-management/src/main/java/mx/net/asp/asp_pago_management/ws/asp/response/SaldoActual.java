package mx.net.asp.asp_pago_management.ws.asp.response;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FREDY IVAN FLORES
 * Clase que representa el modelado del objeto Linea de Palstico y sus propiedades
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaldoActual implements Serializable{
	@Serial
    private static final long serialVersionUID = -8304541238352919526L;
	private String ID_Cuenta;
	private String ClaveTipoCuenta;
	private String DescripcionTipoCuenta;
	private String Saldo;
}