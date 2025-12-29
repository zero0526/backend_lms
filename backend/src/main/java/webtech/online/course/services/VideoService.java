package webtech.online.course.services;

import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.dtos.video.VideoDTO;
import webtech.online.course.models.Video;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    public Video uploadVideo(VideoDTO videoDTO) throws IOException;

    public Video update(Long id, VideoDTO videoDTO) throws IOException;

    public void deleteAllByIds(List<Long> ids);

    public java.util.List<Video> findAll();

    public Video findById(Long id);
    public void deleteAllByVideoIds(List<String> ids);

}
