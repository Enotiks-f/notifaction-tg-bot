package pro.sky.telegrambot.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import pro.sky.telegrambot.enums.NotificationStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_task")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "send_time",  nullable = false)
    private LocalDateTime sendTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAT;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public boolean canRetry() {
        return this.retryCount < 3;
    }



}
