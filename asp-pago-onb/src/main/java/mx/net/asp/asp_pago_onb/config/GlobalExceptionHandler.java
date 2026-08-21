package mx.net.asp.asp_pago_onb.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_onb.dto.RespuestaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Log4j2
@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper(); // Para convertir listas a JSON String

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<RespuestaDTO> handleValidationExceptions(MethodArgumentNotValidException ex) throws JsonProcessingException {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<String> camposObligatorios = new ArrayList<>();
        List<String> otrosErrores = new ArrayList<>();

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            FieldError fieldError = (FieldError) error;
            String fieldName = fieldError.getField().contains(".")
                    ? fieldError.getField().substring(fieldError.getField().lastIndexOf(".") + 1)
                    : fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();

            if (errorMessage.contains("obligatorio")) { // NotNull o NotBlank
                camposObligatorios.add(fieldName);
            } else { // Otras validaciones como @Size, @Pattern, etc.
                otrosErrores.add(fieldName + ": " + errorMessage);
            }
        }

        if (!otrosErrores.isEmpty()) {
            respuesta.setCodigo(-301);
            respuesta.setMensaje("No se cumplen las condiciones del atributo.");
            respuesta.setData(objectMapper.writeValueAsString(otrosErrores));
            log.error("Validacion fallida - Atributos con errores: {}", otrosErrores);
        } else {
            respuesta.setCodigo(-300);
            respuesta.setMensaje("Faltan datos obligatorios.");
            respuesta.setData(objectMapper.writeValueAsString(camposObligatorios));
            log.error("Faltan datos obligatorios - Atributos faltantes: {}", camposObligatorios);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON) // Fuerza JSON en la respuesta
                .body(respuesta);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<RespuestaDTO> handleConstraintViolationException(ConstraintViolationException ex) throws JsonProcessingException {
        RespuestaDTO respuesta = new RespuestaDTO();
        List<String> camposObligatorios = new ArrayList<>();
        List<String> otrosErrores = new ArrayList<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = violation.getPropertyPath().toString();

            // Si el campo contiene un ".", obtenemos solo la última parte; si no, lo dejamos igual
            if (fieldName.contains(".")) {
                String[] fieldParts = fieldName.split("\\.");
                fieldName = fieldParts[fieldParts.length - 1]; // Tomamos el último segmento
            }

            String errorMessage = violation.getMessage();

            if (errorMessage.contains("obligatorio")) { // NotNull o NotBlank
                camposObligatorios.add(fieldName);
            } else { // Otras validaciones
                otrosErrores.add(fieldName + ": " + errorMessage);
            }
        }

        if (!otrosErrores.isEmpty()) {
            respuesta.setCodigo(-301);
            respuesta.setMensaje("No se cumplen las condiciones del atributo.");
            respuesta.setData(objectMapper.writeValueAsString(otrosErrores));
            log.error("Validación fallida - Atributos con errores: {}", otrosErrores);
        } else {
            respuesta.setCodigo(-300);
            respuesta.setMensaje("Faltan datos obligatorios.");
            respuesta.setData(objectMapper.writeValueAsString(camposObligatorios));
            log.error("Faltan datos obligatorios - Atributos faltantes: {}", camposObligatorios);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON) // Fuerza JSON en la respuesta
                .body(respuesta);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseEntity<RespuestaDTO> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) throws JsonProcessingException {
        RespuestaDTO respuesta = new RespuestaDTO();
        respuesta.setCodigo(-300);
        respuesta.setMensaje("Faltan datos obligatorios.");
        List<String> faltantes = Collections.singletonList(ex.getParameterName());
        respuesta.setData(objectMapper.writeValueAsString(faltantes));

        log.error("Parametro faltante: {}", ex.getParameterName());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON) // Fuerza JSON en la respuesta
                .body(respuesta);
    }
}
