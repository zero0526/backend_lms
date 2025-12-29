package webtech.online.course.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DriveFilesDeletedEvent {
    private final List<String> fileIds;
}
