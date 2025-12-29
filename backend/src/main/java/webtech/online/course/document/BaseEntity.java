package webtech.online.course.document;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseEntity {
    @Id
    private String id;
}
