package pro.sky.telegrambot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.enums.NotificationStatus;
import pro.sky.telegrambot.repository.NotifacationTaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificitionTaskService {

    private final NotifacationTaskRepository repository;
    private final MessageParserService messageParser;

    /**
     * Обрабатывает входящее сообщение и сохраняет напоминание
     * @param chatId ID чата
     * @param messageText текст сообщения
     * @return true если сообщение успешно обработано, false если нет
     */

    @Transactional
    public boolean processNotificationMessage(Long chatId, String messageText) {
        log.info("Processing notification message from chat {}: {}", chatId, messageText);

        // Парсим сообщение
        Optional<NotificationTask> taskOptional = messageParser.parseMessage(chatId, messageText);

        if (taskOptional.isEmpty()) {
            log.warn("Failed to parse message from chat {}", chatId);
            return false;
        }

        NotificationTask task = taskOptional.get();

        // Сохраняем в БД
        repository.save(task);
        log.info("Saved notification task with id: {}, scheduled for: {}",
                task.getId(), task.getSendTime());

        return true;
    }

    @Transactional(readOnly = true)
    public List<NotificationTask> getPendingTasks() {
        log.debug("Fetching pending tasks");
        return repository.findByStatusAndSendTimeBefore(
                NotificationStatus.PENDING,
                LocalDateTime.now()
        );
    }

    /**
     * Отмечает задачу как отправленную
     */
    @Transactional
    public void markAsSent(Long id) {
        log.info("Marking task {} as sent", id);
        repository.findById(id).ifPresent(task -> {
            task.setStatus(NotificationStatus.SENT);
            repository.save(task);
        });
    }

    /**
     * Отмечает задачу как неудачную
     */
    @Transactional
    public void markAsFailed(Long id) {
        log.info("Marking task {} as failed", id);
        repository.findById(id).ifPresent(task -> {
            task.incrementRetryCount();

            if (!task.canRetry()) {
                task.setStatus(NotificationStatus.FAILED);
                log.warn("Task {} failed after {} attempts", id, task.getRetryCount());
            }

            repository.save(task);
        });
    }

    /**
     * Получает все задачи для конкретного чата
     */
    @Transactional(readOnly = true)
    public List<NotificationTask> getTasksByChatId(Long chatId) {
        log.debug("Fetching tasks for chat {}", chatId);
        return repository.findByChatId(chatId);
    }
}
