package com.project.board.repository;

import com.project.board.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User readUserByUserId(String userId);

    @Query(value = "insert into token (user_idx, refresh_token, expire) values (:userIdx, :token, :expire)", nativeQuery = true)
    @Modifying
    void saveToken(@Param("userIdx") int userIdx, @Param("token") String token, @Param("expire") LocalDateTime expire);

    @Query(value = "select exists( select refresh_token from token where user_idx = :userIdx and refresh_token = :token)", nativeQuery = true)
    boolean existsToken(@Param("userIdx") String idx, @Param("token") String refreshToken);
}
