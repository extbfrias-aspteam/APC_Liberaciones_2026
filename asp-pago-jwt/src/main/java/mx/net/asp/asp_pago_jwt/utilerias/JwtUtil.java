package mx.net.asp.asp_pago_jwt.utilerias;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtUtil {

    @Value("${jwt.secret.key.cif}")
    private String jwtSecretKeyCif;
    @Value("${jwt.access.token.time}")
    private String jwtAcessTokenTimeMS;
    @Value("${jwt.rfresh.token.time}")
    private String jwtRefreshTokenTimeMS;

    public String generarAccessTokenAspPago(String telefono, String dispositivoId, Integer Idcanal) {
        long accessTokenMillis = Long.parseLong(jwtAcessTokenTimeMS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("telefono", telefono);
        claims.put("dispositivoId", dispositivoId);
        claims.put("idCanal", Idcanal);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(telefono)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenMillis))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKeyCif)
                .compact();
    }

    public String generarRefreshTokenAspPago(String telefono, String dispositivoId, Integer Idcanal) {
        long refreshTokenMillis = Long.parseLong(jwtRefreshTokenTimeMS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("telefono", telefono);
        claims.put("dispositivoId", dispositivoId);
        claims.put("idCanal", Idcanal);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(telefono)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenMillis))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKeyCif)
                .compact();
    }

    public String generarAccessTokenCanalExterno(String tenantId, Integer IdCanal) {
        long accessTokenMillis = Long.parseLong(jwtAcessTokenTimeMS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId);
        claims.put("idCanal", IdCanal);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(tenantId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenMillis))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKeyCif)
                .compact();
    }

    public String generarRefreshTokenCanalExterno(String tenantId, Integer IdCanal) {
        long refreshTokenMillis = Long.parseLong(jwtRefreshTokenTimeMS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId);
        claims.put("idCanal", IdCanal);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(tenantId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenMillis))
                .signWith(SignatureAlgorithm.HS256, jwtSecretKeyCif)
                .compact();
    }

    public boolean tokenEsValido(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecretKeyCif)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public long segundosRestantes(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecretKeyCif)
                    .parseClaimsJws(token)
                    .getBody();

            long tiempoRestante = claims.getExpiration().getTime() - System.currentTimeMillis();
            return tiempoRestante / 1000;
        } catch (Exception e) {
            return 0;
        }
    }

    public Claims obtenerClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKeyCif) // Usa tu clave configurada
                .setAllowedClockSkewSeconds(5) // tolerancia de 5 segundos
                .parseClaimsJws(token)
                .getBody();
    }

    public enum TokenInvalidReason {
        EXPIRED,
        SIGNATURE_INVALID,
        MALFORMED,
        UNSUPPORTED,
        ILLEGAL_ARGUMENT,
        UNKNOWN,
        VALID
    }

    public TokenInvalidReason getTokenInvalidationReason(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecretKeyCif)
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration().after(new Date())) {
                return TokenInvalidReason.VALID;
            } else {
                return TokenInvalidReason.EXPIRED;
            }
        } catch (ExpiredJwtException e) {
            return TokenInvalidReason.EXPIRED;
        } catch (SignatureException e) {
            return TokenInvalidReason.SIGNATURE_INVALID;
        } catch (MalformedJwtException e) {
            return TokenInvalidReason.MALFORMED;
        } catch (UnsupportedJwtException e) {
            return TokenInvalidReason.UNSUPPORTED;
        } catch (IllegalArgumentException e) {
            return TokenInvalidReason.ILLEGAL_ARGUMENT;
        } catch (Exception e) {
            log.warn("Excepción desconocida al validar JWT", e);
            return TokenInvalidReason.UNKNOWN;
        }
    }
}
