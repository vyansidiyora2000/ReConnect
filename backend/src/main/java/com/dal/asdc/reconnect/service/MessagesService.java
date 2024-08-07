package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Chat.Message;
import com.dal.asdc.reconnect.model.Messages;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.MessagesRepository;
import com.dal.asdc.reconnect.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagesService {
    private final MessagesRepository messagesRepository;

    private final UsersRepository usersRepository;

    /**
     * Saves a message to the database.
     *
     * @param senderEmail    The email address of the message sender
     * @param receiverEmail  The email address of the message receiver
     * @param messageContent The content of the message
     * @return true if the message was successfully saved, false otherwise
     */
    public boolean saveMessage(String senderEmail, String receiverEmail, String messageContent) {
        Optional<Users> sender = usersRepository.findByUserEmail(senderEmail);
        Optional<Users> receiver = usersRepository.findByUserEmail(receiverEmail);
        Messages message = new Messages();
        message.setSender(sender.get());
        message.setReceiver(receiver.get());
        message.setMessageContent(messageContent);
        message.setTime(new Date());
        message.setRead(false);
        messagesRepository.save(message);
        log.info("Message from {} to {} saved successfully", senderEmail, receiverEmail);
        return true;
    }

    /**
     * Retrieves the chat history between two users.
     *
     * @param senderEmail   The email address of the message sender
     * @param receiverEmail The email address of the message receiver
     * @return A list of messages between the two users
     */
    public List<Message> getChatHistory(String senderEmail, String receiverEmail) {
        List<Messages> messages = messagesRepository.findChatHistory(senderEmail, receiverEmail);

        List<Message> chatHistoryResponseBodyList = new ArrayList<>();

        for (Messages message : messages) {
            Message messagetItem = new Message();
            messagetItem.setMessage(message.getMessageContent());
            messagetItem.setSenderName(message.getSender().getUsername());
            messagetItem.setSenderProfilePicture(message.getSender().getUserDetails().getProfilePicture());
            messagetItem.setSenderId(message.getSender().getUserID());
            messagetItem.setReceiverName(message.getReceiver().getUsername());
            messagetItem.setReceiverProfilePicture(message.getReceiver().getUserDetails().getProfilePicture());
            messagetItem.setReceiverId(message.getReceiver().getUserID());
            messagetItem.setTimestamp(message.getTime());
            messagetItem.setRead(message.isRead());

            chatHistoryResponseBodyList.add(messagetItem);
        }
        log.info("Retrieved chat history between {} and {}", senderEmail, receiverEmail);
        return chatHistoryResponseBodyList;
    }
}
