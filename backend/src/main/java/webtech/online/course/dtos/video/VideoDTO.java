package webtech.online.course.dtos.video;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record VideoDTO (
        Long duration,
        String title,
        MultipartFile video,
        List<SegmentDTO> segmentDTOs
){

}
