package mx.net.asp.asp_pago_documents.utilerias;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

@Component
public class TraceIdUtil {

	private static final String TRACE_ID_KEY = "myUuid";
	private static final String TRACE_SIGNATURE_KEY = "myUuidSigned";

	private TraceIdUtil() {
		// Evita instanciación
	}

	public static String getTraceId() {
		return ThreadContext.get(TRACE_ID_KEY);
	}

	public static String getTraceSignature() {
		return ThreadContext.get(TRACE_SIGNATURE_KEY);
	}
}
