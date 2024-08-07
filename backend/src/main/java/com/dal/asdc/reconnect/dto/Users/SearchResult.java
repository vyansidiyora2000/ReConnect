package com.dal.asdc.reconnect.dto.Users;

import com.dal.asdc.reconnect.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchResult {
    private int userId;
    private String userName;
    private String companyName;
    private Integer experience;
    private String profilePicture;
    private RequestStatus status;
}
