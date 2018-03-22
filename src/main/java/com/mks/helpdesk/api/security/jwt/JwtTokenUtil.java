/**
 * 
 */
package com.mks.helpdesk.api.security.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Markus Souza on 19/03/2018
 *
 */
@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = 1L;
	
	static final String CLAIM_KEY_USERNAME = "sub";
	static final String CLAIM_KEY_CREATED = "created";
	static final String CLAIM_KEY_EXPIRED = "exp";
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration}")
	private Long expiration;
	
	public String getUsernameFromToken(String token) {
		try {
			return this.getClaimsFromToken(token).getSubject();
		} catch (Exception e) {
			return null;
					
		}
	}
	
	public Date getExpirationDateFromToken(String token) {
		try {
			return this.getClaimsFromToken(token).getExpiration();
		} catch (Exception e) {
			return null;
		}
	}
	
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		
		claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
		claims.put(CLAIM_KEY_CREATED, new Date());
		
		return doGenerateToken(claims);
	}
	
	public Boolean canTokenBeRefreshed(String token) {
		return (!isTokenExpired(token));
	}
	
	public String refreshToken(String token) {
		try {
			final Claims claims = this.getClaimsFromToken(token);
			claims.put(CLAIM_KEY_CREATED, new Date());
			return this.doGenerateToken(claims);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
		JwtUser user = (JwtUser) userDetails;
		final String username = this.getUsernameFromToken(token);
		
		return (username.equals(user.getUsername()) && !isTokenExpired(token));
	}
	
	private String doGenerateToken(Map<String, Object> claims) {
		return Jwts.builder()
				   .setClaims(claims)
				   .setExpiration(new Date(((Date) claims.get(CLAIM_KEY_CREATED)).getTime() + expiration * 1000))
				   .signWith(SignatureAlgorithm.HS512, secret)
				   .compact();
	}
	
	private Claims getClaimsFromToken(String token) {
		try {
			return Jwts.parser()
					   .setSigningKey(secret)
					   .parseClaimsJws(token)
					   .getBody();
		} catch (Exception e) {
			return null;
		}
	}
	
	private Boolean isTokenExpired(String token) {
		return this.getExpirationDateFromToken(token).before(new Date());
	}

}
