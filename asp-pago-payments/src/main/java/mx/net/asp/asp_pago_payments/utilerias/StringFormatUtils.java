package mx.net.asp.asp_pago_payments.utilerias;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

@Component
public class StringFormatUtils {
    public String limpiarEspacios(String cadena){
        String resultado = cadena.replaceAll("\\s+", "");
        return resultado;
    }
    public String limpiarCaracteresEspeciales(String cadena){
        // Expresión regular para encontrar caracteres especiales excepto espacios en blanco
        String regex = "[^a-zA-Z0-9 ]";
        String resultado = cadena.replaceAll(regex, "");
        return resultado;
    }
    public String limitarLongitud(String texto, int maxLength) {
        return texto.length() > maxLength ? texto.substring(0, maxLength) : texto;
    }
    public String generaFolio(){
        String folio = "";
        try{
            Calendar fecha = Calendar.getInstance();
            int ano = fecha.get(Calendar.YEAR);
            int soloAno = Integer.parseInt(String.valueOf(ano).substring(2));
            int mes = fecha.get(Calendar.MONTH) + 1;
            int dia = fecha.get(Calendar.DAY_OF_MONTH);
            int hora = fecha.get(Calendar.HOUR_OF_DAY);
            int minuto = fecha.get(Calendar.MINUTE);
            int segundo = fecha.get(Calendar.SECOND);
            int miliSegundo = fecha.get(Calendar.MILLISECOND);
            int miliSegundoMod = (miliSegundo % 128);

            String binAno = ComplementaBinario(decimalToBinario(soloAno),7);
            String binMes = ComplementaBinario(decimalToBinario(mes),4);
            String binDia = ComplementaBinario(decimalToBinario(dia),5);
            String binHora = ComplementaBinario(decimalToBinario(hora),5);
            String binMinuto = ComplementaBinario(decimalToBinario(minuto),6);
            String binSegundo = ComplementaBinario(decimalToBinario(segundo),6);
            String binMiliSeg = ComplementaBinario(decimalToBinario(miliSegundo),7);
            String binMiliSegMod = ComplementaBinario(decimalToBinario(miliSegundoMod),7);

            String cadena = binAno + binMes + binDia + binHora + binMinuto + binSegundo + binMiliSegMod;
            folio = binarioToHex(cadena);
        }catch(Exception e){
            throw new RuntimeException("Error al generar Folio:: ", e);
        }
        return folio;
    }
    private String ComplementaBinario(String b, int r) {
        String binario = b;
        while(binario.length() < r){
            binario = "0" + binario;
        }
        return binario;
    }

    private String decimalToBinario(int numero){
        StringBuilder ala = new StringBuilder();
        int n = numero;
        String numerobinario = "";
        numerobinario = numerobinario + (n % 2);
        n = n / 2;

        while (n >= 2) {
            numerobinario = numerobinario + (n % 2);
            n = n / 2;
        }
        numerobinario = numerobinario + n;
        StringBuilder cadena = ala.append(numerobinario);
        cadena = ala.reverse();
        //System.out.println(cadena);
        return cadena.toString();
    }

    private String binarioToHex(String bin){
        String result = "";
        StringBuilder resultado = new StringBuilder();
        try{

            for ( int i = 0 ; i < bin.length()-1 ; i +=4 ) {
                int    numero  = Integer.parseInt( bin.substring( i, i+4) , 2 );
                String reprHex = Integer.toString( numero, 16 );
                resultado.append( reprHex );
            }
            result = resultado.toString();
        }catch(Exception e){
            throw new RuntimeException("Error en binarioToHex:: ", e);
        }
        return result;
    }

    public String generaClaveRastreo(Long consecutivo) {
        // Obtener la fecha actual en formato yyyyMMdd
        String sFecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String cve = String.format("%s%s%s", sFecha, "90659ASPOPC00", generaReferenciaNumerica(consecutivo));

        // Recortar la clave a 29 caracteres
        return cve.substring(0, Math.min(cve.length(), 29)).trim();
    }

    private int generaReferenciaNumerica(Long consecutivo) {
        // Obtener la fecha y hora actual
        LocalDateTime now = LocalDateTime.now();

        int dia = now.getDayOfYear();
        int hora = now.getHour();
        int min = now.getMinute();
        int seg = now.getSecond();
        int nano = now.getNano(); // Se usa nano en lugar de milisegundos

        String seq;
        if (consecutivo != null && consecutivo != 0) {
            seq = String.format("%04d", consecutivo);
        } else {
            seq = String.valueOf(hora + min + seg + (nano / 1_000_000)); // Convertir nanosegundos a milisegundos
        }

        String ref = String.format("%s%s000000000", dia, seq);
        ref = ref.substring(0, 7);

        return Integer.parseInt(ref);
    }
}
