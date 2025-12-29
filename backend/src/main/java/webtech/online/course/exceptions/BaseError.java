package webtech.online.course.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class BaseError extends RuntimeException {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp;

    private final int status;
    private final String message;

    public BaseError(int status,String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }
    public BaseError(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.status = HttpStatus.BAD_REQUEST.value();
        this.message = message;
    }
}
