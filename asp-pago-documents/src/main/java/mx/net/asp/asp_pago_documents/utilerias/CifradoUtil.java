package mx.net.asp.asp_pago_documents.utilerias;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

@Component
public class CifradoUtil {

	public String sha512(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-512");
			byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
			return Hex.encodeHexString(hash); // Reemplazo con una línea
		} catch (Exception ex) {
			throw new RuntimeException("Error al calcular SHA-512", ex);
		}
	}

	public String decryptB64(String key, String iv, String encrypted) {
		try {
			byte[] keyBytes = Hex.decodeHex(key);
			byte[] ivBytes = Hex.decodeHex(iv);
			byte[] encryptedBytes = Base64.decodeBase64(encrypted);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));

			byte[] resultBytes = cipher.doFinal(encryptedBytes);
			return new String(resultBytes, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			throw new RuntimeException("Error al desencriptar información", ex);
		}
	}

	public String cryptB64(String key, String iv, String decrypted) {
		try {
			byte[] keyBytes = Hex.decodeHex(key);
			byte[] ivBytes = Hex.decodeHex(iv);
			byte[] decryptedBytes = decrypted.getBytes(StandardCharsets.UTF_8);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new IvParameterSpec(ivBytes));

			byte[] resultBytes = cipher.doFinal(decryptedBytes);
			return Base64.encodeBase64String(resultBytes);
		} catch (Exception ex) {
			throw new RuntimeException("Error al encriptar información", ex);
		}
	}

	public String encriptaInformacionB64(String keySource, String data) {
		Objects.requireNonNull(keySource, "La llave no puede ser nula");
		Objects.requireNonNull(data, "La información a encriptar no puede ser nula");

		String key = keySource.substring(0, 32);
		String iv = keySource.substring(32, 64);
		return cryptB64(key, iv, data);
	}

	public String desencriptaInformacionB64(String keySource, String data) {
		Objects.requireNonNull(keySource, "La llave no puede ser nula");
		Objects.requireNonNull(data, "La información a desencriptar no puede ser nula");

		String key = keySource.substring(0, 32);
		String iv = keySource.substring(32, 64);
		return decryptB64(key, iv, data);
	}
	public String generaKeySource(String s1, String s2) {
		String keySource = "";
		try {
			keySource = sha512(s1);
			keySource = sha512(keySource + s2);
		} catch (Exception e) {
			throw new RuntimeException("Error al generar KeySource:: ", e);
		}
		return keySource;
	}
}
