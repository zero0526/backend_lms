package webtech.online.course.services.impl;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;
import webtech.online.course.services.DriveService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriveServiceImpl implements DriveService {

    private final Drive drive;
    @Value("${google.drive.folderId}")
    private String folderId;

    @Override
    public FileInfo uploadFile(DriveRequest driveRequest) throws IOException {
        String originalFileName= driveRequest.multipartFile().getOriginalFilename();
        MultipartFile multipartFile= driveRequest.multipartFile();
        log.info("Uploading file: {}", originalFileName);

        File fileMetadata = new File();
        fileMetadata.setName(driveRequest.fileName()!=null?driveRequest.fileName():originalFileName);
        if (folderId != null) {
            fileMetadata.setParents(Collections.singletonList(folderId));
        }

        InputStream inputStream = multipartFile.getInputStream();
        InputStreamContent mediaContent = new InputStreamContent(
                multipartFile.getContentType(),
                inputStream
        );

        File file = drive.files().create(fileMetadata, mediaContent)
                .setFields("id, name, webViewLink, mimeType")
                .execute();
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        drive.permissions().create(file.getId(), permission)
                .execute();
        log.info("File uploaded: {} (ID: {})", file.getName(), file.getId());
        return new FileInfo(file.getWebViewLink(), file.getMimeType());
    }

    public FileList listFiles(int pageSize) throws IOException {
        return drive.files().list()
                .setPageSize(pageSize)
                .setFields("files(id, name, webViewLink)")
                .execute();
    }

    public void deleteFiles(List<String> fileIds) {
        for (String fileId : fileIds) {
            deleteSingleFile(fileId);
        }
    }

    private void deleteSingleFile(String fileId) {
        try {
            drive.files().delete(fileId).execute();
            log.info("Deleted Google Drive file: {}", fileId);

        } catch (GoogleJsonResponseException e) {
            if (e.getStatusCode() == 404) {
                log.warn("File not found: {}", fileId);
            } else {
                log.error("Google API error deleting file: {}", fileId, e);
            }
        } catch (Exception ex) {
            log.error("Unexpected error deleting file: {}", fileId, ex);
        }
    }
}
