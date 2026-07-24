package mx.net.asp.asp_pago_management.utilerias;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


import lombok.extern.log4j.Log4j2;
@Component
@Log4j2
public class AESSFC {

	private final Environment env;
	private SecretKey key;
	private final String algorithm = "algorithm";
	private final String keysize = "keysize";

	// Constructor que recibe el Environment para asegurar que esté disponible
	@Autowired
	public AESSFC(Environment env) {
		this.env = env;
	}
	/**
	 * Crea la Llave para encriptar/desencriptar
	 *
	 * @param value Valor de clave personalizado (debe ser de 16 bytes o menos)
	 */
	public void addKey(String value) {
		byte[] keyBytes = value != null && !value.isBlank() && value.length() <= env.getProperty(keysize, Integer.class)
				? value.getBytes()
						: env.getProperty("default.key", String.class).getBytes();
		this.key = new SecretKeySpec(padKey(keyBytes), env.getProperty(algorithm, String.class));
	}

	private byte[] padKey(byte[] keyBytes) {
		byte[] paddedKey = new byte[env.getProperty(keysize, Integer.class)];
		System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(env.getProperty(keysize, Integer.class), keyBytes.length));
		return paddedKey;
	}

	/**
	 * Metodo para encriptar un texto
	 *
	 * @return String texto encriptado
	 */
	public String encriptar(String text) {
		if (text == null || text.isBlank()) return "";

		try {
			Cipher cipher = Cipher.getInstance(env.getProperty(algorithm, String.class));
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptedBytes = cipher.doFinal(text.getBytes());
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
				IllegalBlockSizeException | BadPaddingException ex) {
			log.error("Error encrypting text: {}", ex.getMessage(), ex);
		}
		return "";
	}

	/**
	 * Metodo para desencriptar un texto
	 * @return String texto desencriptado
	 */
	public String desencriptar(String encryptedText) {
		if (encryptedText == null || encryptedText.isBlank()) return "";

		try {
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
			Cipher cipher = Cipher.getInstance(env.getProperty(algorithm, String.class));
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			return new String(decryptedBytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
				IllegalBlockSizeException | BadPaddingException | IllegalArgumentException ex) {
			log.error("Error decrypting text: {}", ex.getMessage(), ex);
		}
		return "";
	}
}
