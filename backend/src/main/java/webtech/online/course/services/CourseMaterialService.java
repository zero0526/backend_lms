package webtech.online.course.services;

import webtech.online.course.dtos.course.CourseMaterialDTO;
import webtech.online.course.models.CourseMaterial;

import java.io.IOException;

public interface CourseMaterialService {
    public CourseMaterial parser(CourseMaterialDTO courseMaterialDTO);
    public CourseMaterial update(CourseMaterialDTO courseMaterialDTO) throws IOException;
    public CourseMaterial findById(Long id);
    public Long save(CourseMaterial courseMaterial);
    public void deleteById(Long id);
}
