package mx.net.asp.asp_pago_management.utilerias;

import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class Token {
    private static final int TIME_STEP_SECONDS = 30;
    private static final int NUM_DIGITS_OUTPUT = 6;
    private static final String HMAC_ALGORITHM = "HmacSHA1";
    
    private final ThreadLocal<Mac> macThreadLocal = ThreadLocal.withInitial(() -> {
        try {
            return Mac.getInstance(HMAC_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("HMAC algorithm not available: " + HMAC_ALGORITHM, e);
        }
    });

    public String generateCurrentNumber(String secret) throws GeneralSecurityException {
        return generateCurrentNumber(secret, Instant.now().toEpochMilli());
    }

    public String generateCurrentNumber(String secret, long currentTimeMillis) throws GeneralSecurityException {
        byte[] key = decodeBase32(secret);
        byte[] data = new byte[8];
        long timeCounter = currentTimeMillis / (TIME_STEP_SECONDS * 1000L);

        for (int i = 7; timeCounter > 0; i--) {
            data[i] = (byte) (timeCounter & 0xFF);
            timeCounter >>= 8;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, HMAC_ALGORITHM);
        Mac mac = macThreadLocal.get();
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[hash.length - 1] & 0xF;
        int truncatedHash = ((hash[offset] & 0x7F) << 24) |
                            ((hash[offset + 1] & 0xFF) << 16) |
                            ((hash[offset + 2] & 0xFF) << 8) |
                            (hash[offset + 3] & 0xFF);

        int otp = truncatedHash % (int) Math.pow(10, NUM_DIGITS_OUTPUT);
        return zeroPrepend(otp, NUM_DIGITS_OUTPUT);
    }

    private byte[] decodeBase32(String str) {
        int numBytes = (str.length() * 5 + 4) / 8;
        byte[] result = new byte[numBytes];
        int resultIndex = 0;
        int working = 0;
        int which = 0;

        for (char ch : str.toUpperCase().toCharArray()) {
            int val = switch (ch) {
            case 'A' -> 0; case 'B' -> 1; case 'C' -> 2; case 'D' -> 3; case 'E' -> 4;
            case 'F' -> 5; case 'G' -> 6; case 'H' -> 7; case 'I' -> 8; case 'J' -> 9;
            case 'K' -> 10; case 'L' -> 11; case 'M' -> 12; case 'N' -> 13; case 'O' -> 14;
            case 'P' -> 15; case 'Q' -> 16; case 'R' -> 17; case 'S' -> 18; case 'T' -> 19;
            case 'U' -> 20; case 'V' -> 21; case 'W' -> 22; case 'X' -> 23; case 'Y' -> 24;
            case 'Z' -> 25; case '0' -> 26; case '1' -> 27; case '2' -> 28; case '3' -> 29;
            case '4' -> 30; case '5' -> 31; case '6' -> 32; case '7' -> 33; case '8' -> 34; case '9' -> 35;
            case '=' -> -1; // No se hace nada con '='
            default -> throw new IllegalArgumentException("Invalid base-32 character: " + ch);
        };

            switch (which) {
                case 0 -> { working = (val & 0x1F) << 3; which = 1; }
                case 1 -> { result[resultIndex++] = (byte) (working | (val >> 2)); working = (val & 0x3) << 6; which = 2; }
                case 2 -> { working |= (val & 0x1F) << 1; which = 3; }
                case 3 -> { result[resultIndex++] = (byte) (working | (val >> 4)); working = (val & 0xF) << 4; which = 4; }
                case 4 -> { result[resultIndex++] = (byte) (working | (val >> 1)); working = (val & 0x1) << 7; which = 5; }
                case 5 -> { working |= (val & 0x1F) << 2; which = 6; }
                case 6 -> { result[resultIndex++] = (byte) (working | (val >> 3)); working = (val & 0x7) << 5; which = 7; }
                case 7 -> { result[resultIndex++] = (byte) (working | val); which = 0; }
                default -> throw new IllegalStateException("Unexpected value: " + which);
            }
        }

        if (which != 0) result[resultIndex++] = (byte) working;
        return Arrays.copyOf(result, resultIndex);
    }

    private String zeroPrepend(int num, int digits) {
        return String.format("%0" + digits + "d", num);
    }
}