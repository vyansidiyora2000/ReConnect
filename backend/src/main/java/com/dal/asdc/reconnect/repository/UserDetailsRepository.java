package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.dto.Request.Requests;
import com.dal.asdc.reconnect.model.Company;
import com.dal.asdc.reconnect.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Integer> {
    @Query("SELECT userName FROM UserDetails WHERE company = :company")
    List<String> findUsernamesByCompany(Company company);

    @Query("SELECT new com.dal.asdc.reconnect.dto.Request.Requests(u.userEmail, ud.userName, ud.profilePicture, u.userID) " +
            "FROM Users u JOIN UserDetails ud ON u.userDetails.detailId = ud.detailId " +
            "WHERE u.userID IN :referrerIds")
    List<Requests> findRequestsByReferrerIds(@Param("referrerIds") List<Integer> referrerIds);

    UserDetails findByUserName(String userName);
}
