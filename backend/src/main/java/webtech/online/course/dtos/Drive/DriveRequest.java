package webtech.online.course.dtos.Drive;

import org.springframework.web.multipart.MultipartFile;

public record DriveRequest(
        String fileName,
        MultipartFile multipartFile
) {
    public DriveRequest(MultipartFile multipartFile){
        this(multipartFile.getOriginalFilename(), multipartFile);
    }
}
