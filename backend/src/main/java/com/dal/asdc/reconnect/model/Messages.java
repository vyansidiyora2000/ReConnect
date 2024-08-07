package com.dal.asdc.reconnect.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "Messages")
@Data
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MessageID")
    private int messageId;

    @Column(name = "MessageContent", nullable = false)
    private String messageContent;

    @ManyToOne
    @JoinColumn(name = "SenderID")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "ReceiverID")
    private Users receiver;

    @Column(name = "Time", nullable = false, updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date time;

    @Column(name = "IsRead", nullable = false)
    private boolean isRead = false;

}
