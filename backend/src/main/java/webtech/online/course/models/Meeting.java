package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import webtech.online.course.enums.Recurrence;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meetings")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originator_id")
    private User originator;


    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MeetingSession> meetingSessions= new ArrayList<>();

    private String title;

    @Builder.Default
    private Period recurrence= Period.ofDays(1);

    @Column(name = "record_option")
    private Boolean recordOption;

    @Column(name = "default_duration_minutes")
    @Builder.Default
    private Integer defaultDurationMinutes= 60;

    @Column(name = "join_url")
    private String joinUrl;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt= LocalDateTime.now();

    @Column(name = "last_updated")
    @Builder.Default
    private LocalDateTime updatedAt= LocalDateTime.now();
}

