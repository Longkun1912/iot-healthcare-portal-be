package com.example.iothealth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    private UUID id;

    @Column
    private String username;

    @Column
    private String avatar;

    @Column
    private String email;

    @Column
    private String mobile;

    @Column
    private String gender;

    @Column
    private String password;

    @Column
    private LocalDateTime last_updated;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HealthRecord> healthRecords;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_organisation")
    private Organisation organisation;

    @ManyToOne
    @JoinColumn(name = "user_health_objective")
    @JsonBackReference
    private HealthObjective health_objective;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Device> devices;

    // One user can have many chats with different users
    // One user can have only one chat to one specific user
    @OneToMany(mappedBy = "member1", cascade = CascadeType.ALL)
    private List<Chat> chat_member1;

    @OneToMany(mappedBy = "member2", cascade = CascadeType.ALL)
    private List<Chat> chats_member1;

    public User(UUID id, String username, String avatar, String email, LocalDateTime last_updated, Set<Role> roles, Organisation organisation) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.email = email;
        this.last_updated = last_updated;
        this.roles = roles;
        this.organisation = organisation;
    }
}
