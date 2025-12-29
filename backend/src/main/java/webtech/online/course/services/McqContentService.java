package webtech.online.course.services;

import webtech.online.course.dtos.course.McqContentDTO;
import webtech.online.course.models.MCPContent;

import java.io.IOException;

public interface McqContentService {
    public MCPContent findById(Long id);
    public MCPContent update(Long id, McqContentDTO mcqContentDTO) throws IOException;
    public void delete(Long id);

}
