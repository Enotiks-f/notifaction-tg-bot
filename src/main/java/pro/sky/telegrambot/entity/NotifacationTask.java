package pro.sky.telegrambot.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class NotifacationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String message;
    private LocalDateTime date;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Integer retry_count;




}
