package ru.ballo.projects.models;

import ru.ballo.projects.models.statuses.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Entity
public class Task {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(nullable = false)
    private Integer complexity;

    @Column(name = "creation_date", nullable = false)
    @CreationTimestamp
    private Date creationDate;

    @Column(name = "last_modified_date")
    @UpdateTimestamp
    private Date lastModified;

    @Column(name = "deadline", nullable = false)
    private Date deadline;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    private TeamMember assignee;

    @ManyToOne
    private TeamMember author;
}
