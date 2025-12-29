package webtech.online.course.dtos.live;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JoinMeetingResponse {
    private String token;       // JWT Token LiveKit
    private String wsUrl;       // URL WebSocket Server
    private Long sessionId;     // ID phiên làm việc
    private String roomName;    // Tên phòng trong LiveKit
    private UserResponse user;  // Thông tin user (để FE lưu context)
}
