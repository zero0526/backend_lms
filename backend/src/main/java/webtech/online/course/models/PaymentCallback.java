package webtech.online.course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "payment_callbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCallback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_request_id")
    private PaymentRequest paymentRequest;

    @Column(name = "raw_data", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> rawData;

    @Column(name = "result_code")
    private Integer resultCode;

    private String message;

    @Column(name = "callback_time")
    @Builder.Default
    private LocalDateTime callbackTime = LocalDateTime.now();

    private String signature;
}
