package com.ftn.iss.eventPlanner.util;

import com.ftn.iss.eventPlanner.model.Account;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import java.util.Date;

@Component
public class TokenUtils {

    @Value("event-planner")
    private String APP_NAME;

    @Value("somesecret")
    public String SECRET;

    @Value("14400000")
    private int EXPIRES_IN;

    @Value("Authorization")
    private String AUTH_HEADER;

    private static final String AUDIENCE_WEB = "web";

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String generateToken(Account account) {
        return Jwts.builder()
                .setIssuer(APP_NAME)
                .setSubject(account.getUsername())
                .setAudience(generateAudience())
                .setIssuedAt(new Date())
                .claim("role", account.getRole().toString())
                .claim("account_id", account.getId())
                .claim("user_id", account.getUser()==null?0:account.getUser().getId())
                .setExpiration(generateExpirationDate())
                .signWith(SIGNATURE_ALGORITHM, SECRET).compact();
    }

    private String generateAudience() {
        return AUDIENCE_WEB;
    }

    private Date generateExpirationDate() {
        return new Date(new Date().getTime() + EXPIRES_IN);
    }


    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); //Getting only token, value after "Bearer " prefix
        }

        return null;
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            username = null;
        }

        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    public String getAudienceFromToken(String token) {
        String audience;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            audience = claims.getAudience();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            audience = null;
        }
        return audience;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            expiration = null;
        }

        return expiration;
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            throw ex;
        } catch (Exception e) {
            claims = null;
        }

        return claims;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        Account account = (Account) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);

        return (username != null
                && username.equals(userDetails.getUsername())
                && !isCreatedBeforeLastPasswordReset(created, account.getLastPasswordResetDate()));
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    public int getExpiresIn() {
        return EXPIRES_IN;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

}
