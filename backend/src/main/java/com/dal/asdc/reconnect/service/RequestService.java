package com.dal.asdc.reconnect.service;

import com.dal.asdc.reconnect.dto.Request.Requests;
import com.dal.asdc.reconnect.enums.RequestStatus;
import com.dal.asdc.reconnect.model.ReferralRequests;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.RequestRepository;
import com.dal.asdc.reconnect.repository.UserDetailsRepository;
import com.dal.asdc.reconnect.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;

    private final UserDetailsRepository userDetailsRepository;

    private final UsersRepository usersRepository;

    /**
     * Get all pending requests for a referent
     *
     * @param Sender
     * @return List of Requests
     */
    public List<Requests> getPendingRequestForReferent(String Sender) {
        Optional<Users> users = usersRepository.findByUserEmail(Sender);
        if (!users.isPresent()) {
            log.error("User not found with email");
        }
        int userID = users.get().getUserID();
        List<Integer> pendingRequestsID = requestRepository.findReferrerIdsByReferentIdAndStatusPending(userID);
        return userDetailsRepository.findRequestsByReferrerIds(pendingRequestsID);

    }

    /**
     * Get all accepted requests for a referent
     *
     * @param userId
     * @return List of ReferralRequests
     */
    public List<ReferralRequests> getAcceptedRequestForReferent(int userId) {
        return requestRepository.findByReferent_UserIDAndStatus(userId, RequestStatus.ACCEPTED);
    }

    /**
     * Get all pending requests for a referrer
     *
     * @param Sender
     * @return List of Requests
     */
    public List<Requests> getPendingRequestForReferrer(String Sender) {
        Optional<Users> users = usersRepository.findByUserEmail(Sender);
        int userID = users.get().getUserID();

        List<Integer> pendingRequestsID = requestRepository.findReferentIdsByReferrerIdAndStatusPending(userID);

        return userDetailsRepository.findRequestsByReferrerIds(pendingRequestsID);
    }

    /**
     * Accepts a request.
     * <p>
     * This method updates the status of a request to ACCEPTED and sets the response date
     * to the current date and time. It operates within a transaction to ensure data consistency.
     *
     * @param Sender     The email address of the user who sent the request.
     * @param referentID The ID of the referent (the user accepting the request).
     * @throws RuntimeException if the database update operation fails.
     */
    @Transactional
    public void acceptRequest(String Sender, int referentID) {
        Optional<Users> users = usersRepository.findByUserEmail(Sender);
        int refereeID = users.get().getUserID();
        requestRepository.updateStatusAndResponseDate(RequestStatus.ACCEPTED, LocalDateTime.now(), referentID, refereeID);
    }

    /**
     * Rejects a request.
     * <p>
     * This method updates the status of a request to REJECTED and sets the response date
     * to the current date and time. It operates within a transaction to ensure data consistency.
     *
     * @param Sender     The email address of the user who sent the request.
     * @param referentID The ID of the referent (the user rejecting the request).
     * @throws RuntimeException if the database update operation fails.
     */
    @Transactional
    public void requestRejected(String Sender, int referentID) {
        Optional<Users> users = usersRepository.findByUserEmail(Sender);
        int refereeID = users.get().getUserID();
        requestRepository.updateStatusAndResponseDate(RequestStatus.REJECTED, LocalDateTime.now(), referentID, refereeID);
    }

    /**
     * Sends a request.
     * <p>
     * This method creates a new request with the specified referent and referrer,
     * sets the status to PENDING, and sets the request date to the current date and time.
     * It operates within a transaction to ensure data consistency.
     *
     * @param referentId The ID of the referent (the user to whom the request is sent).
     * @param referrerId The ID of the referrer (the user sending the request).
     * @return true if the request was successfully sent, false if a request already exists between the referent and referrer.
     * @throws IllegalArgumentException if the referent or referrer ID is invalid.
     */
    @Transactional
    public boolean sendRequest(Integer referentId, Integer referrerId) {
        Users referent = usersRepository.findById(referentId).orElseThrow(() -> new IllegalArgumentException("Invalid referent ID"));
        Users referrer = usersRepository.findById(referrerId).orElseThrow(() -> new IllegalArgumentException("Invalid referrer ID"));

        Optional<ReferralRequests> existingRequest = requestRepository.findByReferrerAndReferent(referrer, referent);
        if (existingRequest.isPresent()) {
            return false;
        }
        ReferralRequests referralRequest = new ReferralRequests();
        referralRequest.setReferent(referent);
        referralRequest.setReferrer(referrer);
        referralRequest.setStatus(RequestStatus.PENDING);
        referralRequest.setRequestDate(LocalDateTime.now());
        ReferralRequests savedRequest = requestRepository.save(referralRequest);
        return savedRequest != null;
    }

    /**
     * Get all accepted requests for a referrer
     *
     * @param userID
     * @return List of ReferralRequests
     */
    public List<ReferralRequests> getAcceptedRequestForReferrer(int userID) {
        return requestRepository.findByReferrer_UserIDAndStatus(userID, RequestStatus.ACCEPTED);
    }
}
