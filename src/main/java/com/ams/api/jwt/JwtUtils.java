package com.ams.api.jwt;

import com.ams.api.config.AmsAuthJwtConfig;
import com.ams.api.service.impl.CustomerUserDetails;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    private AmsAuthJwtConfig amsAuthJwtConfig;

    public String generateJwtToken(CustomerUserDetails userPrincipal) {
        return generateToken(userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getEmail());
    }

    public String generateToken(Long userId, String username, String email) {
        UserClaim userClaim = new UserClaim(userId, username, email);
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + amsAuthJwtConfig.getJwtConfig().getExpirationMs()))
                .signWith(SignatureAlgorithm.HS512, amsAuthJwtConfig.getJwtConfig().getSecret()).claim("user-details", userClaim).compact();
    }


    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(amsAuthJwtConfig.getJwtConfig().getSecret()).parseClaimsJws(token).getBody()
                .getSubject();
    }

    public String getEmailFromJwtToken(String token) {
        UserClaim userClaim = getUserClaimFromJwtToken(token);
        return userClaim.getEmail();
    }

    public UserClaim getUserClaimFromJwtToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(amsAuthJwtConfig.getJwtConfig().getSecret()).parseClaimsJws(token).getBody();
        Map<String, String> userDetailsMap = claims.get("user-details", Map.class);
        return new UserClaim(userDetailsMap);
    }


    public Date getTokenExpiryFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(amsAuthJwtConfig.getJwtConfig().getSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration();
    }


    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(amsAuthJwtConfig.getJwtConfig().getSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
