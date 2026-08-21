package mx.net.asp.asp_pago_payments.model.spei;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

@Data
@AllArgsConstructor
public class RespuestaBodySVC implements Serializable{
	@Serial
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Object>	params;

	public RespuestaBodySVC(){
		
	}
	
	public void addValor(String nombre, Object valor){
		if(params == null){
			params = new HashMap<>();
		}
		params.put(nombre, valor);
	}
	
	public Object getValor(String nombre){
		return params == null ? null :  params.get(nombre);
	}
}
