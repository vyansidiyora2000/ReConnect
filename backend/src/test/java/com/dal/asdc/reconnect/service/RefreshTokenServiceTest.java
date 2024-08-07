package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.model.RefreshToken;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.RefreshTokenRepository;
import com.dal.asdc.reconnect.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UsersRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRefreshToken() {
        // Setup
        String email = "test@example.com";
        Users mockUser = Users.builder()
                .userID(1)
                .userEmail(email)
                .build();

        RefreshToken existingToken = RefreshToken.builder()
                .users(mockUser)
                .token("existing-token") // Use a fixed token for simplicity
                .expiryDate(Instant.now().plusMillis(600000)) // Existing valid token
                .build();

        RefreshToken newToken = RefreshToken.builder()
                .users(mockUser)
                .token("new-token") // Use a fixed token for simplicity
                .expiryDate(Instant.now().plusMillis(600000)) // New token
                .build();

        // Mock the user repository
        when(userRepository.findByUserEmail(email)).thenReturn(Optional.of(mockUser));

        // Mock the existing token lookup
        when(refreshTokenRepository.findRefreshTokenByUserId(mockUser.getUserID())).thenReturn(Optional.of(existingToken));

        // Mock the save method
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setToken(newToken.getToken()); // Simulate saving with a fixed token
            return token;
        });

        // Mock the delete method to avoid actual deletions
        doNothing().when(refreshTokenRepository).delete(any(RefreshToken.class));

        // Call the service method
        RefreshToken result = refreshTokenService.createRefreshToken(email);

        // Verify the result
        assertNotNull(result);
        // Verify that the token returned matches the new token's token
        assertEquals(newToken.getToken(), result.getToken());

        // Capture arguments passed to save and delete
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        // Verify interactions
        verify(refreshTokenRepository).delete(tokenCaptor.capture()); // Verify delete was called
        RefreshToken capturedDeletedToken = tokenCaptor.getValue();
        assertEquals(existingToken.getToken(), capturedDeletedToken.getToken()); // Verify token was deleted

        verify(refreshTokenRepository).save(tokenCaptor.capture()); // Verify save was called
        RefreshToken capturedSavedToken = tokenCaptor.getValue();
        assertEquals(newToken.getToken(), capturedSavedToken.getToken()); // Verify token was saved
    }


    @Test
    void testFindByToken() {
        // Setup
        String token = UUID.randomUUID().toString();
        RefreshToken mockToken = RefreshToken.builder()
                .token(token)
                .expiryDate(Instant.now().plusMillis(600000))
                .build();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(mockToken));

        // Call the service method
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(mockToken, result.get());
    }

    @Test
    void testVerifyExpirationTokenExpired() {
        // Setup
        RefreshToken expiredToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().minusMillis(600000)) // Token is expired
                .build();

        // Mock the repository to throw an exception when delete is called
        doThrow(new EmptyResultDataAccessException(1)).when(refreshTokenRepository).delete(expiredToken);

        // Call the service method and verify exception
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        });

        // Verify interactions
        verify(refreshTokenRepository).delete(expiredToken); // Verify delete was called
    }

    @Test
    void testVerifyExpirationTokenValid() {
        // Setup
        RefreshToken validToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // Token is valid
                .build();

        // Call the service method
        RefreshToken result = refreshTokenService.verifyExpiration(validToken);

        // Verify
        assertNotNull(result);
        assertEquals(validToken, result);
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class)); // verify delete was not called
    }
}
