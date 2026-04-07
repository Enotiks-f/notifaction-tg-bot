package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.enums.NotificationStatus;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotifacationTaskRepository
        extends JpaRepository<NotificationTask, Long> {
    List<NotificationTask> findByStatusAndSendTimeBefore(NotificationStatus status, LocalDateTime time);

    List<NotificationTask> findByStatusAndSendTime(NotificationStatus status, LocalDateTime sendTime);

    List<NotificationTask> findByStatusAndSendTimeBetween(NotificationStatus status,
                                                          LocalDateTime start,
                                                          LocalDateTime end);

    List<NotificationTask> findByChatId(Long chatId);

    List<NotificationTask> findByStatus(NotificationStatus status);

    @Modifying
    @Query("UPDATE NotificationTask t SET t.status = :status WHERE t.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") NotificationStatus status);

    // Увеличение счетчика попыток
    @Modifying
    @Query("UPDATE NotificationTask t SET t.retryCount = t.retryCount + 1 WHERE t.id = :id")
    void incrementRetryCount(@Param("id") Long id);

}
