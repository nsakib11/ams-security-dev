package com.ams.api.repository;


import com.ams.api.entity.Token;
import com.ams.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByUser(User user);

    @Modifying
    int deleteByUser(User user);

    @Modifying
    @Query("update ams_token t set t.token = ?1 where t.user = ?2")
    void updateTokenByUser(String token, User user);

    Optional<Token> findByToken(String token);
}
