package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.model.RefreshToken;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.RefreshTokenRepository;
import com.dal.asdc.reconnect.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final UsersRepository userRepository;

    /**
     * Creates or updates a refresh token for a given user.
     * <p>
     * This method generates a new refresh token for the user identified by the provided email.
     * If a refresh token already exists for the user, it is deleted before creating a new one.
     *
     * @param email The email address of the user for whom to create the refresh token.
     * @return The newly created RefreshToken object.
     */
    public RefreshToken createRefreshToken(String email) {
        Optional<Users> user = userRepository.findByUserEmail(email);

        RefreshToken refreshToken = RefreshToken.builder()
                .users(user.get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(600000)) // set expiry of refresh token to 10 minutes - you can configure it application.properties file
                .build();
        Optional<RefreshToken> refreshToken1 = refreshTokenRepository.findRefreshTokenByUserId(refreshToken.getUsers().getUserID());

        refreshToken1.ifPresent(token -> refreshTokenRepository.delete(token));

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Retrieves a RefreshToken by its token value.
     *
     * @param token The string value of the refresh token to search for.
     * @return An Optional containing the RefreshToken if found, or an empty Optional if not found.
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies if a given RefreshToken has expired.
     * <p>
     * This method checks the expiration date of the provided RefreshToken against the current time.
     * If the token has expired, it is deleted from the repository and an exception is thrown.
     *
     * @param token The RefreshToken to verify.
     * @return The same RefreshToken if it hasn't expired.
     * @throws RuntimeException if the token has expired, with a message indicating that a new login is required.
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

}
