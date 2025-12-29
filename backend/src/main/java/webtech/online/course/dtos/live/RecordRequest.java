package webtech.online.course.dtos.live;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordRequest {
    private String roomName;
    private String egressId;
}
