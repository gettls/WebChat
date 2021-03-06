package ex.websocket.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider {
	
	@Value("${spring.jwt.secret}")
	private String secretKey;
	
	private long tokenValidMillisecond = 1000L * 60 * 60; // 1 hour
	
	/*
	 * 이름으로 Jwt Token 생성
	 */
	public String generteToken(String name) {
		Date now = new Date();
		return Jwts.builder()
					.setId(name)
					.setIssuedAt(now)
					.setExpiration(new Date(now.getTime()+tokenValidMillisecond))
					.signWith(SignatureAlgorithm.HS256, secretKey)
					.compact();
	}
	
	/*
	 * Jwt Token 을 복호화해서 유저 이름을 얻는다.
	 */
	public String getUserNameFromJwt(String jwt) {
		return getClaims(jwt).getBody().getId();
	}
	
	/*
	 * Jwt Token 유효성 체크
	 */
	public boolean validateToken(String jwt) {
		return this.getClaims(jwt) != null;
	}
	
	private Jws<Claims> getClaims(String jwt){
		try{
			return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
		}catch (SignatureException ex) {
			log.error("Invalid JWT signature");
			throw ex;
		}catch (MalformedJwtException ex) {
			log.error("Invalid JWT token");
			throw ex;
		}catch (ExpiredJwtException ex) {
			log.error("Expired JWT token");
			throw ex;
		}catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token");
			throw ex;
		}catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty.");
			throw ex;
		}
	}
}
