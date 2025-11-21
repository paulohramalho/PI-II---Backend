package com.lumenlabs.energymanagement.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lumenlabs.energymanagement.dto.security.LoginDTO;
import com.lumenlabs.energymanagement.model.User;
import com.lumenlabs.energymanagement.repository.UserRepository;

@Service
public class JWTService {

	@Autowired
	private UserRepository userRepository;

	@Value("${jwt.issuer}")
	private String ISSUER;
	@Value("${jwt.secret}")
	private String KEY;

	public String getToken(LoginDTO loginDto) {
		User user = userRepository.findByEmailIgnoreCase(loginDto.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));;
		String token = generateJwt(user);
		return token;
	}

	public String generateJwt(User user) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(KEY);
			return JWT.create().withIssuer(ISSUER).withSubject(user.getUsername())
					.withClaim("role", user.getRole().toString()).withExpiresAt(generateExpirationDate())
					.sign(algorithm);
		} catch (JWTCreationException e) {
			throw new RuntimeException("Error trying to create token: " + e.getMessage(), e);
		}
	}

	private Instant generateExpirationDate() {
		return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("-03:00"));
	}

	public String validateToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(KEY);
			return JWT.require(algorithm).withIssuer(ISSUER).build().verify(token).getSubject();
		} catch (JWTVerificationException e) {
			return "";
		}
	}
}
