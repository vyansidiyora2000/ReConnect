package com.dal.asdc.reconnect.model;

import com.dal.asdc.reconnect.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ReferralRequests")
@Data
public class ReferralRequests {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RequestID")
    private int requestId;

    @ManyToOne
    @JoinColumn(name = "ReferentID", nullable = false)
    private Users referent;

    @ManyToOne
    @JoinColumn(name = "ReferrerID", nullable = false)
    private Users referrer;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 50, nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "RequestDate", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime requestDate;

    @Column(name = "ResponseDate")
    private LocalDateTime responseDate;
}
