package mx.net.asp.vf_core_api.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.vf_core_api.config.EndpointsDisabledProperties;
import mx.net.asp.vf_core_api.config.SuffixesCanalesPermitidosProperties;
import mx.net.asp.vf_core_api.dto.RespuestaDTO;
import mx.net.asp.vf_core_api.utilerias.ErrorHandler;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Component
@Order(0) // Ejecutar primero
@Log4j2
@RequiredArgsConstructor
public class TraceIdFilter extends OncePerRequestFilter {

    private final ErrorHandler errorHandler;

    @Value("${secret.key.uuid}")
    private String SECRET_KEY_UUID;
    @Value("${trace.id.header}")
    private String TRACE_ID_HEADER;
    @Value("${trace.suffix}")
    private String TRACE_SUFFIX;

    private final EndpointsDisabledProperties endpointsDisabledProperties;
    private final SuffixesCanalesPermitidosProperties canalesPermitidos;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        ThreadContext.clearAll();

        log.info(">>> Entrando a TraceIdFilter para: {}", request.getRequestURI());

        //validacion de endpoint deshabilitado
        String uri = request.getRequestURI();
        var match = endpointsDisabledProperties.getDeshabilitados().entrySet().stream()
                .filter(e -> uri.contains(e.getKey()))
                .findFirst();

        if (match.isPresent()) {
            log.info("Endpoint {} deshabilitado por configuracion", uri);

            String mensaje = match.get().getValue();

            RespuestaDTO respuesta = new RespuestaDTO();
            respuesta.setCodigo(500);
            respuesta.setMensaje(mensaje);

            enviarErrorJson(response, request, respuesta);

            return;
        }

        String traceIdHeader = request.getHeader(TRACE_ID_HEADER);

        String traceId;

        if (traceIdHeader == null || traceIdHeader.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "-" + TRACE_SUFFIX;
            log.warn("TraceId generado por el sistema: {}", traceId);
        } else {
            traceId = traceIdHeader;
            if (!canalesPermitidos.getPermitidos().stream()
                    .anyMatch(externo -> traceId.contains(externo))) {
                log.warn("TraceId con sufijo sospechoso: {}", traceId);
            }
        }

        ThreadContext.put("myUuid", traceId);

        HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if (TRACE_ID_HEADER.equalsIgnoreCase(name)) return traceId;
                return super.getHeader(name);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                if (TRACE_ID_HEADER.equalsIgnoreCase(name))
                    return Collections.enumeration(Collections.singletonList(traceId));
                return super.getHeaders(name);
            }
        };

        filterChain.doFilter(wrappedRequest, response);
    }


    private void enviarErrorJson(HttpServletResponse response, HttpServletRequest request, RespuestaDTO respuesta) throws IOException {
        log.error("Error Endpoint deshabilitado: {}", respuesta.getMensaje());

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // Usando Jackson para serializar objeto a JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(respuesta);

        response.getWriter().write(json);
    }
}
