package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.*;
import webtech.online.course.enums.MeetingSessionStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "meeting_sessions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    @ToString.Exclude
    private Meeting meeting;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "actual_duration")
    private BigDecimal actualDuration;

    @Builder.Default
    private String status = "upcoming";

    @OneToMany(mappedBy = "session")
    @ToString.Exclude
    private List<MeetingParticipant> participants;

    @OneToMany(mappedBy = "session")
    @ToString.Exclude
    private List<MeetingMessage> messages;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "egress_id")
    private String egressId;

    @Column(name = "current_presenter_id")
    private String currentPresenterId;
}

