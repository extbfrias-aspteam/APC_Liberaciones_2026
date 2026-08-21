package mx.net.asp.asp_pago_payments.model.spei;

import lombok.AllArgsConstructor;
import lombok.Data;
import mx.net.asp.asp_pago_payments.utilerias.errores.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Queue;

@Data
@AllArgsConstructor
public class RespuestaErrorSVC implements Serializable{

	public static final String STI = "STI";
	
	@Serial
	private static final long serialVersionUID = 1L;
	private Queue<Cqueue>  qErrores;
	
	public RespuestaErrorSVC(){
		qErrores = new ArrayDeque<>();
	}
	
	///////////////////////////////////  COLA DE ERRORES /////////////////////////////////////////
	
	private String  ObtenDescripcion(String modulo, long id) 
	{
		String descError= "";
		if(RespuestaErrorSVC.STI.equals(modulo)){
			descError = ErroresSTI.descError.get(id);
		}
		return descError;
	}
	
	/**
	 *  AGREGA UN ERROR A LA COLA, ACEPTA ARGUMENTOS QUE SE AGREGARAN A LA CADENA DEL MENSAJE
	 * @param modulo  Modulo al que pertenece el error
	 * @param id Codigo de Error,
	 * @param args  Argumentos Adcionales, se sustituye en las cadenas (%s)
	 */
	public void  addCodigo(String modulo, long id, Object ... args) 
	{
		Cqueue   cqueue;
		String descError;
		
		cqueue = new Cqueue();
		if(qErrores == null){
			qErrores = new ArrayDeque<>();
		}
		
		descError= ObtenDescripcion(modulo, id);		
		cqueue.setCodigoError(id);
		cqueue.setDescrError(String.format(descError, args));
		qErrores.add(cqueue);
	}
	
	/**
	 *  AGREGA UN ERROR A LA COLA. 
	 * @param modulo Modulo al que pertenece el error
	 * @param id  Codigo de Error,
	 */
	public void  addCodigo(String modulo, long id) 
	{
		Cqueue   cqueue;
		String descError;
		
		cqueue = new Cqueue();
		if(qErrores == null){
			qErrores = new ArrayDeque<>();
		}

		descError= ObtenDescripcion(modulo, id);		
		cqueue.setCodigoError(id);
		cqueue.setDescrError(descError);
		qErrores.add(cqueue);
	}
	
	/**
	 *  INICIALIZA LA COLA DE ERRORES CUANDO  EL PARAMETRO DE ERROR ES IGAL A 0
	 * @param codigoError  0= INICIALIZA LA COLA
	 */
	public void setCodigoError( long codigoError) {
		if (codigoError == 0) {
			qErrores = new ArrayDeque<>();
		}
	}

	
	/**
	 *  OBTIENE UN CADENA CON TODOS LOS ERRORES DE LA COLA.
	 *  @return  REGRESA UNA CADENA CON FORMATO CODIGO + DESCRIPCION 
	 */	
	public String ObtenerErrores ()
	{
		String errordescr;
		String newl;
		
		newl = "\r\n";
		errordescr= "";
		for (Cqueue cqueue : qErrores ) {
			errordescr += String.format("No. codigo %s :  %s %s", cqueue.getCodigoError(),  cqueue.getDescrError(), newl );			
		}		
		return errordescr;
	}	
	
	/**
	 *  OBTIENE LA DESCRIPCION DEL ULTIMO ERROR EN LA COLA 
	 *   @return  DESCRIPCION DEL ERROR 
	 */
	public String getDescError ()
	{
		String errordescr;
		String newl;

        errordescr= "";
		for (Cqueue cqueue : qErrores ) {
			errordescr = String.format("%s", cqueue.getDescrError() );
			break;
		} 		
		return errordescr; 
	}
	
	/**
	 *  OBTIENE LA DESCRIPCION DEL ULTIMO ERROR EN LA COLA 
	 *   @return  REGRESA UNA CADENA CON FORMATO CODIGO + DESCRIPCION
	 */
	public String obtenerUltimaDescripcionError ()
	{
		String errordescr;
		String newl;
		
		newl = "\r\n";
		errordescr= "";			
		for (Cqueue cqueue : qErrores ) {
			errordescr = String.format("No codigo %s :  %s %s", cqueue.getCodigoError(),  cqueue.getDescrError(), newl ); 
		} 		
		return errordescr; 
	}
	
	/**
	 * OBTIENE EL CODIGO  DEL ULTIMO ERROR
	 * @return   CODIGO ERROR 
	 */
	public long getCodigoError()
	{
		long codigo= 0L;
		
		if (qErrores== null) {
			return codigo ;
		}		
		for (Cqueue cqueue : qErrores ) {
			codigo = cqueue.getCodigoError(); 			
			break;
		}
		return codigo;		
	}
	
	/**
	 * OBTIENE EL CODIGO  DEL ULTIMO ERROR
	 * @return   CODIGO ERROR 
	 */
	public long obtenerUltimoError()
	{
		long codigo= 0L;
		
		if (qErrores== null) {
			return codigo;
		}		
		for (Cqueue cqueue : qErrores ) {
			codigo = cqueue.getCodigoError(); 
		}
		return codigo;		
	}
	
	
	/**
	 * OBTIENE LA DESCRIPCION DEL ULTIMO ERROR 
	 * @return   DESCRIPCION 
	 */
	/*public String descripcionError()
	{
		String  descr= "";
		
		if (qErrores== null) {
			descr= "";
		}
		for (Cqueue cqueue : qErrores ) {
			descr = cqueue.getDescrError(); 
		}
		return descr;		
	}*/
	
	private  Queue< Cqueue>  CopyQueue( Queue< Cqueue>  source)
	{
		Queue< Cqueue>  tmpQueue;
		Cqueue tmpcqueue;
		
		tmpQueue = new ArrayDeque<>();
		for (Cqueue cqueue : source ) {
			tmpcqueue= new Cqueue();
			tmpcqueue.setCodigoError(cqueue.getCodigoError());
			tmpcqueue.setDescrError(cqueue.getDescrError());
			tmpQueue.add(tmpcqueue);
		}
		return tmpQueue;
	}
	
	private  Queue< Cqueue>  CopyQueue(Queue< Cqueue>  tmpQueue,  Queue< Cqueue>  source)
	{
		Cqueue tmpcqueue;
		
		if (tmpQueue == null) {
			tmpQueue = new ArrayDeque<>();
		}
		for (Cqueue cqueue : source ) {
			tmpcqueue= new Cqueue();
			tmpcqueue.setCodigoError(cqueue.getCodigoError());
			tmpcqueue.setDescrError(cqueue.getDescrError());
			tmpQueue.add(tmpcqueue);
		}
		return tmpQueue;
	}

	/**
	 * OBTIENE LA COLA DE LOS ERRORES
	 * @return  COLA DE ERRORES 
	 */	
	public  Queue< Cqueue>  getErrores()
	{
		Queue< Cqueue>  tmpQueue;
		
		tmpQueue = CopyQueue(qErrores);
		return tmpQueue;
	}
	
	/**
	 *  COPIA UNA COLA DE ERRORES A LA COLA LOCAL
	 *  @param qSource  RECIBE COLA DE ERRORES
	 */
	public void  setErrores(Queue< Cqueue>  qSource)
	{
		qErrores = CopyQueue(qSource);
	}
	
	public void  setAddErrores(Queue< Cqueue>  qSource)
	{
		qErrores= CopyQueue(qErrores, qSource);
	}
	
}
