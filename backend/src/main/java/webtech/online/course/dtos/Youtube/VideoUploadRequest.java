package webtech.online.course.dtos.Youtube;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.dtos.video.SegmentDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Setter
@Builder
public class VideoUploadRequest {

    @Getter
    private String title;
    @Getter
    private String description;
    @Getter
    @Builder.Default
    private String categoryId = "22";
    @Getter
    @Builder.Default
    private String privacyStatus = "unlisted";
    private String tags;
    @Getter
    private MultipartFile videoFile;
    @Getter
    private List<SegmentDTO> segmentDTOs;
    @Getter
    private Long duration;
    public List<String> getTags() {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

}