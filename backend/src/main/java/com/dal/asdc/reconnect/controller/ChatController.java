package com.dal.asdc.reconnect.controller;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.dal.asdc.reconnect.dto.Chat.Message;
import com.dal.asdc.reconnect.dto.Response;
import com.dal.asdc.reconnect.service.MessagesService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/api")
public class ChatController {

    public ConnectListener onUserConnectWithSocket = new ConnectListener() {
        @Override
        public void onConnect(SocketIOClient client) {
            log.info("Perform operation on user connect in controller");
        }
    };
    public DisconnectListener onUserDisconnectWithSocket = new DisconnectListener() {
        @Override
        public void onDisconnect(SocketIOClient client) {
            log.info("Perform operation on user disconnect in controller");
        }
    };
    /*
    Socket.IO
     */
    @Autowired
    MessagesService messagesService;
    @Autowired
    private SocketIOServer socketServer;
    public DataListener<Message> onSendMessage = new DataListener<Message>() {
        @Override
        public void onData(SocketIOClient client, Message message, AckRequest acknowledge) throws Exception {

            /**
             * Sending message to target user
             * Send the same payload to user
             */
            messagesService.saveMessage(message.getSenderEmail(), message.getReceiverEmail(), message.getMessage());
            log.info(message.getSenderName() + " user send message to user " + message.getReceiverName() + " and message is " + message.getMessage());
            socketServer.getBroadcastOperations().sendEvent(message.getReceiverEmail(), client, message);


            /**
             * After sending message to target user we can send acknowledge to sender
             */
            acknowledge.sendAckData("Message send to target user successfully");
        }
    };

    ChatController(SocketIOServer socketServer) {
        this.socketServer = socketServer;

        this.socketServer.addConnectListener(onUserConnectWithSocket);
        this.socketServer.addDisconnectListener(onUserDisconnectWithSocket);

        /**
         * Here we create only one event listener,
         * but we can create any number of listener
         * messageSendToUser is socket end point after socket connection user have to send message payload on messageSendToUser event
         */
        this.socketServer.addEventListener("messageSendToUser", Message.class, onSendMessage);

    }

    /**
     * Get chat history between two users
     *
     * @param receiverEmail
     * @return ResponseEntity with list of messages
     */
    @GetMapping("/getChatHistory")
    public ResponseEntity<?> getChatHistory(@RequestParam("email") String receiverEmail) {
        var senderEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Message> list = messagesService.getChatHistory(senderEmail, receiverEmail);
        if (list.size() > 0) {
            Response<List<Message>> response = new Response<>(HttpStatus.OK.value(), "Messages Fetched", list);
            return ResponseEntity.ok(response);
        } else {
            Response<?> response = new Response<>(HttpStatus.CONFLICT.value(), "Messages Not Found", new ArrayList<>());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
}
