package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhorroSaldosOBJ {
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
