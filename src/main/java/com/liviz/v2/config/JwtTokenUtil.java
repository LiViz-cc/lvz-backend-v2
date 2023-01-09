package com.liviz.v2.config;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.liviz.v2.controller.UserController;
import com.liviz.v2.exception.UnauthenticatedException;
import com.liviz.v2.model.User;
import com.liviz.v2.service.UserService;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import javafx.util.Pair;

//reference: https://www.javainuse.com/spring/boot-jwt
@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    UserService userService;

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        String token = Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes(StandardCharsets.UTF_8)).compact();

        return token;
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateToken(String token, String username) {
        final String usernameFromToken = getUsernameFromToken(token);
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }

    /**
     * @param authorizationHeader the JWT token to validate
     * @return username if token is valid, null otherwise
     */
    public String getJwtIdentity(String authorizationHeader) throws UnauthenticatedException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthenticatedException("Bearer token is missing or invalid");
        }
        String bearerToken = authorizationHeader.substring("Bearer ".length());

        String username = getUsernameFromToken(bearerToken);

        if (isTokenExpired(bearerToken)) {
            throw new UnauthenticatedException("Bearer token is invalid");
        }
        return username;
    }

    public com.liviz.v2.model.User getJwtUserFromToken(String authorizationHeader) throws UnauthenticatedException {
        // get jwt username
        String usernameFromToken = getJwtIdentity(authorizationHeader);

        // get user from database
        Optional<User> userOptional = userService.findByUsername(usernameFromToken);

        // return unauthenticated if jwt username is null
        if (userOptional.isEmpty()) {
            throw new UnauthenticatedException("User not found");
        }

        return userOptional.get();
    }

}

