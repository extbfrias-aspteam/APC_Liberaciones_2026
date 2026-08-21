package mx.net.asp.asp_pago_access.utilerias;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.regex.Pattern;

@Component
public class StringFormatUtils {
    //Validaciones para contrasena ASP Pago
    public boolean tieneCaracteresConsecutivos(String contrasena) {
        // Expresión regular para detectar tres caracteres consecutivos iguales
        Pattern patron = Pattern.compile("(.)\\1{2}");
        return patron.matcher(contrasena).find();
    }
    public boolean contieneNombreCliente(String contrasena, String nombreCliente) {
        // Convertir el nombre del cliente a minúsculas y dividirlo en partes
        String[] partesNombre = nombreCliente.toLowerCase().split(" ");
        // Convertir la contrasena a minúsculas
        String contrasenaSimplificada = contrasena.toLowerCase();
        // Verificar si alguna parte del nombre aparece en la contrasena
        for (String parte : partesNombre) {
            if (!parte.isEmpty() && contrasenaSimplificada.contains(parte)) {
                return true;
            }
        }
        return false;
    }
    public String generaFolio(){
        String folio;
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
        StringBuilder binario = new StringBuilder(b);
        while(binario.length() < r){
            binario.insert(0, "0");
        }
        return binario.toString();
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
        String result;
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
}
