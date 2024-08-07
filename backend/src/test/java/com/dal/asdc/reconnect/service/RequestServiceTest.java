package com.dal.asdc.reconnect.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.dal.asdc.reconnect.dto.Request.Requests;
import com.dal.asdc.reconnect.enums.RequestStatus;
import com.dal.asdc.reconnect.model.ReferralRequests;
import com.dal.asdc.reconnect.model.Users;
import com.dal.asdc.reconnect.repository.RequestRepository;
import com.dal.asdc.reconnect.repository.UserDetailsRepository;
import com.dal.asdc.reconnect.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @InjectMocks
    private RequestService requestService;

    private Users referent;
    private Users referrer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        referent = new Users();
        referent.setUserID(1);

        referrer = new Users();
        referrer.setUserID(2);
    }

    @Test
    public void testGetPendingRequestForReferent() {
        String sender = "test@example.com";
        Users user = new Users();
        user.setUserID(1);
        when(usersRepository.findByUserEmail(sender)).thenReturn(Optional.of(user));

        List<Integer> pendingRequestsID = Collections.singletonList(1);
        when(requestRepository.findReferrerIdsByReferentIdAndStatusPending(user.getUserID())).thenReturn(pendingRequestsID);

        Requests request = new Requests();
        when(userDetailsRepository.findRequestsByReferrerIds(pendingRequestsID)).thenReturn(Collections.singletonList(request));

        List<Requests> result = requestService.getPendingRequestForReferent(sender);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request, result.get(0));
    }

    @Test
    public void testGetPendingRequestForReferrer() {
        String sender = "test@example.com";
        Users user = new Users();
        user.setUserID(1);
        when(usersRepository.findByUserEmail(sender)).thenReturn(Optional.of(user));

        List<Integer> pendingRequestsID = Collections.singletonList(1);
        when(requestRepository.findReferentIdsByReferrerIdAndStatusPending(user.getUserID())).thenReturn(pendingRequestsID);

        Requests request = new Requests();
        when(userDetailsRepository.findRequestsByReferrerIds(pendingRequestsID)).thenReturn(Collections.singletonList(request));

        List<Requests> result = requestService.getPendingRequestForReferrer(sender);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(request, result.get(0));
    }

    @Test
    public void testAcceptRequest() {
        String sender = "test@example.com";
        Users user = new Users();
        user.setUserID(1);
        when(usersRepository.findByUserEmail(sender)).thenReturn(Optional.of(user));

        int referentID = 1;
        when(requestRepository.updateStatusAndResponseDate(RequestStatus.ACCEPTED, LocalDateTime.now(), referentID, user.getUserID())).thenReturn(1);

        requestService.acceptRequest(sender, referentID);
    }

    @Test
    public void testRequestRejected() {
        String sender = "test@example.com";
        Users user = new Users();
        user.setUserID(1);
        when(usersRepository.findByUserEmail(sender)).thenReturn(Optional.of(user));
        int referentID = 1;
        when(requestRepository.updateStatusAndResponseDate(RequestStatus.REJECTED, LocalDateTime.now(), referentID, user.getUserID())).thenReturn(1);

        requestService.requestRejected(sender, referentID);
    }

    @Test
    public void testGetAcceptedRequestForReferent() {
        int userId = 1;
        ReferralRequests request1 = new ReferralRequests();
        request1.setRequestId(1);
        ReferralRequests request2 = new ReferralRequests();
        request2.setRequestId(2);

        when(requestRepository.findByReferent_UserIDAndStatus(userId, RequestStatus.ACCEPTED))
                .thenReturn(List.of(request1, request2));

        List<ReferralRequests> result = requestService.getAcceptedRequestForReferent(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(request1.getRequestId(), result.get(0).getRequestId());
        assertEquals(request2.getRequestId(), result.get(1).getRequestId());
    }

    @Test
    public void testGetAcceptedRequestForReferent_NoRequests() {
        int userId = 1;

        when(requestRepository.findByReferent_UserIDAndStatus(userId, RequestStatus.ACCEPTED))
                .thenReturn(Collections.emptyList());

        List<ReferralRequests> result = requestService.getAcceptedRequestForReferent(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSendRequestInvalidReferentId() {
        when(usersRepository.findById(1)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            requestService.sendRequest(1, 2);
        });

        assertEquals("Invalid referent ID", thrown.getMessage());
        verify(usersRepository, times(1)).findById(1);
        verifyNoInteractions(requestRepository);
    }

    @Test
    public void testSendRequestInvalidReferrerId() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(referent));
        when(usersRepository.findById(2)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            requestService.sendRequest(1, 2);
        });

        assertEquals("Invalid referrer ID", thrown.getMessage());
        verify(usersRepository, times(1)).findById(1);
        verify(usersRepository, times(1)).findById(2);
        verifyNoInteractions(requestRepository);
    }

    @Test
    public void testSendRequestAlreadyExists() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(referent));
        when(usersRepository.findById(2)).thenReturn(Optional.of(referrer));
        ReferralRequests existingRequest = new ReferralRequests();
        when(requestRepository.findByReferrerAndReferent(referrer, referent)).thenReturn(Optional.of(existingRequest));

        boolean result = requestService.sendRequest(1, 2);

        assertFalse(result);
        verify(usersRepository, times(1)).findById(1);
        verify(usersRepository, times(1)).findById(2);
        verify(requestRepository, times(1)).findByReferrerAndReferent(referrer, referent);
        verify(requestRepository, never()).save(any(ReferralRequests.class));
    }

    @Test
    public void testSendRequestSuccess() {
        when(usersRepository.findById(1)).thenReturn(Optional.of(referent));
        when(usersRepository.findById(2)).thenReturn(Optional.of(referrer));
        when(requestRepository.findByReferrerAndReferent(referrer, referent)).thenReturn(Optional.empty());
        ReferralRequests referralRequest = new ReferralRequests();
        referralRequest.setReferent(referent);
        referralRequest.setReferrer(referrer);
        referralRequest.setStatus(RequestStatus.PENDING);
        referralRequest.setRequestDate(LocalDateTime.now());
        when(requestRepository.save(any(ReferralRequests.class))).thenReturn(referralRequest);

        boolean result = requestService.sendRequest(1, 2);

        assertTrue(result);
        verify(usersRepository, times(1)).findById(1);
        verify(usersRepository, times(1)).findById(2);
        verify(requestRepository, times(1)).findByReferrerAndReferent(referrer, referent);
        verify(requestRepository, times(1)).save(any(ReferralRequests.class));
    }


    @Test
    public void testGetAcceptedRequestForReferrer() {
        int userId = 1;
        ReferralRequests request1 = new ReferralRequests();
        request1.setRequestId(1);
        ReferralRequests request2 = new ReferralRequests();
        request2.setRequestId(2);

        when(requestRepository.findByReferrer_UserIDAndStatus(userId, RequestStatus.ACCEPTED))
                .thenReturn(List.of(request1, request2));

        List<ReferralRequests> result = requestService.getAcceptedRequestForReferrer(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(request1.getRequestId(), result.get(0).getRequestId());
        assertEquals(request2.getRequestId(), result.get(1).getRequestId());
    }

    @Test
    public void testGetAcceptedRequestForReferrer_NoRequests() {
        int userId = 1;

        when(requestRepository.findByReferrer_UserIDAndStatus(userId, RequestStatus.ACCEPTED))
                .thenReturn(Collections.emptyList());

        List<ReferralRequests> result = requestService.getAcceptedRequestForReferrer(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPendingRequestForReferent_UserFound() {
        // Arrange
        String senderEmail = "test@example.com";
        Users user = new Users();
        user.setUserID(1);

        Requests request1 = new Requests();
        Requests request2 = new Requests();

        when(usersRepository.findByUserEmail(senderEmail)).thenReturn(Optional.of(user));
        when(requestRepository.findReferrerIdsByReferentIdAndStatusPending(1)).thenReturn(Arrays.asList(101, 102));
        when(userDetailsRepository.findRequestsByReferrerIds(Arrays.asList(101, 102)))
                .thenReturn(Arrays.asList(request1, request2));

        // Act
        List<Requests> requests = requestService.getPendingRequestForReferent(senderEmail);

        // Assert
        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertTrue(requests.contains(request1));
        assertTrue(requests.contains(request2));
    }

}
