package webtech.online.course.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webtech.online.course.domains.VideoDeletedEvent;
import webtech.online.course.dtos.Youtube.VideoUploadRequest;
import webtech.online.course.dtos.Youtube.VideoUploadResponse;
import webtech.online.course.dtos.video.VideoDTO;
import webtech.online.course.models.Segment;
import webtech.online.course.models.Video;
import webtech.online.course.repositories.VideoRepository;
import webtech.online.course.services.SegmentService;
import webtech.online.course.services.VideoService;
import webtech.online.course.services.YoutubeService;
import webtech.online.course.utils.Common;
import webtech.online.course.utils.DatetimeUtils;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final YoutubeService youtubeService;
    private final SegmentService segmentService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Video uploadVideo(VideoDTO videoDTO) throws IOException {
        VideoUploadResponse vResp = youtubeService.uploadVideo(VideoUploadRequest.builder()
                .tags("learn,zerotohero")
                .videoFile(videoDTO.video())
                .title(videoDTO.title())
                .duration(videoDTO.duration())
                .segmentDTOs(videoDTO.segmentDTOs())
                .build());

        Video v = Video.builder()
                .duration(Math.toIntExact(vResp.duration()))
                .videoUrl(vResp.videoUrl())
                .title(vResp.title())
                .build();
        videoDTO.segmentDTOs().forEach(sDTO -> {
            Segment s = segmentService.parserDTO(sDTO);
            v.addSegment(s);
        });
        return v;
    }

    @Override
    @Transactional
    public Video update(Long id, VideoDTO videoDTO) throws IOException {
        Video video = findById(id);
        video.setTitle(videoDTO.title());

        if (videoDTO.video() != null && !videoDTO.video().isEmpty()) {
            VideoUploadResponse vResp = youtubeService.uploadVideo(VideoUploadRequest.builder()
                    .tags("learn,zerotohero")
                    .videoFile(videoDTO.video())
                    .title(videoDTO.title())
                    .segmentDTOs(videoDTO.segmentDTOs())
                            .duration(videoDTO.duration())
                    .build());
            video.setVideoUrl(vResp.videoUrl());
            video.setDuration(Math.toIntExact(vResp.duration()));
        }

        if (videoDTO.segmentDTOs() != null) {
            videoDTO.segmentDTOs()
                    .forEach(seg->{
                       Segment s= Segment.builder()
                               .endAt(DatetimeUtils.convertSecond2ISO(seg.endAtSeconds()))
                               .description(seg.description())
                               .startAt(DatetimeUtils.convertSecond2ISO(seg.startAtSeconds())).build();
                       video.addSegment(s);
                    });
        }

        return videoRepository.saveAndFlush(video);
    }

    @Override
    @Transactional
    public void deleteAllByIds(List<Long> ids) {

        List<Video> videos = videoRepository.findAllById(ids);

        if (videos.isEmpty()) {
            return;
        }

        List<String> ytIds = videos.stream()
                .map(v -> Common.extractVideoId(v.getVideoUrl()))
                .toList();

        videoRepository.deleteAll(videos);

        eventPublisher.publishEvent(new VideoDeletedEvent(ytIds));
    }
    @Override
    @Transactional
    public void deleteAllByVideoIds(List<String> ids) {
        if(ids.isEmpty()) return;
        eventPublisher.publishEvent(new VideoDeletedEvent(ids));
    }
    @Override
    public java.util.List<Video> findAll() {
        return videoRepository.findAll();
    }

    @Override
    public Video findById(Long id) {
        return videoRepository.findById(id).orElseThrow(
                () -> new webtech.online.course.exceptions.BaseError(404, "Video not found with id=" + id));
    }
}
