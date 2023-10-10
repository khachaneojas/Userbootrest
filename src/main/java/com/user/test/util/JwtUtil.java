package com.user.test.util;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.user.test.enums.Authority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	//requirement :
    public static final long JWT_TOKEN_VALIDITY = 86400000;

    //    public static final long JWT_TOKEN_VALIDITY =  60;
    @Value("${app.jwt.secret}")
    private String secret;
    
    private String getTokenfromHeaderString(String authorizatonString) {
    	if(StringUtils.hasText(authorizatonString) && authorizatonString.startsWith("Bearer "))
    		return authorizatonString.substring(7);
    	
    	return authorizatonString;
    }
    
    private Claims getClaims(String authToken) {
    	try {
    		return Jwts.parserBuilder()
    				.setSigningKey(getSignKey())
    				.build()
    				.parseClaimsJws(authToken)
    				.getBody();
    	}catch (Exception e) {
    		throw new JwtException(e.getMessage());
    	}
    }
    
    public Integer getUserIdfromJwt(String authorizationString) {
    	return Integer.parseInt
    			(getClaims(getTokenfromHeaderString(authorizationString))
    					.getSubject());
    	
//    	String str = getTokenfromHeaderString(authorizationString);
//    	Claims claim = getClaims(str);
//    	
//    	Integer id = Integer.parseInt(claim.getSubject());
//    	return id;
    	
    	}
    public String generateToken(int id) {

    	return Jwts.builder()
        		.setSubject(String.valueOf(id))
        		.setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getSignKey(),SignatureAlgorithm.HS512).compact();
    }

    private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
  
//  //retrieve username from jwt token
//  public String getUsernameFromToken(String token) {
//      return getClaimFromToken(token, Claims::getSubject);
//  }
    
//    //retrieve expiration date from jwt token
//    public Date getExpirationDateFromToken(String token) {
//        return getClaimFromToken(token, Claims::getExpiration);
//    }

//    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = getAllClaimsFromToken(token);
//        return claimsResolver.apply(claims);
//    }
//
//    //for retrieveing any information from token we will need the secret key
//    private Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
//    }

    //check if the token has expired
//    private Boolean isTokenExpired(String token) {
//        final Date expiration = getExpirationDateFromToken(token);
//        return expiration.before(new Date());
//    }

	//validate token
//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = getUsernameFromToken(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
}
