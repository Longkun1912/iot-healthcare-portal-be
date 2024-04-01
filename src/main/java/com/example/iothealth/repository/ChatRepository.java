package com.example.iothealth.repository;

import com.example.iothealth.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    @Query("SELECT c FROM Chat c WHERE c.member1.id = :userId OR c.member2.id = :userId")
    List<Chat> findAllChatsByUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM Chat c WHERE (c.member1.id = :userId OR c.member2.id = :userId) ORDER BY c.updated_at DESC")
    List<Chat> findLatestChatByUserId(@Param("userId") UUID userId);
}
