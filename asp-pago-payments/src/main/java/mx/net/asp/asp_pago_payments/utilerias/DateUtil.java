package mx.net.asp.asp_pago_payments.utilerias;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {
	
	public static final String fmtParseFecha = "MM/dd/yyyy HH:mm:ss"; 
	public static final String fmtParseFechaLatin = "dd/MM/yyyy hh:mm:ss"; 
	public static final String fmtParseFechaSinHora = "MM/dd/yyyy";
	public static final String fmtParseHora = "hh:mm";
	
	public static final String fmtSalidaFechaWeb = "dd / MMM / yyyy";
	public static final String fmtSalidaFecha = "dd/MMM/yyyy";
	public static final String fmtParseFechaES = "dd/!!!/yyyy";
	public static final String fmtSalidaFechaBuro = "ddMMyyyy";
	
	static final SimpleDateFormat sdf = new SimpleDateFormat(fmtParseFecha);
	static final SimpleDateFormat sdfSinHora = new SimpleDateFormat(fmtParseFechaSinHora);
	static final SimpleDateFormat sdfHora = new SimpleDateFormat(fmtParseHora);
	static final SimpleDateFormat sdfBuro = new SimpleDateFormat(fmtSalidaFechaBuro);
	static final SimpleDateFormat sdfSinHoraES = new SimpleDateFormat(fmtParseFechaES);
	
	/**
	 * Metodo que devuelve si la fecha1 es anterior a la fecha2
	 **/
	
	public static SimpleDateFormat SdfSinHora()
	{
		return sdfSinHora;
	}
	public static SimpleDateFormat Sdf()
	{
		return sdf;
	}
	
	public static boolean fechaAntes(Date fecha1, Date fecha2) throws ParseException{
		
		String fechaActual = "";
		String fechaAComparar = "";
		
		Date fechaUno = null;
		Date fechaDos = null;
		
		if(fecha1 != null && fecha2 != null){
			
			fechaActual = sdfSinHora.format(fecha1);
			fechaAComparar = sdfSinHora.format(fecha2);
			
			fechaUno = sdfSinHora.parse(fechaActual);
			fechaDos = sdfSinHora.parse(fechaAComparar);
			
			if (fechaUno.before(fechaDos))
				return true;
			else
				return false;
			
		}else{
			
			return false;
		
		}
		
	}
	
	/**
	 * Metodo que devuelve si la fecha1 es posterior a la fecha2
	 * @param fecha1, fecha2
	 * @return boolean
	 */
	public static boolean fechaDespues(Date fecha1, Date fecha2) throws ParseException{
		
		String fechaActual = "";
		String fechaAComparar = "";
		
		Date fechaUno = null;
		Date fechaDos = null;
		
		if(fecha1 != null && fecha2 != null){
			
			fechaActual = sdfSinHora.format(fecha1);
			fechaAComparar = sdfSinHora.format(fecha2);
			fechaUno = sdfSinHora.parse(fechaActual);
			fechaDos = sdfSinHora.parse(fechaAComparar);
			
			if (fechaUno.after(fechaDos))
				return true;
			else
				return false;
			
		}else{
			
			return false;
		
		}
			
	}
	
	/**
	 * Metodo que devuelve el dia de la semana en letra (ej: Lunes, Martes, etc) 
	 * @param dia: Long del dia empezando con 1 = Domingo
	 * @return String
	 */
	public static String diaSemana(int dia){
		String res = switch (dia) {
            case 1 -> "Domingo";
            case 2 -> "Lunes";
            case 3 -> "Martes";
            case 4 -> "Miercoles";
            case 5 -> "Jueves";
            case 6 -> "Viernes";
            case 7 -> "Sabado";
            default -> "Dia Invalido";
        };
        return res;
	}
	
	/**
	 * 
	 * @param fecInicio primera fecha
	 * @param fecFinal segunda fecha
	 * @return numero de dias de diferencia entre las fechas
	 */
	public static int getDifDiasFechas(Date fecInicio, Date fecFinal){
		long tmp = 0L;
		long val = 0;
		
		val = 1000 * 60 * 60 * 24;

		tmp = (fecFinal.getTime() / val) - (fecInicio.getTime() / val); 
		
		return (int) tmp;
	}

	/**
	 * Metodo que devuelve el mes del año en letra (ej: Enero, Febrero, etc) 
	 * @param mes: Long del mes empezando con 1 = Enero
	 * @return String
	 */
	
	public static String mesALetra(int mes){
		String res = switch (mes) {
            case 1 -> "Enero";
            case 2 -> "Febrero";
            case 3 -> "Marzo";
            case 4 -> "Abril";
            case 5 -> "Mayo";
            case 6 -> "Junio";
            case 7 -> "Julio";
            case 8 -> "Agosto";
            case 9 -> "Septiembre";
            case 10 -> "Octubre";
            case 11 -> "Noviembre";
            case 12 -> "Diciembre";
            default -> "Mes no existe";
        };
        return res;
	}
	
	public static boolean isFechaValida(String fecha) {

	    if (fecha == null)
	      return false;

	    //set the format to use as a constructor argument "dd-MM-yyyy" 
	    SimpleDateFormat dateFormat = new SimpleDateFormat(fmtParseFecha);
	    
	    if (fecha.trim().length() != dateFormat.toPattern().length())
	      return false;

	    dateFormat.setLenient(false);
	    
	    try {
	    	dateFormat.parse(fecha.trim());
	    }catch (ParseException pe) {
	      return false;
	    }
	    return true;
	  }
	
	public static Date stringToDate(String fecha) {
        if ("".equals(fecha) || fecha==null) {
        	return null;
        }
	    try {
	    	return  sdf.parse(fecha);
	    }catch (ParseException pe) {
	    	pe.printStackTrace();
	    	return null;
	    }
	  }
	
	public static Date stringToDate(String fecha,String formato) {
        if ("".equals(fecha) || fecha==null) {
        	return null;
        }
	    try {
	    	return   new SimpleDateFormat(formato).parse(fecha); 
	    }catch (ParseException pe) {
	    	pe.printStackTrace();
	    	return null;
	    }
	  }
	
	public static String  StringToDateStringFrm(String fecha,String formato) {
		Date dt;
		String frm= "";		
		int dia;
		int mes;
		int anio;
		int hora;
		int min;
		int sec;
		
		try {
			if (fecha== null || fecha.isEmpty()) {
				return "";
			}	    	
			fecha = fecha.replace("-", "/");
            dt = switch (formato) {
                case "DMYHMS" -> new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(fecha);
                case "DMYHM" -> new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(fecha);
                case "YMDHMS" -> new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(fecha);
                default -> new SimpleDateFormat("MM/dd/yyyy").parse(fecha);
            };
			Calendar fechaConvertir = Calendar.getInstance();	    		
			fechaConvertir.setTime(dt);
			dia= fechaConvertir.get(fechaConvertir.DAY_OF_MONTH);
			mes = fechaConvertir.get(fechaConvertir.MONTH)+1;
			anio = fechaConvertir.get(fechaConvertir.YEAR);
			hora = fechaConvertir.get(fechaConvertir.HOUR_OF_DAY);
			min = fechaConvertir.get(fechaConvertir.MINUTE);
			sec = fechaConvertir.get(fechaConvertir.SECOND);
			
			frm =String.format("%02d/%s/%d", dia, mesALetra(mes).substring(0,3), anio);

            frm = switch (formato) {
                case "DMYHMS" ->
                        String.format("%02d/%s/%d %02d:%02d:%02d", dia, mesALetra(mes).substring(0, 3), anio, hora, min, sec);
                case "DMYHM" ->
                        String.format("%02d/%s/%d %02d:%02d", dia, mesALetra(mes).substring(0, 3), anio, hora, min);
                default -> String.format("%02d/%s/%d", dia, mesALetra(mes).substring(0, 3), anio);
            };

		}
		catch (ParseException pe) {
			pe.printStackTrace();
			frm= "";
		}
		return frm;
	  }
	
	public static String dateToString(Date fecha) {

	    try {
	    	return  sdf.format(fecha);
	    }catch (Exception e) {
	    	return null;
	    }	   
	  }
	
	public static String dateToStringCorta(Date fecha) 
	{
	    try {
	    	return  sdfSinHora.format(fecha);
	    }
	    catch (Exception e) {
	    	return null;
	    }	   
	  }
	
	public static String horaToString(Date fecha) 
	{
	    try {
	    	return  sdfHora.format(fecha);
	    }
	    catch (Exception e) {
	    	return null;
	    }	   
	  }
	
	
	
	public static String dateToStringBuro(Date fecha) {
	    try {
	    	return  sdfBuro.format(fecha);
	    }catch (Exception e) {
	    	return null;
	    }
	   
	  }

	@SuppressWarnings("static-access")
	public static String fechaCompleta(Date fecha){
		
		String fechaCompleta = "";
		
		int dia;
		int mes;
		int anio;
		
		Calendar fechaConvertir = Calendar.getInstance();
		
		fechaConvertir.setTime(fecha);
		
		dia = fechaConvertir.DAY_OF_WEEK;
		mes = fechaConvertir.MONTH;
		anio = fechaConvertir.YEAR;
		
		fechaCompleta = diaSemana(dia) + " " + dia + " de " + mesALetra(mes) + " de " + anio;
		
		return fechaCompleta;
	}
	
	public static java.sql.Timestamp getCurrentTimeStamp() {
		Date today = new Date();
		return new java.sql.Timestamp(today.getTime());
	}
	
	public static String dateToStringDDMMYYYYHHMMSS(Date date){
		if (date == null)
			return null;

		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(date);
	}
	
	public static String dateToStringMMDDYYYYHHMMSS(Date date){
		if (date == null)
			return null;

		return new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(date);
	}
	
	public static java.sql.Timestamp getCurrentTimeStamp(String date) {
		 SimpleDateFormat formato = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		
		Date today = new Date();
		 try {
			today = formato.parse(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        } 
		return new java.sql.Timestamp(today.getTime());
	}
		
	public static java.sql.Timestamp getCurrentTimeStampUS(String date) {
		 SimpleDateFormat formato = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
		
		Date today = new Date();
		 try {
			today = formato.parse(date);
       } catch (ParseException ex) {
           ex.printStackTrace();
       } 
		return new java.sql.Timestamp(today.getTime());
	}

	public static Date dateDDMMYYYY(Date date){
		if (date == null)
			return null;

			try {
				return new SimpleDateFormat("MM/dd/yyyy").parse(dateToStringDDMMYYYYHHMMSS(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return date;
			}
	}
	
	public static String getHoraActual() {
		Calendar         hoy                  = Calendar.getInstance();
		int hora,minutos,segundos,millisegundos;
		hora=hoy.get(Calendar.HOUR_OF_DAY);
		minutos=hoy.get(Calendar.MINUTE);
		segundos=hoy.get(Calendar.SECOND);
		millisegundos=hoy.get(Calendar.MILLISECOND);
        String tiempo= (hora<=9?"0"+hora:hora)+":"+(minutos<=9?"0"+minutos:minutos)+":"+(segundos<=9?"0"+segundos:segundos)+":"+(millisegundos<=9?"0"+millisegundos:millisegundos);
        return tiempo;
    }
	
	public static String HoraDeFechaMilitar() 
	{		
		Calendar         hoy                  = Calendar.getInstance();
		int hora,minutos,segundos,millisegundos;
		hora=hoy.get(Calendar.HOUR_OF_DAY);
		minutos=hoy.get(Calendar.MINUTE);
		segundos=hoy.get(Calendar.SECOND);
		millisegundos=hoy.get(Calendar.MILLISECOND);
        String tiempo= (hora<=9?"0"+hora:hora)+""+(minutos<=9?"0"+minutos:minutos) ;
        return tiempo;
    }
	
	public static String DateToString(Date fecha,String formato) {
		try{
			 return   new SimpleDateFormat(formato).format(fecha);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	  }

	public static String dateToStringWeb(Date dt)
	{
		LocalDate localDate = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String mes = mesALetra(localDate.getMonthValue());
		String fecha= sdfSinHoraES.format(dt);
		fecha= fecha.replace("!!!", mes);
		return fecha.toUpperCase();
	}
	
	public static String dateToStringWebShort(Date dt)
	{
		LocalDate localDate = dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String mes = mesALetra(localDate.getMonthValue());
		String fecha= sdfSinHoraES.format(dt);
		fecha= fecha.replace("!!!", mes.substring(0,3));
		return fecha.toUpperCase();
	}

	public static String  fechaWebCompleta(Date dt)
	{
		Calendar fechaConvertir = Calendar.getInstance();
		String fecha;
		int dia;
		int mes;
		int hora;
		
		fechaConvertir.setTime(dt);
		dia = fechaConvertir.get(Calendar.DAY_OF_WEEK);
		mes = fechaConvertir.get(Calendar.MONTH)+1;
		hora=fechaConvertir.get(Calendar.HOUR_OF_DAY);
		fecha=	String.format("%s, %s de %s %s  %02d:%02d",  diaSemana(dia), fechaConvertir.get(Calendar.DAY_OF_MONTH), mesALetra(mes), fechaConvertir.get(Calendar.YEAR), 
				hora,  fechaConvertir.get(Calendar.MINUTE) );		
		return fecha;
	}
	
	public static String  fechaWebCompletafrm(Date dt)
	{
		Calendar fechaConvertir = Calendar.getInstance();
		String fecha;
		int dia;
		int mes;
		int hora;
		
		fechaConvertir.setTime(dt);
		dia = fechaConvertir.get(Calendar.DAY_OF_WEEK);
		mes = fechaConvertir.get(Calendar.MONTH)+1;
		hora=fechaConvertir.get(Calendar.HOUR_OF_DAY);
		fecha=	String.format("%s, %s de %s %s",  diaSemana(dia), fechaConvertir.get(Calendar.DAY_OF_MONTH), mesALetra(mes), fechaConvertir.get(Calendar.YEAR) 
				 );		
		return fecha;
	}
}
