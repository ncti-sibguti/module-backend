package ru.ncti.modulebackend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

    @Value("${jwtSecret}")
    private String secret;
    private final Long jwtExpirationInMs = 604800000L; // 7 days
    private final Long refreshExpirationInMs = 1209600000L; // 14 days

    public String generateToken(UserDetailsImpl userDetails) {
        Map<String, Object> claims = new HashMap<>() {{
            put("user_id", userDetails.getUser().getId());
            put("role", userDetails.getAuthorities());
        }};
        return Jwts.builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuer("ncti-backend")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public String generateRefreshToken(UserDetailsImpl userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
    }

    public boolean validateRefreshToken(String refreshToken, UserDetails userDetails) {
        final String username = getUsernameFromToken(refreshToken);
        return (username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(refreshToken));
    }

    private boolean isRefreshTokenExpired(String refreshToken) {
        final Date expiration = getExpirationDateFromToken(refreshToken);
        return expiration.before(new Date());
    }

}
