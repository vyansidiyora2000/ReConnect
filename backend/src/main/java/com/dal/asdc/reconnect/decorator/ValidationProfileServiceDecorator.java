package com.dal.asdc.reconnect.decorator;

import com.dal.asdc.reconnect.dto.userdetails.UserDetailsRequest;
import com.dal.asdc.reconnect.dto.userdetails.UserDetailsResponse;
import com.dal.asdc.reconnect.service.ProfileService;
import jakarta.transaction.Transactional;

public class ValidationProfileServiceDecorator implements ProfileService {

    private final ProfileService delegate;

    public ValidationProfileServiceDecorator(ProfileService delegate) {
        this.delegate = delegate;
    }

    @Override
    public UserDetailsResponse getUserDetailsByUserID(int userID) {
        if (userID <= 0) {
            throw new IllegalArgumentException("User ID must be positive.");
        }
        return delegate.getUserDetailsByUserID(userID);
    }

    @Override
    @Transactional
    public UserDetailsResponse updateUserDetails(UserDetailsRequest request) {
        return delegate.updateUserDetails(request);
    }

    @Override
    public void updateResumePath(int userId, String resumePath) {
        validateUserId(userId);
        validatePath(resumePath);
        delegate.updateResumePath(userId, resumePath);
    }

    @Override
    public void updateProfilePicturePath(int userId, String profilePicturePath) {
        validateUserId(userId);
        validatePath(profilePicturePath);
        delegate.updateProfilePicturePath(userId, profilePicturePath);
    }

    @Override
    public String getResumePath(int userId) {
        validateUserId(userId);
        return delegate.getResumePath(userId);
    }

    @Override
    public String getProfilePicturePath(int userId) {
        validateUserId(userId);
        return delegate.getProfilePicturePath(userId);
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }

    private void validatePath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path must not be null or empty");
        }
    }
}
