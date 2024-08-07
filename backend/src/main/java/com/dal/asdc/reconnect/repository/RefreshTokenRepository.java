package com.dal.asdc.reconnect.repository;

import com.dal.asdc.reconnect.model.RefreshToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT rt FROM RefreshToken rt WHERE rt.users.userID = :userId")
    Optional<RefreshToken> findRefreshTokenByUserId(@Param("userId") int userId);

}
