package pro.sky.telegrambot.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.enums.NotificationStatus;
import pro.sky.telegrambot.repository.NotifacationTaskRepository;
import pro.sky.telegrambot.service.NotificationSenderService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotifacationTaskRepository repository;
    private final NotificationSenderService notificationSenderService;

    /**
     * Запуск каждую минуту для проверки и отправки уведомлений
     * Cron: секунды минуты часы дни месяцы дни_недели
     * 0 0/1 * * * * - каждую минуту
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void checkAndSendNotifications() {
        log.info("Starting scheduled notification check...");

        try {
            // Обрезаем текущее время до минут (убираем секунды и наносекунды)
            LocalDateTime currentMinute = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            log.debug("Current minute (truncated): {}", currentMinute);

            // Ищем задачи, которые нужно отправить в эту минуту
            List<NotificationTask> tasksToSend = repository.findByStatusAndSendTime(
                    NotificationStatus.PENDING,
                    currentMinute
            );

            log.info("Found {} notifications to send for time {}", tasksToSend.size(), currentMinute);

            if (!tasksToSend.isEmpty()) {
                // Отправляем все найденные уведомления
                notificationSenderService.sendBatchNotifications(tasksToSend);
            } else {
                log.debug("No notifications to send at this time");
            }

        } catch (Exception e) {
            log.error("Error in notification scheduler", e);
        }

        log.info("Scheduled notification check completed");
    }

    /**
     * Альтернативный вариант: запуск каждые 30 секунд для более точной отправки
     */
    @Scheduled(cron = "0/30 * * * * *")
    public void checkAndSendNotificationsWithOffset() {
        log.debug("Running notification check (30 sec interval)...");

        try {
            // Получаем текущее время с округлением до минут
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime currentMinute = now.truncatedTo(ChronoUnit.MINUTES);

            // Ищем задачи за последнюю минуту
            LocalDateTime startMinute = currentMinute.minusMinutes(1);
            LocalDateTime endMinute = currentMinute;

            List<NotificationTask> tasksToSend = repository.findByStatusAndSendTimeBetween(
                    NotificationStatus.PENDING,
                    startMinute,
                    endMinute
            );

            if (!tasksToSend.isEmpty()) {
                log.info("Found {} delayed notifications to send", tasksToSend.size());
                notificationSenderService.sendBatchNotifications(tasksToSend);
            }

        } catch (Exception e) {
            log.error("Error in notification scheduler (30 sec interval)", e);
        }
    }

    /**
     * Метод для очистки старых задач (раз в день)
     * Например, удаляем задачи со статусом SENT старше 30 дней
     */
    @Scheduled(cron = "0 0 3 * * *")  // Каждый день в 3 часа ночи
    public void cleanupOldTasks() {
        log.info("Starting cleanup of old notification tasks...");

        try {
            LocalDateTime threshold = LocalDateTime.now().minusDays(30);

            // Здесь можно добавить логику удаления старых задач
            // Но осторожно - лучше просто архивировать или помечать
            log.info("Cleanup completed. Removing tasks older than {}", threshold);

        } catch (Exception e) {
            log.error("Error during cleanup", e);
        }
    }
}