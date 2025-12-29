package webtech.online.course.services.impl;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.dtos.Youtube.VideoUploadRequest;
import webtech.online.course.dtos.Youtube.VideoUploadResponse;
import webtech.online.course.dtos.video.SegmentDTO;
import webtech.online.course.services.DriveService;
import webtech.online.course.services.YoutubeService;
import webtech.online.course.utils.Common;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeServiceImpl implements YoutubeService {

    private final YouTube youTube;
    private final DriveService driveService;

    public VideoUploadResponse uploadVideo(VideoUploadRequest request) throws IOException {
        log.info("Bắt đầu upload video: {}", request.getTitle());

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(request.getTitle());
        snippet.setDescription(request.getDescription());
        snippet.setCategoryId(request.getCategoryId());

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            snippet.setTags(request.getTags());
        }

        VideoStatus status = new VideoStatus();
        status.setPrivacyStatus(request.getPrivacyStatus());
        status.setSelfDeclaredMadeForKids(false);

        Video video = new Video();
        video.setSnippet(snippet);
        video.setStatus(status);

        MultipartFile videoFile = request.getVideoFile();
        InputStream inputStream = videoFile.getInputStream();
        InputStreamContent mediaContent = new InputStreamContent(
                videoFile.getContentType(),
                inputStream
        );
        mediaContent.setLength(videoFile.getSize());
        String description="";
        if (request.getSegmentDTOs() != null && !request.getSegmentDTOs().isEmpty()) {
            StringBuilder segmentDesc = new StringBuilder("\n\nSegments:\n");

            for (SegmentDTO seg : request.getSegmentDTOs()) {
                String start = Common.parserTime(seg.startAtSeconds());
                String desc  = seg.description();

                segmentDesc.append(String.format("%s %s\n", start, desc));
            }

            description += segmentDesc.toString();
        }
        snippet.setDescription(description);
       YouTube.Videos.Insert videoInsert = youTube.videos()
                .insert(List.of("snippet", "status"), video, mediaContent);
        Video uploadedVideo = videoInsert.execute();
        String videoId = uploadedVideo.getId();
        log.info("Upload video thành công! Video ID: {}", videoId);

        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

        return new VideoUploadResponse(
                videoId,
                videoUrl,
                request.getTitle(),
                "SUCCESS",
                "Video đã được upload thành công!",
                request.getDuration()
        );
    }

    private void uploadThumbnail(String videoId, MultipartFile thumbnailFile) {
        try {
            InputStream inputStream = thumbnailFile.getInputStream();
            InputStreamContent mediaContent = new InputStreamContent(
                    thumbnailFile.getContentType(),
                    inputStream
            );

            youTube.thumbnails()
                    .set(videoId, mediaContent)
                    .execute();

            log.info("Upload thumbnail thành công cho video: {}", videoId);
        } catch (IOException e) {
            log.error("Lỗi khi upload thumbnail: {}", e.getMessage());
        }
    }

    public Video getVideoDetails(String videoId) throws IOException {
        YouTube.Videos.List list = youTube.videos()
                .list(List.of("contentDetails"))
                .setId(List.of(videoId));

        VideoListResponse info = list.execute();
        Video videoInfo = info.getItems().getFirst();

        if (videoInfo == null || videoInfo.isEmpty()) {
            return null;
        }

        return videoInfo;
    }

    @Override
    public void deleteVideo(String videoId) throws IOException {
        youTube.videos().delete(videoId).execute();
    }
}