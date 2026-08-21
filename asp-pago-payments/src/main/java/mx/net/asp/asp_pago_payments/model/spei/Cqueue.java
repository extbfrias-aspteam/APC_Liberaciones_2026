package mx.net.asp.asp_pago_payments.model.spei;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cqueue implements Serializable
{	
	@Serial
    private static final long serialVersionUID = 1L;
	protected Long codigoError;
	protected String descrError;
}

