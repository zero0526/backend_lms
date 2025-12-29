package webtech.online.course.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import webtech.online.course.domains.FileInfo;
import webtech.online.course.dtos.Drive.DriveRequest;
import webtech.online.course.dtos.course.CourseMaterialDTO;
import webtech.online.course.exceptions.BaseError;
import webtech.online.course.models.CourseMaterial;
import webtech.online.course.repositories.CourseMaterialRepository;
import webtech.online.course.services.CourseMaterialService;
import webtech.online.course.services.DriveService;
import webtech.online.course.utils.Common;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseMaterialServiceImpl implements CourseMaterialService {
    private final CourseMaterialRepository courseMaterialRepository;
    private final DriveService driveService;

    public CourseMaterial parser(CourseMaterialDTO courseMaterialDTO){
        try {
            if(courseMaterialDTO.doc()==null||courseMaterialDTO.doc().isEmpty())return null;
            FileInfo fileInfo = driveService.uploadFile(new DriveRequest(courseMaterialDTO.doc()));
            return CourseMaterial.builder()
                    .docUrl(fileInfo.urlUploaded())
                    .fileType(fileInfo.fileType())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CourseMaterial update(CourseMaterialDTO courseMaterialDTO) throws IOException {
        CourseMaterial courseMaterial= findById(courseMaterialDTO.id());
        if(courseMaterialDTO.title()!=null && !courseMaterialDTO.title().isEmpty()){
            courseMaterial.setFileType(courseMaterialDTO.title());
        }
        if(courseMaterialDTO.doc()!=null && !courseMaterialDTO.doc().isEmpty()){
            String oldUrl="";
            if(courseMaterial.getDocUrl()!=null && !courseMaterial.getDocUrl().isEmpty())oldUrl= Common.extractDriveFileId(courseMaterial.getDocUrl());
            FileInfo fileInfo = driveService.uploadFile(new DriveRequest(courseMaterialDTO.doc()));
            courseMaterial.setDocUrl(fileInfo.urlUploaded());
            courseMaterial.setFileType(fileInfo.fileType());
            if(!oldUrl.isEmpty())driveService.deleteFiles(List.of(oldUrl));
        }

        return null;
    }

    @Override
    public CourseMaterial findById(Long id) {
        return courseMaterialRepository.findById(id).orElseThrow(()-> new BaseError("Not found the courseMaterial has id=%d".formatted(id)));
    }

    @Override
    public Long save(CourseMaterial courseMaterial) {
        return courseMaterialRepository.saveAndFlush(courseMaterial).getId();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        courseMaterialRepository.deleteById(id);
    }
}
