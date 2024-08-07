package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Chat.Message;
import com.dal.asdc.reconnect.model.Messages;
import com.dal.asdc.reconnect.model.UserDetails;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.MessagesRepository;
import com.dal.asdc.reconnect.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MessageServiceTest
{
    @Mock
    private MessagesRepository messagesRepository;

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private MessagesService messagesService;

    @BeforeEach
    public void MessagesServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveMessage() {
        // Arrange
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";
        String messageContent = "Hello";

        Users sender = new Users();
        sender.setUserEmail(senderEmail);

        Users receiver = new Users();
        receiver.setUserEmail(receiverEmail);

        when(usersRepository.findByUserEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(usersRepository.findByUserEmail(receiverEmail)).thenReturn(Optional.of(receiver));

        boolean result = messagesService.saveMessage(senderEmail, receiverEmail, messageContent);

        assertTrue(result);
    }

    @Test
    public void testGetChatHistory() {
        // Arrange
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";

        Users sender = new Users();
        UserDetails userDetails = new UserDetails();
        userDetails.setProfilePicture("profile.png");
        sender.setUserEmail(senderEmail);
        sender.setUserDetails(userDetails);

        Users receiver = new Users();
        receiver.setUserEmail(receiverEmail);
        receiver.setUserDetails(userDetails);


        Messages message1 = new Messages();
        message1.setSender(sender);
        message1.setReceiver(receiver);
        message1.setMessageContent("Hello");
        message1.setTime(new Date());
        message1.setRead(false);

        List<Messages> messages = new ArrayList<>();
        messages.add(message1);

        when(messagesRepository.findChatHistory(senderEmail, receiverEmail)).thenReturn(messages);

        // Act
        List<Message> chatHistory = messagesService.getChatHistory(senderEmail, receiverEmail);

        // Assert
        assertEquals(1, chatHistory.size());
        Message response = chatHistory.get(0);
        assertTrue(Objects.equals(response.getSenderName(), sender.getUserEmail()));
        assertEquals("Hello", response.getMessage());
        assertNotNull(response.getTimestamp());
    }
}
