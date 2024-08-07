package com.dal.asdc.reconnect.decorator;

import com.dal.asdc.reconnect.dto.userdetails.UserDetailsRequest;
import com.dal.asdc.reconnect.dto.userdetails.UserDetailsResponse;
import com.dal.asdc.reconnect.service.ProfileService;
import com.dal.asdc.reconnect.service.ProfileServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingProfileServiceDecorator implements ProfileService {

    private final ProfileService delegate;

    public LoggingProfileServiceDecorator(ProfileService delegate) {
        this.delegate = delegate;
    }

    @Override
    public UserDetailsResponse getUserDetailsByUserID(int userID) {
        log.info("Fetching user details for user ID: {}", userID);
        UserDetailsResponse response = delegate.getUserDetailsByUserID(userID);
        log.info("Fetched user details: {}", response);
        return response;
    }

    @Override
    public UserDetailsResponse updateUserDetails(UserDetailsRequest request) {
        log.info("Updating user details for user ID: {}", request.getUserId());
        UserDetailsResponse response = delegate.updateUserDetails(request);
        log.info("Updated user details for user ID: {}", request.getUserId());
        return response;
    }

    @Override
    public void updateResumePath(int userId, String resumePath) {
        log.info("Updating resume path for user ID: {}", userId);
        delegate.updateResumePath(userId, resumePath);
        log.info("Updated resume path for user ID: {}", userId);
    }

    @Override
    public void updateProfilePicturePath(int userId, String profilePicturePath) {
        log.info("Updating profile picture path for user ID: {}", userId);
        delegate.updateProfilePicturePath(userId, profilePicturePath);
        log.info("Updated profile picture path for user ID: {}", userId);
    }

    @Override
    public String getResumePath(int userId) {
        log.info("Fetching resume path for user ID: {}", userId);
        String resumePath = delegate.getResumePath(userId);
        log.info("Fetched resume path for user ID: {}", userId);
        return resumePath;
    }

    @Override
    public String getProfilePicturePath(int userId) {
        log.info("Fetching profile picture path for user ID: {}", userId);
        String profilePicturePath = delegate.getProfilePicturePath(userId);
        log.info("Fetched profile picture path for user ID: {}", userId);
        return profilePicturePath;
    }
}
