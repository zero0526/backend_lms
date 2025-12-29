package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webtech.online.course.dtos.video.SegmentDTO;
import webtech.online.course.models.Segment;
import webtech.online.course.repositories.SegmentRepository;
import webtech.online.course.services.SegmentService;
import webtech.online.course.utils.Common;

@Service
@RequiredArgsConstructor
public class SegmentServiceImpl implements SegmentService {
    private final SegmentRepository segmentRepository;
    private final webtech.online.course.repositories.VideoRepository videoRepository;

    public Segment parserDTO(SegmentDTO segmentDTO) {
        return Segment.builder()
                .endAt(Common.parserTime(segmentDTO.endAtSeconds()))
                .startAt(Common.parserTime(segmentDTO.startAtSeconds()))
                .description(segmentDTO.description())
                .build();
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Segment save(Long videoId, SegmentDTO segmentDTO) {
        webtech.online.course.models.Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new webtech.online.course.exceptions.BaseError(404,
                        "Video not found with id=" + videoId));

        Segment segment = parserDTO(segmentDTO);
        segment.setVideo(video);
        return segmentRepository.saveAndFlush(segment);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Segment update(Long id, SegmentDTO segmentDTO) {
        Segment segment = findById(id);
        segment.setStartAt(Common.parserTime(segmentDTO.startAtSeconds()));
        segment.setEndAt(Common.parserTime(segmentDTO.endAtSeconds()));
        segment.setDescription(segmentDTO.description());
        return segmentRepository.saveAndFlush(segment);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void delete(Long id) {
        Segment segment = findById(id);
        segmentRepository.delete(segment);
    }

    @Override
    public java.util.List<Segment> findAll() {
        return segmentRepository.findAll();
    }

    @Override
    public Segment findById(Long id) {
        return segmentRepository.findById(id).orElseThrow(
                () -> new webtech.online.course.exceptions.BaseError(404, "Segment not found with id=" + id));
    }
}
