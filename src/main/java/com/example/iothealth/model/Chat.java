package com.example.iothealth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor
public class Chat {
    @Id
    private UUID id;

    // One user can have many chats with different users
    // One user can have only one chat to one specific user
    @OneToOne
    @JoinColumn(name = "member1", nullable = false)
    private User member1;

    @OneToOne
    @JoinColumn(name = "member2", nullable = false)
    private User member2;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    @Column
    private LocalDateTime created_at;

    @Column
    private LocalDateTime updated_at;
}
