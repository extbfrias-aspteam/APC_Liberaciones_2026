package mx.net.asp.asp_pago_payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AhCuentaOBJ {
	private HeaderWS header;
	private String cuenta;
	private String referencia;
	private String solicitanteId;
	private String cliente_id;
	private String cliente;
	private Integer estatusId;
	private Integer producto_id;
	private String claveProducto;
	private String fechaApertura;
	private String nivel_cuenta;
	private String actuoCuentaPropia;
	private String onboarding;
	private Integer productoAhorroId;
	private double montoApertura;
	private Integer sucursalId;
	private String clabeInterbancaria;
	private double rendimiento;
	private Double gatNominal;
	private Double gatReal;
	private Integer monedaId;
	private Integer asesorId;
	private Integer comoEnteroId;
	private String comoEnteroObs;
	private Integer usuarioCreacion;
	private String tipoCliente;
	private double monto_max_aho_cuenta;
	private double monto_ingresos_mensuales;
	private Integer ocupacion_id;
	private String ocupacion;
	private Integer actividad_id;
	private String actividad;
	private Integer giro_id;
	private String giro;
	private String cve_destino;
	private String destino;
	private String centro_Trabajo;
	private String puesto;
}
