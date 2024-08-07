package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.userdetails.UserDetailsRequest;
import com.dal.asdc.reconnect.dto.userdetails.UserDetailsResponse;

public interface ProfileService {
    UserDetailsResponse getUserDetailsByUserID(int userID);

    UserDetailsResponse updateUserDetails(UserDetailsRequest request);

    void updateResumePath(int userId, String resumePath);

    void updateProfilePicturePath(int userId, String profilePicturePath);

    String getResumePath(int userId);

    String getProfilePicturePath(int userId);
}
