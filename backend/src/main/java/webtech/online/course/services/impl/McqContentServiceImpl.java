package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;
import webtech.online.course.dtos.course.McqContentDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.MCPContent;
import webtech.online.course.repositories.McqContentRepository;
import webtech.online.course.services.DriveService;
import webtech.online.course.services.McqContentService;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class McqContentServiceImpl implements McqContentService {
    private final McqContentRepository mcqContentRepository;
    private final DriveService driveService;

    public MCPContent findById(Long id) {
        return mcqContentRepository.findById(id)
                .orElseThrow(() -> new BaseError(500, "Not Found answer has id=%d".formatted(id)));
    }

    @Transactional
    public MCPContent update(Long id, McqContentDTO mcqContentDTO) throws IOException {
        MCPContent mcpContent = findById(id);
        mcpContent.setChoiceText(mcqContentDTO.cText());
        mcpContent.setIsCorrect(mcqContentDTO.isCorrect());
        if (mcqContentDTO.cImage() != null && !mcqContentDTO.cImage().isEmpty()) {
            FileInfo fileInfo = driveService.uploadFile(new DriveRequest(mcqContentDTO.cImage()));
            mcpContent.setChoiceImage(fileInfo.urlUploaded());
        }
        return mcqContentRepository.saveAndFlush(mcpContent);
    }

    @Transactional
    public void delete(Long id) {
        MCPContent mcpContent = mcqContentRepository.findFullTree(id);
        List<String> ids= mcpContent.getFileId();
        mcqContentRepository.delete(mcpContent);
        driveService.deleteFiles(ids);
    }
}
