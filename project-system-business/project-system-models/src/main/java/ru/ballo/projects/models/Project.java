package ru.ballo.projects.models;

import ru.ballo.projects.models.statuses.ProjectStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Project {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStatus projectStatus;

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
    private Team team;
}
