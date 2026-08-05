package mx.net.asp.vf_core_api.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.config.JwtEndpointsExcluidosProperties;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.ws.asp.WSAspPagoJWT;
import mx.net.asp.vf_core_api.ws.asp.request.ValidaJWTReq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Log4j2
public class JwtValidationFilter extends OncePerRequestFilter {

    @Value("${ms.jwt.valida.jwt.service}")
    private String validaJwtService;
    @Value("${valida.jwt.token}")
    private Boolean validaJwtToken;
    @Value("${jwt.default.idcanal:459}")
    private Integer defaultIdCanal;

    private final JwtEndpointsExcluidosProperties endpointsExcluidos;
    private final WSAspPagoJWT wsAspPagoJWT;
    private final ObjectMapper objectMapper;

    public JwtValidationFilter(
            JwtEndpointsExcluidosProperties endpointsExcluidos,
            WSAspPagoJWT wsAspPagoJWT,
            ObjectMapper objectMapper
    ) {
        this.endpointsExcluidos = endpointsExcluidos;
        this.wsAspPagoJWT = wsAspPagoJWT;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (Boolean.TRUE.equals(validaJwtToken)) {
            if (!endpointsExcluidos.getExcluidos().stream()
                    .anyMatch(excluido -> request.getRequestURI().contains(excluido))) {

                log.info("Entrando a JWT Filter para {} con X-JWT-Token: {}", request.getRequestURI(), request.getHeader("X-JWT-Token"));

                //obtener cabecera JWT
                String jwtHeader = "";
                if (request.getHeader("X-JWT-Token") != null && !request.getHeader("X-JWT-Token").isEmpty()) {
                    jwtHeader = request.getHeader("X-JWT-Token");
                }

                // Canal
                String canalHeader = request.getHeader("X-Id-Canal");
                Integer idCanal = canalHeader != null ?
                        Integer.valueOf(canalHeader) : defaultIdCanal;

                // Llamar al servicio para validar JWT
                ValidaJWTReq validaJWTReq = new ValidaJWTReq(
                        jwtHeader,
                        request.getRequestURI(),
                        "",
                        "",
                        idCanal
                );

                RespuestaDTO respuesta = wsAspPagoJWT.validaJwtToken(validaJWTReq);
                if (respuesta.getCodigo() != 0) {
                    enviarErrorJson(response, request, respuesta);
                    return;
                }

                log.info("JWT Validado correctamente");
            }
        } else {
            log.info("=== Validacion JWT deshabilitada ===");
        }

        filterChain.doFilter(request, response);
    }

    private void enviarErrorJson(HttpServletResponse response, HttpServletRequest request, RespuestaDTO respuesta) throws IOException {
        log.error("Error valida JWT: {}", respuesta.getMensaje());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // Usando Jackson para serializar objeto a JSON
        String json = objectMapper.writeValueAsString(respuesta);

        response.getWriter().write(json);
    }
}
