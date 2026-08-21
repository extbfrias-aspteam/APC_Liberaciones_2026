package mx.net.asp.asp_pago_management.utilerias;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Calendar;
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
		String keySource;
		try {
			keySource = sha512(s1);
			keySource = sha512(keySource + s2);
		} catch (Exception e) {
			throw new RuntimeException("Error al generar KeySource:: ", e);
		}
		return keySource;
	}
	public String generaKeySource(String s1) {
		String keySource;
		try {
			keySource = sha512(s1);
		} catch (Exception e) {
			throw new RuntimeException("Error al generar KeySource:: ", e);
		}
		return keySource;
	}
	public String getFolioMensajeCobro() {
		String folio;
		try {
			Calendar fecha = Calendar.getInstance();
			int ano = fecha.get(Calendar.YEAR);
			int soloAno = Integer.parseInt(String.valueOf(ano).substring(2));
			int mes = fecha.get(Calendar.MONTH) + 1;
			int dia = fecha.get(Calendar.DAY_OF_MONTH);
			int hora = fecha.get(Calendar.HOUR_OF_DAY);
			int minuto = fecha.get(Calendar.MINUTE);
			int segundo = fecha.get(Calendar.SECOND);
			int miliSegundo = fecha.get(Calendar.MILLISECOND);
			int miliSegundoMod = (miliSegundo % 128);

			String binAno = complementaBinario(decimalToBinario(soloAno), 7);
			String binMes = complementaBinario(decimalToBinario(mes), 4);
			String binDia = complementaBinario(decimalToBinario(dia), 5);
			String binHora = complementaBinario(decimalToBinario(hora), 5);
			String binMinuto = complementaBinario(decimalToBinario(minuto), 6);
			String binSegundo = complementaBinario(decimalToBinario(segundo), 6);
			String binMiliSegMod = complementaBinario(decimalToBinario(miliSegundoMod), 7);

			String cadena = binAno + binMes + binDia + binHora + binMinuto + binSegundo + binMiliSegMod;
			folio = binarioToHex(cadena);
		} catch (Exception e) {
			throw new RuntimeException("Error al generar Folio:: ", e);
		}
		return folio;
	}
	public String complementaBinario(String b, int r) {
		String binario = b;
		while (binario.length() < r) {
			binario = "0" + binario;
		}
		return binario;
	}
	private String decimalToBinario(int numero) {
		StringBuilder stringBuilder = new StringBuilder();
		int n = numero;
		String numerobinario = "";
		numerobinario = numerobinario + (n % 2);
		n = n / 2;

		while (n >= 2) {
			numerobinario = numerobinario + (n % 2);
			n = n / 2;
		}
		numerobinario = numerobinario + n;
		StringBuilder cadena = stringBuilder.append(numerobinario).reverse();
		return cadena.toString();
	}
	private String binarioToHex(String bin) {
		String result;
		StringBuilder resultado = new StringBuilder();
		try {

			for (int i = 0; i < bin.length() - 1; i += 4) {
				int numero = Integer.parseInt(bin.substring(i, i + 4), 2);
				String reprHex = Integer.toString(numero, 16);
				resultado.append(reprHex);
			}
			result = resultado.toString();
		} catch (Exception e) {
			throw new RuntimeException("Error al generar binarioToHex:: ", e);
		}
		return result;
	}
}
