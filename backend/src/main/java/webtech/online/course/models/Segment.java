package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "segments")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Segment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "start_at")
    private String startAt;
    @Column(name = "end_at")
    private String endAt;

    public List<Integer> getStartAtIso(){
        return Arrays.stream(this.startAt.split(":")).map(Integer::parseInt).toList();
    }
}