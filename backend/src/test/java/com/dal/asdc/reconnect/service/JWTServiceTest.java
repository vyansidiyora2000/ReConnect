package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.model.UserType;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.UserDetailsRepository;
import com.dal.asdc.reconnect.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JWTServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @InjectMocks
    private JWTService jwtService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set mock values for the properties
        jwtService.secretKey = "649d9d23fbb80c52a706fe67c673511974e57906363f4f0401721e99ec05c231";
        jwtService.jwtExpiration = 3600000L; // 1 hour in milliseconds
    }

    @Test
    public void testExtractUsername() {
        String token = createToken("testUser");
        String username = jwtService.extractUsername(token);
        assertEquals("testUser", username);
    }

    @Test
    public void testExtractClaim() {
        String token = createToken("testUser");
        String email = jwtService.extractClaim(token, claims -> claims.get("email", String.class));
        assertEquals("testUser@example.com", email);
    }

    @Test
    public void testGenerateToken() {
        // Mock UserDetails
        UserDetails userDetailsFromToken = mock(UserDetails.class);
        when(userDetailsFromToken.getUsername()).thenReturn("testUser");

        // Mock Users
        Users user = new Users();
        user.setUserEmail("testUser@example.com");
        user.setUserID(1);

        // Mock UserDetails
        com.dal.asdc.reconnect.model.UserDetails userDetails = new com.dal.asdc.reconnect.model.UserDetails();
        userDetails.setDetailId(1);

        // Mock UserType and set it to the user
        UserType userType = mock(UserType.class);
        when(userType.getTypeID()).thenReturn(1);
        user.setUserType(userType);
        user.setUserDetails(userDetails);

        // Mock com.dal.asdc.reconnect.model.UserDetails
        com.dal.asdc.reconnect.model.UserDetails userDetailsModel = mock(com.dal.asdc.reconnect.model.UserDetails.class);
        when(userDetailsModel.getProfilePicture()).thenReturn("profilePictureUrl");

        // Mock repositories
        when(usersRepository.findByUserEmail("testUser")).thenReturn(Optional.of(user));
        when(userDetailsRepository.findById(1)).thenReturn(Optional.of(userDetails));

        // Generate and return token
        String token = jwtService.generateToken(userDetailsFromToken);

        assertNotNull(token);
    }


    @Test
    public void testBuildToken() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("email", "testUser@example.com");
        extraClaims.put("userType", 1);
        extraClaims.put("userID", 1);
        extraClaims.put("userName", "testUser");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.buildToken(extraClaims, userDetails, 3600000L);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJhbGciOiJIUzI1NiJ9")); // JWT header base64url encoded part
    }

    @Test
    public void testIsTokenValid() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = createToken("testUser");

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    public void testIsTokenExpired() {
        String token = createToken("testUser");
        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    public void testExtractExpiration() {
        String token = createToken("testUser");
        Date expiration = jwtService.extractExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    public void testExtractAllClaims() {
        String token = createToken("testUser");
        Claims claims = jwtService.extractAllClaims(token);
        assertNotNull(claims);
        assertEquals("testUser", claims.getSubject());
    }

    @Test
    public void testGetSignInKey() {
        Key key = jwtService.getSignInKey();
        assertNotNull(key);
    }

    @Test
    public void testExtractUserType() {
        String token = createToken("testUser");
        int userType = jwtService.extractUserType(token);
        assertEquals(1, userType);
    }

    @Test
    public void testExtractEmail() {
        String token = createToken("testUser");
        String email = jwtService.extractEmail(token);
        assertEquals("testUser@example.com", email);
    }

    @Test
    public void testExtractID() {
        String token = createToken("testUser");
        int userID = jwtService.extractID(token);
        assertEquals(1, userID);
    }


    @Test
    public void testExtractProfilePicture() {
        // Arrange
        String token = createToken("testUser");

        // Act
        String extractedProfilePicture = jwtService.extractProfilePicture(token);

        // Assert
        assertNull(extractedProfilePicture);
    }

    private String createToken(String username) {
        // Mock UserDetails
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);


        // Mock Users
        Users user = new Users();
        user.setUserEmail(username + "@example.com");
        user.setUserID(1);

        // Mock User Details
        com.dal.asdc.reconnect.model.UserDetails userDetails1 = new com.dal.asdc.reconnect.model.UserDetails();
        userDetails1.setUserName(username);
        userDetails1.setDetailId(1);

        // Mock UserType and set it to the user
        UserType userType = mock(UserType.class);
        when(userType.getTypeID()).thenReturn(1);
        user.setUserType(userType);
        user.setUserDetails(userDetails1);

        // Mock UserDetailsRepository
        com.dal.asdc.reconnect.model.UserDetails userDetailsModel = mock(com.dal.asdc.reconnect.model.UserDetails.class);
        when(userDetailsModel.getProfilePicture()).thenReturn("profilePictureUrl");
        when(userDetailsModel.getDetailId()).thenReturn(1);

        // Mock repositories
        when(usersRepository.findByUserEmail(username)).thenReturn(Optional.of(user));
        when(userDetailsRepository.findById(userDetails1.getDetailId())).thenReturn(Optional.of(userDetails1));

        // Generate and return token
        return jwtService.generateToken(userDetails);
    }

    @Test
    public void testGetExpirationTime() {
        // Set a mock value for jwtExpiration
        long expectedExpirationTime = 3600000L; // 1 hour in milliseconds
        jwtService.jwtExpiration = expectedExpirationTime;

        // Call the method to be tested
        long actualExpirationTime = jwtService.getExpirationTime();

        // Verify if the returned value matches the expected value
        assertEquals(expectedExpirationTime, actualExpirationTime);
    }

}

