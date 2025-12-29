package webtech.online.course.dtos.video;

public record SegmentDTO(
        Long startAtSeconds,
        Long endAtSeconds,
        String description
) {
}
