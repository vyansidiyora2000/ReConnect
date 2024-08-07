package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.model.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessagesRepository extends JpaRepository<Messages, Integer> {

    @Query("SELECT m FROM Messages m WHERE (m.sender.userEmail = :senderEmail AND m.receiver.userEmail = :receiverEmail) OR (m.sender.userEmail = :receiverEmail AND m.receiver.userEmail = :senderEmail) ORDER BY m.time")
    List<Messages> findChatHistory(@Param("senderEmail") String senderEmail, @Param("receiverEmail") String receiverEmail);

}
