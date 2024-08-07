package com.dal.asdc.reconnect.dto.Chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatHistoryResponseBody {
    private boolean sender;
    private String message;
    private LocalDateTime timestamp;

}
