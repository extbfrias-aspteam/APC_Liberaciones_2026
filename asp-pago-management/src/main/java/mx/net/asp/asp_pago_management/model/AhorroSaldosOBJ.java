package mx.net.asp.asp_pago_management.model;

import java.io.Serial;
import java.io.Serializable;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AhorroSaldosOBJ implements Serializable {
	@Serial
    private static final long serialVersionUID = 1L;

	private Integer ahorroSaldosId;
	private String cuenta;
	private String solicitanteId;
	private Double saldoReal;
	private Double saldoPromedio;
	private Double saldoAcumulado;
	private Date fechaCorte;
	private Date fechaDeposito;
	private Double intereses;
	private Double iva;
	private Double isr;
	private Double retenciones;
	private Double desviacion;
	private Integer dias;
	private Double saldoDisponible;
	private Double ide;

}
