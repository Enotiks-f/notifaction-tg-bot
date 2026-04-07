package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSenderService {

    private final TelegramBot telegramBot;
    private final NotificitionTaskService notificationTaskService;

    /**
     * Отправка одного уведомления
     * @param task задача на отправку
     * @return true если отправка успешна, false если ошибка
     */
    public boolean sendNotification(NotificationTask task) {
        log.info("Sending notification to chat {}: {}", task.getChatId(), task.getMessage());

        try {
            SendMessage request = new SendMessage(task.getChatId(), task.getMessage());
            SendResponse response = telegramBot.execute(request);

            if (response.isOk()) {
                log.info("Notification sent successfully to chat {}", task.getChatId());
                notificationTaskService.markAsSent(task.getId());
                return true;
            } else {
                log.error("Failed to send notification to chat {}: {}",
                        task.getChatId(), response.description());
                handleFailedNotification(task);
                return false;
            }

        } catch (Exception e) {
            log.error("Error sending notification to chat {}: {}",
                    task.getChatId(), e.getMessage());
            handleFailedNotification(task);
            return false;
        }
    }

    /**
     * Обработка неудачной отправки
     */
    private void handleFailedNotification(NotificationTask task) {
        notificationTaskService.markAsFailed(task.getId());

        if (!task.canRetry()) {
            log.warn("Notification task {} failed after {} attempts",
                    task.getId(), task.getRetryCount());
        } else {
            log.info("Notification task {} will be retried (attempt {}/3)",
                    task.getId(), task.getRetryCount() + 1);
        }
    }

    /**
     * Пакетная отправка уведомлений
     */
    public void sendBatchNotifications(java.util.List<NotificationTask> tasks) {
        log.info("Sending batch of {} notifications", tasks.size());

        int successCount = 0;
        int failCount = 0;

        for (NotificationTask task : tasks) {
            if (sendNotification(task)) {
                successCount++;
            } else {
                failCount++;
            }
        }

        log.info("Batch sending completed. Success: {}, Failed: {}", successCount, failCount);
    }
}