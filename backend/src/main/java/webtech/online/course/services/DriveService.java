package webtech.online.course.services;

import com.google.api.services.drive.model.FileList;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.services.drive.model.File;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;

import java.io.IOException;
import java.util.List;

public interface DriveService {
    public FileInfo uploadFile(DriveRequest driveRequest)throws IOException;
    public FileList listFiles(int pageSize)throws IOException;
    public void deleteFiles(List<String> fileIds);
}
