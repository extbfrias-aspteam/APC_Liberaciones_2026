package mx.net.asp.asp_pago_jwt.utilerias;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

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
