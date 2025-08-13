package com.team.updevic001.utility;


import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.repositories.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Configuration
@PropertySource("classpath:application.yml")
@RequiredArgsConstructor
public class JwtUtil {

    private final UserRepository userRepository;

    @Value("${application.security.jwt.key}")
    private String secret_key;
    @Value("${application.security.jwt.expiration}")
    private long accessTokenValidity;
    private static Key key;

    public Key initializeKey() {
        byte[] keyBytes;
        keyBytes = Decoders.BASE64.decode(secret_key);
        key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }

    public String createToken(User user) {
        key = initializeKey();
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(()-> new UsernameNotFoundException("USER_NOT_FOUND"));

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("authorities", roles);
        claimsMap.put("user_id", user.getId());

        Date tokenCreateTime = new Date();

        Date tokenValidity = new Date(tokenCreateTime.getTime() + accessTokenValidity);

        final JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(tokenValidity)
                .addClaims(claimsMap)
                .signWith(key, SignatureAlgorithm.HS512);
        return jwtBuilder.compact();
    }

    public String resolveToken(HttpServletRequest request) {

        String TOKEN_HEADER = "Authorization";
        String bearerToken = request.getHeader(TOKEN_HEADER);
        String TOKEN_PREFIX = "Bearer ";
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean isTokenValid(String token, User user) {
        final String userEmail = extractEmail(token);
        return userEmail.equals(user.getEmail()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(initializeKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Return claims even for expired tokens
            return e.getClaims();
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
}
