package mx.net.asp.asp_pago_documents.filters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import mx.net.asp.asp_pago_documents.config.SuffixesCanalesPermitidosProperties;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.config.TraceSuffixProperties;
import mx.net.asp.asp_pago_documents.dao.SeguridadDAO;
import mx.net.asp.asp_pago_documents.model.TraceIdSospechosoOBJ;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;

@Component
@Order(0) // Ejecutar primero
@Log4j2
@RequiredArgsConstructor
public class TraceIdFilter extends OncePerRequestFilter {

    private final SeguridadDAO seguridadDAO;
    private final ErrorHandler errorHandler;

    @Value("${secret.key.uuid}")
    private String SECRET_KEY_UUID;
    @Value("${trace.id.header}")
    private String TRACE_ID_HEADER;
    @Value("${trace.id.header.signature}")
    private String TRACE_SIGNATURE_HEADER;
    @Value("${trace.suffix}")
    private String TRACE_SUFFIX;

    private final TraceSuffixProperties listSuffixesUUID;
    private final SuffixesCanalesPermitidosProperties canalesPermitidos;

    private static final List<String> ENDPOINTS_EXCLUIDOS_FIRMA = Arrays.asList(
            "obtenerDatosInicialesV"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info(">>> Entrando a TraceIdFilter para: {}", request.getRequestURI());

        String traceIdHeader = request.getHeader(TRACE_ID_HEADER);
        String traceSignatureHeader = request.getHeader(TRACE_SIGNATURE_HEADER);

        String traceId;
        String traceSignature;

        if (traceIdHeader == null || traceIdHeader.isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "-" + TRACE_SUFFIX;
            traceSignature = firmar(traceId);
            log.warn("TraceId generado por el sistema: {}", traceId);
            registrarTraceIdSospechoso(traceId, request, "TraceId generado por el sistema", traceSignature);
        } else {
            traceId = traceIdHeader;
            traceSignature = (traceSignatureHeader != null) ? traceSignatureHeader : "";

            if (listSuffixesUUID.getSuffixes().values().stream().anyMatch(traceId::contains)) {
                log.warn("TraceId generado por el sistema: {}", traceId);
                registrarTraceIdSospechoso(traceId, request, "TraceId generado por el sistema", traceSignature);
            } else if (!canalesPermitidos.getPermitidos().stream()
                    .anyMatch(externo -> traceId.contains(externo))) {
                log.warn("TraceId con sufijo sospechoso: {}", traceId);
                registrarTraceIdSospechoso(traceId, request, "TraceId con sufijo sospechoso", traceSignature);
            }

            if (traceSignature.isEmpty()) {
                log.warn("Firma vacía para X-Trace-Id: {}", traceId);
                registrarTraceIdSospechoso(traceId, request, "Firma vacía", traceSignature);
            } else if (!estaEnEndpointsExcluidos(request)) {
                if (!traceSignature.equals(firmar(traceId))) {
                    log.warn("Firma inválida para X-Trace-Id: {}", traceId);
                    registrarTraceIdSospechoso(traceId, request, "Firma inválida", traceSignature);
                }
            }
        }

        ThreadContext.put("myUuid", traceId);
        ThreadContext.put("myUuidSigned", traceSignature);

        HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if (TRACE_ID_HEADER.equalsIgnoreCase(name)) return traceId;
                if (TRACE_SIGNATURE_HEADER.equalsIgnoreCase(name)) return traceSignature;
                return super.getHeader(name);
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                if (TRACE_ID_HEADER.equalsIgnoreCase(name))
                    return Collections.enumeration(Collections.singletonList(traceId));
                if (TRACE_SIGNATURE_HEADER.equalsIgnoreCase(name))
                    return Collections.enumeration(Collections.singletonList(traceSignature));
                return super.getHeaders(name);
            }
        };

        filterChain.doFilter(wrappedRequest, response);
    }

    private String firmar(String traceId) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY_UUID.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKeySpec);
            byte[] firma = hmac.doFinal(traceId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(firma);
        } catch (Exception e) {
            throw new RuntimeException("Error al firmar el TraceId", e);
        }
    }

    private void registrarTraceIdSospechoso(String traceId, HttpServletRequest request, String obs, String traceSignature) {
        try {
            TraceIdSospechosoOBJ entidad = new TraceIdSospechosoOBJ();
            entidad.setTraceId(traceId);
            entidad.setTraceIdSigned(traceSignature);
            entidad.setIpCliente(request.getRemoteAddr());
            entidad.setEndpoint(request.getRequestURI());
            entidad.setObservaciones(obs);
            seguridadDAO.registraUUIDSospechoso(entidad);
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
    }

    private boolean estaEnEndpointsExcluidos(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return ENDPOINTS_EXCLUIDOS_FIRMA.stream().anyMatch(uri::contains);
    }
}
