package com.example.iothealth.repository;

import com.example.iothealth.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("SELECT m FROM Message m WHERE m.id = :id")
    Optional<Message> findMessageById(Integer id);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sent_at ASC")
    List<Message> findAllByChatId(UUID chatId);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.sent_at DESC")
    List<Message> findLatestByChatId(UUID chatId);
}
