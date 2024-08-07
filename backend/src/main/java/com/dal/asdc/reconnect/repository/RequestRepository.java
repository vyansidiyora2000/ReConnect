package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.enums.RequestStatus;
import com.dal.asdc.reconnect.model.ReferralRequests;
import com.dal.asdc.reconnect.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ReferralRequests, Integer> {

    @Query("SELECT r.referrer.userID FROM ReferralRequests r WHERE r.referent.userID = :referentId AND r.status = 'PENDING'")
    List<Integer> findReferrerIdsByReferentIdAndStatusPending(@Param("referentId") int referentId);


    @Query("SELECT r.referrer.userID FROM ReferralRequests r WHERE r.referent.userID = :referentId AND r.status = 'ACCEPTED'")
    List<Integer> findReferrerIdsByReferentIdAndStatusAccepted(@Param("referentId") int referentId);

    @Query("SELECT r.referent.userID FROM ReferralRequests r WHERE r.referrer.userID = :referrerID AND r.status = 'PENDING'")
    List<Integer> findReferentIdsByReferrerIdAndStatusPending(@Param("referrerID") int referrerID);

    List<ReferralRequests> findByReferent_UserIDAndStatus(int referentId, RequestStatus status);

    List<ReferralRequests> findByReferrer_UserIDAndStatus(int referentId, RequestStatus status);

    @Modifying
    @Query("UPDATE ReferralRequests r SET r.status = :status, r.responseDate = :responseDate " +
            "WHERE r.referent.userID = :referentId AND r.referrer.userID = :referrerId")
    int updateStatusAndResponseDate(@Param("status") RequestStatus status,
                                    @Param("responseDate") LocalDateTime responseDate,
                                    @Param("referentId") int referentId,
                                    @Param("referrerId") int referrerId);

    Optional<ReferralRequests> findByReferrerAndReferent(Users referrer, Users referent);
}
