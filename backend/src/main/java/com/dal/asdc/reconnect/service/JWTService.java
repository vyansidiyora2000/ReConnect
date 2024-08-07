package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.UserDetailsRepository;
import com.dal.asdc.reconnect.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for handling JWT (JSON Web Token) operations.
 * This class provides methods to generate, validate, and extract information from JWT tokens.
 */
@Service
@RequiredArgsConstructor
public class JWTService {
    @Value("${security.jwt.secret-key}")
    public String secretKey;

    @Value("${security.jwt.expiration-time}")
    public long jwtExpiration;

    private final UsersRepository usersRepository;

    private final UserDetailsRepository userDetailsRepository;

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token using a provided claims resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver the function to resolve the claim
     * @param <T>            the type of the claim
     * @return the extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for the given UserDetails without any extra claims.
     *
     * @param userDetails the UserDetails for which the token is to be generated
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token for the given UserDetails with additional claims.
     *
     * @param extraClaims additional claims to be included in the token
     * @param userDetails the UserDetails for which the token is to be generated
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Optional<Users> user = usersRepository.findByUserEmail(userDetails.getUsername());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Users currentUser = user.get();
        int userType = currentUser.getUserType().getTypeID();

        com.dal.asdc.reconnect.model.UserDetails userDetailsEntity = userDetailsRepository.findById(currentUser.getUserDetails().getDetailId())
                .orElseThrow(() -> new RuntimeException("UserDetails not found"));

        extraClaims.put("email", user.get().getUserEmail());
        extraClaims.put("userType", userType);
        extraClaims.put("userID", user.get().getUserID());
        extraClaims.put("userName", userDetails.getUsername());
        extraClaims.put("profile", userDetailsEntity.getProfilePicture());
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * Retrieves the configured JWT expiration time.
     *
     * @return the JWT expiration time in milliseconds
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Builds the JWT token with the provided claims and expiration time.
     *
     * @param extraClaims additional claims to be included in the token
     * @param userDetails the UserDetails for which the token is to be generated
     * @param expiration  the expiration time for the token in milliseconds
     * @return the generated JWT token
     */
    public String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Checks if the provided JWT token is valid for the given UserDetails.
     *
     * @param token       the JWT token
     * @param userDetails the UserDetails to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the provided JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the given JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from the given JWT token.
     *
     * @param token the JWT token
     * @return the claims extracted from the token
     */
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key used to sign the JWT token.
     *
     * @return the signing key
     */
    public Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the user type from the given JWT token.
     *
     * @param token the JWT token
     * @return the user type extracted from the token
     */
    public int extractUserType(String token) {
        Claims claims = extractAllClaims(token);
        return (int) claims.get("userType");
    }

    /**
     * Extracts the email from the given JWT token.
     *
     * @param token the JWT token
     * @return the email extracted from the token
     */
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("email");
    }

    /**
     * Extracts the user ID from the given JWT token.
     *
     * @param token the JWT token
     * @return the user ID extracted from the token
     */
    public int extractID(String token) {
        Claims claims = extractAllClaims(token);
        return (int) claims.get("userID");
    }

    /**
     * Extracts the profile picture URL from the given JWT token.
     *
     * @param token the JWT token
     * @return the profile picture URL extracted from the token
     */
    public String extractProfilePicture(String token) {
        Claims claims = extractAllClaims(token);
        return (String) claims.get("profile");
    }
}
