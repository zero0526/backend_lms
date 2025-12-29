package webtech.online.course.exceptions;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DefaultResponse{
    private LocalDateTime timestamp;
    private int status;

    public DefaultResponse(int status) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }
}
