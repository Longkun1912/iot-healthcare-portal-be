package com.example.iothealth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organisations")
@Getter
@Setter
public class Organisation {
    @Id
    private UUID id;

    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String address;
    @Column
    private String contact_number;
    @Column
    private LocalDateTime last_updated;

    @OneToMany(mappedBy = "organisation", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private List<User> users;

    @PreRemove
    private void removeUsersFromOrganisation() {
        for (User user : users) {
            user.setOrganisation(null);
        }
    }
}
