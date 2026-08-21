package mx.net.asp.asp_pago_management.service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.request.ValidaCurpCecobanReq;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;

import java.util.Arrays;

@Log4j2
@Service
@RequiredArgsConstructor
public class GeneraCurpService {
    private final ErrorHandler errorHandler;

    private static final Set<String> NOMBRES_INVALIDOS = new HashSet<>(Arrays.asList("MARIA", "MA", "MA.", "JOSE", "J", "J."));
    private static final Set<String> PREPOSICIONES = new HashSet<>(Arrays.asList("DA", "DAS", "DE", "DEL", "DER", "DI", "DIE", "DD", "EL", "LA", "LOS", "LAS", "LE", "LES", "MAC", "MC", "VAN", "VON", "Y"));

    private static final Map<String, String> PALABRAS_INCONVENIENTES = new HashMap<>();

    static {
        // Mapeo de palabras inconvenientes
        String[] palabrasAltisonantes = {
                "BACA", "BAKA", "BUEI", "BUEY", "CACA", "CACO", "CAGA", "CAGO", "CAKA", "CAKO",
                "COGE", "COGI", "COJA", "COJE", "COJI", "COJO", "COLA", "CULO", "FALO", "FETO",
                "GETA", "GUEI", "GUEY", "JETA", "JOTO", "KACA", "KACO", "KAGA", "KAGO", "KAKA",
                "KAKO", "KOGE", "KOGI", "KOJA", "KOJE", "KOJI", "KOJO", "KOLA", "KULO", "LILO",
                "LOCO", "LOKA", "LOKO", "MAME", "MAMO", "MEAR", "MEAS", "MEON", "MIAR", "MION",
                "MOCO", "MOKO", "MULA", "MULO", "NACA", "NACO", "PEDA", "PEDO", "PENE", "PIPI",
                "PITO", "POPO", "PUTA", "PUTO", "QULO", "RATA", "ROBA", "ROBE", "ROBO", "RUIN",
                "SENO", "TETA", "VACA", "VAGA", "VAGO", "VAKA", "VUEI", "VUEY", "WUEI", "WUEY",
                "LOCA"
        };
        for (String palabra : palabrasAltisonantes) {
            PALABRAS_INCONVENIENTES.put(palabra, palabra.charAt(0) + "X" + palabra.substring(2));
        }
    }

    public RespuestaDTO generaCurp(ValidaCurpCecobanReq generaCurpOBJ) {

        RespuestaDTO respuesta = new RespuestaDTO();

        try {
            // Limpiar nombres y apellidos
            String apellidoPaterno = limpiarTexto(generaCurpOBJ.getApellidoPaterno());
            String apellidoMaterno = limpiarTexto(generaCurpOBJ.getApellidoMaterno());
            String nombre = limpiarTexto(generaCurpOBJ.getNombre());

            // Aplicar reglas para obtener iniciales
            apellidoPaterno = eliminarPreposicion(apellidoPaterno);
            apellidoMaterno = eliminarPreposicion(apellidoMaterno);
            nombre = obtenerPrimerNombreValido(nombre);

            String fechaNacimiento = generaCurpOBJ.getFechaNacimiento();
            String sexo = generaCurpOBJ.getSexo();
            String entidad = generaCurpOBJ.getEntidadFederativaNacimiento();

            // Posiciones 1-4: Generar los primeros 4 caracteres de la CURP
            String curpInicial = obtenerInicialesCurp(apellidoPaterno, apellidoMaterno, nombre);

            // Verificar palabras inconvenientes
            curpInicial = PALABRAS_INCONVENIENTES.getOrDefault(curpInicial, curpInicial);

            // Posiciones 5-10: Obtener fecha de nacimiento en formato YYMMDD
            String curpFecha = convertirFechaFormatoCURP(fechaNacimiento);

            // Posición 11: Sexo
            String curpSexo = sexo.toUpperCase().equals("H") ? "H" : "M";

            // Posiciones 12-13: Entidad Federativa (Se asume que ya viene en formato correcto)
            String curpEstado = entidad.toUpperCase();

            // Posiciones 14-16: Primeras consonantes internas
            String curpConsonantes = obtenerConsonantesInternas(apellidoPaterno, apellidoMaterno, nombre);

            // CURP final de 16 posiciones; CURP Final (sin homoclave aún)
            String curp = curpInicial + curpFecha + curpSexo + curpEstado + curpConsonantes;

            log.info("CURP generada: {}", curp);
            respuesta.setCodigo(0);
            respuesta.setMensaje("OK");
            respuesta.setData(curp);
            
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }

        return respuesta;
    }

    // Paso 1: Limpiar texto (Eliminar acentos, reemplazar Ñ por X)
    private String limpiarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) return "X";

        texto = texto.toUpperCase().replace("Ñ", "X"); // Reemplazar Ñ antes de normalizar
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");  // Elimina acentos y diéresis
    }

    // Paso 2: Eliminar preposiciones en nombres y apellidos
    private String eliminarPreposicion(String texto) {
        String[] palabras = texto.split(" ");
        int index = 0;

        // Si la primera palabra es una preposición, usa la siguiente
        while (index < palabras.length && PREPOSICIONES.contains(palabras[index])) {
            index++;
        }

        return (index < palabras.length) ? palabras[index] : "X";
    }
    
    // Paso 3: Validar nombres compuestos y eliminar "MARIA", "JOSE"
    private String obtenerPrimerNombreValido(String nombre) {
        String[] nombres = nombre.split(" ");
        int index = 0;

        // Si el primer nombre es inválido, usar el siguiente
        if (index < nombres.length && NOMBRES_INVALIDOS.contains(nombres[index])) {
            index++;
        }

        // Si el siguiente nombre también es una preposición, ignorarla
        while (index < nombres.length && PREPOSICIONES.contains(nombres[index])) {
            index++;
        }

        return (index < nombres.length) ? nombres[index] : "X";
    }

    // Paso 4: Obtener las iniciales para la CURP
    private String obtenerInicialesCurp(String apellidoPaterno, String apellidoMaterno, String nombre) {
        // Obtener las iniciales y vocal del apellido paterno
        char inicialApellidoPaterno = reemplazarEspeciales(apellidoPaterno.charAt(0));
        char primeraVocalApellidoPaterno = obtenerPrimeraVocal(apellidoPaterno);
    
        // Obtener la inicial del apellido materno (si existe)
        char inicialApellidoMaterno = apellidoMaterno.isEmpty() ? 'X' : reemplazarEspeciales(apellidoMaterno.charAt(0));
    
        // Obtener la inicial del nombre
        char inicialNombre = reemplazarEspeciales(nombre.charAt(0));
    
        return "" + inicialApellidoPaterno + primeraVocalApellidoPaterno + inicialApellidoMaterno + inicialNombre;
    }

    // Paso 5: Obtener la primera vocal interna de un texto
    private char obtenerPrimeraVocal(String texto) {
        if (texto.length() < 2){
            for (char c : texto.toCharArray()) {
                if ("AEIOU/-.".indexOf(c) != -1) {
                    return c;
                }

                if ("/-.".indexOf(c) != -1) {
                    return 'X'; // Si encuentra un carácter especial antes, retorna "X"
                }
            }
        } // Si el texto es muy corto, retorna "X"
    
        for (int i = 1; i < texto.length(); i++) { // Empezamos desde la segunda letra
            char c = texto.charAt(i);
            if ("AEIOU".indexOf(c) != -1) {
                return c; // Retorna la primera vocal interna encontrada
            }

            if ("/-.".indexOf(c) != -1) {
                return 'X'; // Si encuentra un carácter especial antes, retorna "X"
            }
        }
        return 'X'; // Si no hay vocal interna, se asigna "X"
    }

    // Paso 6: Convertir fecha a formato YYMMDD para la CURP
    private String convertirFechaFormatoCURP(String fechaNacimiento) {
        String[] partes = fechaNacimiento.split("/");
        if (partes.length != 3) return "000000"; // Si no es un formato válido, retorna 000000

        String dia = partes[0];
        String mes = partes[1];
        String año = partes[2].substring(2, 4);

        return año + mes + dia;
    }

    // Paso 7: Obtener las consonantes internas de los apellidos y nombre
    private String obtenerConsonantesInternas(String apellidoPaterno, String apellidoMaterno, String nombre) {
        char consonantePaterno = obtenerPrimeraConsonanteInterna(apellidoPaterno);
        char consonanteMaterno = obtenerPrimeraConsonanteInterna(apellidoMaterno);
        char consonanteNombre = obtenerPrimeraConsonanteInterna(nombre);

        return "" + consonantePaterno + consonanteMaterno + consonanteNombre;
    }

    // Paso 8: Obtener la primera consonante interna
    private char obtenerPrimeraConsonanteInterna(String palabra) {
        for (int i = 1; i < palabra.length(); i++) { // Ignorar la primera letra
            char c = palabra.charAt(i);
            if ("BCDFGHJKLMNPQRSTVWXYZ".indexOf(c) != -1) {
                return c;
            }

            if ("Ñ".indexOf(c) != -1) {
                return 'X';
            }
        }
        return 'X'; // Si no hay consonante, se asigna "X"
    }

    // Paso 9: Remplazar caracteres especiales por X
    private char reemplazarEspeciales(char c) {
        if ("/-.".indexOf(c) != -1) {
            return 'X'; // Reemplazar solo si es especial
        }
        return c; // Si no es especial, mantener el carácter original
    }
}
