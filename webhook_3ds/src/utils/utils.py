import base64
from Crypto.Cipher import AES
from src.core.config import settings
from src.utils.logger import logger

GCM_NONCE_LENGTH = 12
GCM_TAG_LENGTH = 16

def decrypt_dock_payload(encrypted_data: str) -> str:
    try:
        logger.info(f"AES ENV: {settings.AES_KEY}")

        # Decodificar base64
        key = base64.b64decode(settings.AES_KEY.encode("utf-8"))
        message_bytes = base64.b64decode(encrypted_data.encode("utf-8"))
        
        # Extraer componentes
        nonce = message_bytes[:GCM_NONCE_LENGTH]
        ciphertext = message_bytes[GCM_NONCE_LENGTH:-GCM_TAG_LENGTH]
        tag = message_bytes[-GCM_TAG_LENGTH:]
        
        # Configurar cifrado

        cipher = AES.new(key, AES.MODE_GCM, nonce=nonce)
        
        # Desencriptar y verificar
        decrypted = cipher.decrypt_and_verify(ciphertext, tag)
        
        logger.info(f"Payload desencriptado: {decrypted.decode('utf-8')}")
        return decrypted.decode("utf-8")
    
    except (ValueError, IndexError) as e:
        logger.error(f"Error de desencriptación: {str(e)}")
        raise ValueError("Payload encriptado inválido")
    

if __name__ == "__main__":
    encrypted_sample = "JRDqPUIo5yyZSBPrf5xFWrXiJ1wtXUdKcL2iWeO6SUcT+3hzuEv1Uxcp2fzdBMfRcUWaq8kdd6+G5TdPTQ5FNqqxvi48RxrpWQQ4gibPG4FsT2n0HK/3R1G9j3Ybz80LCMYoqvp8NQkZGLpSKQ2t1lLma+VSSlMbUh8Mh/hB0O7aUyn8DSJCKJ54f8T0g79Uf+4P3NFJSHRv5sn9QbWLn93nVRiLR+eMd9+D9n2nMpvYjWB9AF/Wix1fTwkIQqlWDiHBHx+I2MH4Jjst681XhU2FbokiY6FRAAXVUQXGH7RdcOFd7ZbVS95IBalqyby76ljyHGSX3dsyTMqzmy1tn/thiLG23+G+Busd3fIIn7wbp0xfRRMeqK2jssnIpEKClABGq/jJ/vpqPGxQrb2UQzE0NJuFLhSQW9SNrjbcv3SuBmGqoQbKuUBkt1ZYP71T2CaR031XFEuc4rJCVxIOlMp9AkML/XgN5rjTD0gGJqWC8TTA7AZRCOBZsaULGLdDx8mE7rRPb0G5yPDXJb3LC+r9J/9Pgs+JTvv9DKbexZPlzPv4lN1v+XlsjsmiT2FjNv9xRwE6U62n1uR3YFAPtIqXqKUmInfPjz1pv6J+3IW3/G0emgWE2djvqXHlTdC9iRaNF1D53DEoAU2/921agR3pKnEZajMv7/ahEeuAOROA/eZu8IQVYUkLKv5FWt2Vot4yRxAs/n2KIXndFRf1nYXWIZPchgQzPBmjQus4a129aHwqRKKNnqPsqi9C2fxKqxhICvZpRcpZaxELxaAB9c7ghyhsFg1ero5XdrLJTCFOTtRPGUy6VGvUdZVWspB82MDhi8/3M0YTyRm/mGjBPRnMx4AyL4evXUo3iaSaZdX+Lzx1ZWTrzUTkKoYAXfP+lKVp3UvzSC5QtW96DherJNYlKjaWL5sIxf2udcUwLD7cafqjZltWw/Aowoc8Ohw6I/cYrckhkSbwBYmzviMCwPdpLyiVsHYJ/4UWwdWWGz/3Ucb62HRVifbn3MXUnhhuTwMhd3jsl+mf7BZ7pxwoMN186sxhR1NuXsOz7q9IYA48U32RbcHHCU9ypQoILLI04PZP0nnRqBUZC5tzDi0zNZVZO27KjDNgfNd4mMS9xf3JAjjFSMx6VxrjDD4xPm9hkJUdLQ89ZFw0debU8kRAdVDTnQQ4cevdWWqrSdCC2EZcj12NW+kDr5AxAwskyp4AKyt5Vw4zSAsheBtRi7RhXV2mS9jCLpfj7a70B7Z5u+bWDRdf1mIaH+b1SAKQAE6+S/gSMqkZDfF2+G9pOnkwnH0DuvR2PtDYPsqxwuuR9rGcegc8YKPOGdvwRUAN6KcH2BGyRnegIwB2jv2HvavVaG3Hno2cK8WPCI3VPVcFx/eHPm9XOfw7n9C28YlaHkKB/3Sdsa6uAYwR2z1Q9LaGB3jn6Ce65dk/qJECju3xjvUfbCBPa8Be1Nk7Z6Vb74MLPTmnYv7D9dbwCFeYqkBWvY24mCVc/47cjx4l0f8awA7caacubM+yFHsr6Rvp64FoPb+u+65i568NcjCXRtGKWxOlNOA7qQ1T40h7jt33wRVA1BJf7yEt/qtvJfwNs0JMaCuflrpZB6texu2kir6DOP8300Z6vuWF38wkm4I5mq8f8gvNPkewSIDZ2FGBq1jxUcdTcbWEDSVSJH9yQWtMhmc/YdXE4/1o54SwLTToZJmsf/CReOpDup5aYMwHNhA="
    try:
        decrypted_data = decrypt_dock_payload(encrypted_sample)
        print(f"Decrypted data: {decrypted_data}")
    except ValueError as e:
        print(f"Failed to decrypt: {e}")