package webtech.online.course.services;

import webtech.online.course.dtos.video.SegmentDTO;
import webtech.online.course.models.Segment;

public interface SegmentService {
    public Segment parserDTO(SegmentDTO segmentDTO);

    public Segment save(Long videoId, SegmentDTO segmentDTO);

    public Segment update(Long id, SegmentDTO segmentDTO);

    public void delete(Long id);

    public java.util.List<Segment> findAll();

    public Segment findById(Long id);
}
