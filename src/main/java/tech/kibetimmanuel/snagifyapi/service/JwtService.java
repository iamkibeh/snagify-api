package tech.kibetimmanuel.snagifyapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;
import tech.kibetimmanuel.snagifyapi.entity.Token;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.enums.TokenType;
import tech.kibetimmanuel.snagifyapi.repository.TokenRepository;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Getter
    @Value("${spring.security.jwt.access-token.expiration-time}")
    private long accessTokenExpiration;
    @Getter
    @Value("${spring.security.jwt.refresh-token.expiration-time}")
    private long refreshTokenExpiration;

    @Value("${spring.security.jwt.refresh-token-cookie}")
    private String jwtRefreshCookie;

    private final TokenRepository tokenRepo;


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.
                parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    private Key getSignInKey() {
        byte[] decodeKeyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(decodeKeyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.
                builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValidToken = tokenRepo.findByToken(token)
                .map(t-> !t.isExpired() && !t.isRevoked()).orElse(false);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token)) && isValidToken;
    }

    public ResponseCookie generateJwtRefreshTokenCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken, "/api/v1/auth/refresh-token");
    }

    public String getJwtRefreshTokenFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    public ResponseCookie getCleanJwtRefreshTokenCookie() {
        return ResponseCookie.from(jwtRefreshCookie).path("/api/v1/auth/refresh-token").build();
    }

    private ResponseCookie generateCookie(String cookieName, String cookieValue, String path) {
        return ResponseCookie.from(cookieName, cookieValue).path(path).httpOnly(true).maxAge(refreshTokenExpiration/1000).build();
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }
}