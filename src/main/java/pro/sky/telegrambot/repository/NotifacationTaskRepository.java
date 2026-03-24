package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotifacationTaskRepository
        extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> findByStatusAndSendTimeBefore(NotificationStatus status, LocalDateTime time);

    List<NotificationTask> findByStatus(NotificationStatus status);

    List<NotificationTask> findByChatId(Long chatId);
}
