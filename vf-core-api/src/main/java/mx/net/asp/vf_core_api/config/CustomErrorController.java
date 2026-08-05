package mx.net.asp.vf_core_api.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class CustomErrorController implements ErrorController {


    @RequestMapping("/error")
    public ResponseEntity<RespuestaDTO> handleError(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int statusCode = (status != null) ? Integer.parseInt(status.toString()) : 500;
        String path = (uri != null) ? uri.toString() : "N/A";

        String mensaje;

        // Logging y mensaje según el error
        switch (statusCode) {
            case 404:
                mensaje = "Recurso no encontrado";
                log.error("404 Not Found: {}", path);
                break;
            case 403:
                mensaje = "Acceso prohibido";
                log.error("403 Forbidden: {}", path);
                break;
            case 401:
                mensaje = "No autorizado";
                log.error("401 Unauthorized: {}", path);
                break;
            case 500:
                mensaje = "Error interno del servidor";
                log.error("500 Internal Server Error: {}", path);
                break;
            default:
                mensaje = "Error inesperado";
                log.error("⚠ Error {} en URL {}", statusCode, path);
        }

        // Construimos tu objeto de respuesta
        RespuestaDTO respuesta = new RespuestaDTO(
                statusCode,     // codigo
                mensaje,        // mensaje
                path            // data
        );

        return ResponseEntity.status(statusCode).body(respuesta);
    }
}
