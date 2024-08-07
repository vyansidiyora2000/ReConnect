package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.exception.EmailSendingException;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {

    @Value("${reset.password.url}")
    private String resetPasswordUrl;

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final JavaMailSender mailSender;

    /**
     * Sends a password reset email to the user with the specified email address.
     *
     * @param email the email address of the user who requested a password reset.
     */
    public void sendResetEmail(String email) {
        Optional<Users> user = usersRepository.findByUserEmail(email);
        if (user.isEmpty()) {
            log.warn("User with email {} not found", email);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        String token = UUID.randomUUID().toString();
        user.get().setResetToken(token);
        usersRepository.save(user.get());

        String resetUrl = resetPasswordUrl + token;
        String subject = "Password Reset Request";
        String message = "To reset your password, click the link below:\n" + resetUrl;

        sendEmail(user.get().getUserEmail(), subject, message);
    }

    /**
     * Resets the password for the user with the specified reset token.
     *
     * @param token       the reset token that was sent to the user's email.
     * @param newPassword the new password to set for the user.
     * @return true if the password was successfully reset, false otherwise.
     */
    public boolean resetPassword(String token, String newPassword) {
        Users user = usersRepository.findByResetToken(token);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword)); // Ideally, hash the password
            user.setResetToken(null);
            usersRepository.save(user);
            log.info("Password reset successfully for user with reset token {}", token);
            return true;
        }
        return false;
    }

    /**
     * Sends an email with the specified subject and message to the specified email address.
     *
     * @param toEmail the email address to send the email to.
     * @param subject the subject of the email.
     * @param message the body of the email.
     */
    public void sendEmail(String toEmail, String subject, String message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(toEmail);
            email.setSubject(subject);
            email.setText(message);
            mailSender.send(email);
            log.info("Email sent successfully to {}", toEmail);
        } catch (MailException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new EmailSendingException("Failed to send password reset email", HttpStatus.EXPECTATION_FAILED);
        }
    }
}
