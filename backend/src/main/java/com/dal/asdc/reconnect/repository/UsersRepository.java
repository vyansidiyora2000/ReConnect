package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.dto.Users.SearchResult;
import com.dal.asdc.reconnect.dto.Users.User;
import com.dal.asdc.reconnect.dto.Users.UserCompanySearch;
import com.dal.asdc.reconnect.model.Company;
import com.dal.asdc.reconnect.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByUserDetailsUserName(String username);

    Users findByResetToken(String resetToken);

    Optional<Users> findByUserEmail(String email);

    List<Users> findAllUsersByUserTypeTypeID(int typeId);


    @Query("SELECT new com.dal.asdc.reconnect.dto.Users.SearchResult( " +
            "u.userID, ud.userName,c.companyName, ud.experience, ud.profilePicture, rr.status ) " +
            "FROM Users u " +
            "JOIN u.userDetails ud " +
            "JOIN ud.company c " +
            "LEFT JOIN ReferralRequests rr ON u.userID = rr.referrer.userID AND rr.referent.userID = :referentID " +
            "WHERE u.userType.typeID = :userType " +
            "AND c.companyName LIKE CONCAT(:companyInitial, '%')")
    List<SearchResult> findUsersWithDetailsAndReferralStatusWithCompany(
            @Param("referentID") int referentID,
            @Param("userType") int userType,
            @Param("companyInitial") String companyInitial);


    @Query("SELECT new com.dal.asdc.reconnect.dto.Users.SearchResult( " +
            "u.userID, ud.userName,c.companyName, ud.experience, ud.profilePicture, rr.status ) " +
            "FROM Users u " +
            "JOIN u.userDetails ud " +
            "JOIN ud.company c " +
            "LEFT JOIN ReferralRequests rr ON u.userID = rr.referent.userID AND rr.referrer.userID = :referrerID " +
            "WHERE u.userType.typeID = :userType " +
            "AND c.companyName LIKE CONCAT(:companyInitial, '%')")
    List<SearchResult> findUsersWithDetailsAndReferentStatusWithCompany(
            @Param("referrerID") int referrerID,
            @Param("userType") int userType,
            @Param("companyInitial") String companyInitial);


    @Query("SELECT new com.dal.asdc.reconnect.dto.Users.SearchResult( " +
            "u.userID, ud.userName,ud.company.companyName, ud.experience, ud.profilePicture, rr.status ) " +
            "FROM Users u " +
            "JOIN u.userDetails ud " +
            "LEFT JOIN ReferralRequests rr ON u.userID = rr.referrer.userID AND rr.referent.userID = :referentID " +
            "WHERE u.userType.typeID = :userType " +
            "AND ud.userName LIKE CONCAT(:userNameInitial, '%')")
    List<SearchResult> findUsersWithDetailsAndReferralStatusWithUserName(
            @Param("referentID") int referentID,
            @Param("userType") int userType,
            @Param("userNameInitial") String userNameInitial);


    @Query("SELECT new com.dal.asdc.reconnect.dto.Users.SearchResult( " +
            "u.userID, ud.userName,ud.company.companyName, ud.experience, ud.profilePicture, rr.status ) " +
            "FROM Users u " +
            "JOIN u.userDetails ud " +
            "LEFT JOIN ReferralRequests rr ON u.userID = rr.referent.userID AND rr.referrer.userID = :referrerID " +
            "WHERE u.userType.typeID = :userType " +
            "AND ud.userName LIKE CONCAT(:userNameInitial, '%')")
    List<SearchResult> findUsersWithDetailsAndReferentStatusWithUserName(
            @Param("referrerID") int referrerID,
            @Param("userType") int userType,
            @Param("userNameInitial") String userNameInitial);


    Optional<Users> findByUserID(int userID);
}
