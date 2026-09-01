package com.mx.asp.pago.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.springframework.stereotype.Component;

import com.blu.seguridadKaiosama.Algoritmo;
import com.blu.seguridadKaiosama.Criptography;
import com.blu.seguridadKaiosama.PKITools;
import com.blu.seguridadKaiosama.simetricKey.PublicKeyComponent;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Component
public class MetodosEncriptacion {

	private static PKITools pkiTools = new PKITools();
	private static PublicKeyComponent publicKeyComponent = new PublicKeyComponent();

	public byte[] firmaBuffer(final PrivateKey privateKey, final byte[] MSG)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		log.info("-----Inicia Entrando a firmaBuffer ---");
		Security.addProvider((Provider) new BouncyCastleProvider());
		byte[] signature = null;
		final Criptography cripto = new Criptography(Algoritmo.SHA256);
		try {
			signature = cripto.generateSignature(MSG, privateKey);
			return signature;
		} catch (InvalidKeyException e) {
			throw e;
		} catch (SignatureException e2) {
			throw e2;
		} catch (NoSuchAlgorithmException e3) {
			throw e3;
		}
	}

	public String firmar(final String json, final PrivateKey pkey)
			throws UnsupportedEncodingException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		log.info(".....Inicia metodo que genera firma..... [{}]", json);
		String result = new String();
		final byte[] bFirma = firmaBuffer(pkey, json.getBytes("UTF-8"));
		result = java.util.Base64.getEncoder().encodeToString(bFirma);
		log.info("...Firma generada.... [{}]", json);
		return result;
	}

	public String leerPasswordArchivo(File nombre) {
		log.info(".....Metodo de lectura de password..... [{}]", nombre);
		File archivo = nombre;
		String linea = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(archivo));
			linea = reader.readLine();
			reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		log.info("...Password.... [{}]", linea);
		return linea;
	}

	public Map<String, String> getKey(String jsonInput, File certificado) {
		log.info("---Datos de entreda--- [{}] , [{}]  ", jsonInput, certificado);
		Map<String, String> map = new HashMap<>();
		String keyAesCifrada = null;
		String peticionCifrada = null;

		try {
			byte[] jsonOriginalBytes = jsonInput.getBytes(StandardCharsets.UTF_8);
			Key keyAes = pkiTools.generateKey(128);
			peticionCifrada = pkiTools.aesEncrypt(jsonOriginalBytes, keyAes);

			PublicKey publicKey = publicKeyComponent.getLlavePublica(certificado.getPath());
			keyAesCifrada = pkiTools.rsaEncrypt(keyAes.getEncoded(), publicKey);

			map.put("KEY_CIFRADA", keyAesCifrada);
			map.put("PETICION_CIFRADA", peticionCifrada);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;
	}

	public String getFirma(String jsonInput, File llave, File archivoFrase) {
		PrivateKey pKey = null;
		String firma = null;
		String frase = leerPasswordArchivo(archivoFrase);
		log.info("Frase: " + frase);
		try {

			FileReader fileReader = null;
			try {

				fileReader = new FileReader(llave);
			} catch (IOException ex) {

				log.error("Ha ocurrido un error inesperado. Exception {} {}", ex.getMessage() + " " + ex,
						ex.getStackTrace());
			}
			PEMParser keyReader = new PEMParser(fileReader);

			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			InputDecryptorProvider decryptionProv = new JceOpenSSLPKCS8DecryptorProviderBuilder()
					.setProvider(new BouncyCastleProvider()).build(frase.toCharArray());
			Object keyPair = keyReader.readObject();
			PrivateKeyInfo keyInfo;

			if (keyPair instanceof PKCS8EncryptedPrivateKeyInfo) {
				keyInfo = ((PKCS8EncryptedPrivateKeyInfo) keyPair).decryptPrivateKeyInfo(decryptionProv);
				pKey = converter.getPrivateKey(keyInfo);
			}
			keyReader.close();
			if (pKey != null) {
				log.info("Obtuve la pKey");
				firma = firmar(jsonInput, pKey);
			} else {
				log.info("NO Obtuve la pKey");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		log.info("Firma generada: [{}] ", firma);
		return firma;
	}

}
