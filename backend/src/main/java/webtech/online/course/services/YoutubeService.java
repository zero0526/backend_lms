package webtech.online.course.services;

import webtech.online.course.dtos.Youtube.VideoUploadRequest;
import webtech.online.course.dtos.Youtube.VideoUploadResponse;
import com.google.api.services.youtube.model.Video;

import java.io.IOException;

public interface YoutubeService {
    public VideoUploadResponse uploadVideo(VideoUploadRequest request) throws IOException;
    public Video getVideoDetails(String videoId) throws IOException;
    public void deleteVideo(String videoId) throws IOException;
}
