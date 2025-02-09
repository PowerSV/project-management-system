package ru.ballo.projects.models;

import ru.ballo.projects.models.statuses.MemberRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "team_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "team_id"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private MemberRole role;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}


