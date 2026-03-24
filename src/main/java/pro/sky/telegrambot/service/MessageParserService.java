package pro.sky.telegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class MessageParserService {
    private final static Pattern MESSAGE_PATTERN =
            Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");

    private final static DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Парсит сообщение и создает объект NotificationTask
     * @param chatId ID чата
     * @param messageText текст сообщения
     * @return Optional с NotificationTask или пустой Optional, если парсинг не удался
     */

    public Optional<NotificationTask> parseMessage(Long chatId, String messageText) {

        Matcher matcher = MESSAGE_PATTERN.matcher(messageText.trim());

        if(!matcher.matches()) {
            log.warn("Message does not match pattern: {}", messageText);
            return Optional.empty();
        }

        try {
            String dateTimeStr = matcher.group(1);

            String reminderText = matcher.group(3);

            LocalDateTime sendTime = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
            log.debug("Parsed LocalDateTime: {}", sendTime);

            if (sendTime.isBefore(LocalDateTime.now())) {
                log.warn("Send time is in the past: {}", sendTime);
                return Optional.empty();
            }

            NotificationTask task = NotificationTask.builder()
                    .chatId(chatId)
                    .message(reminderText)
                    .sendTime(sendTime)
                    .build();

            return Optional.of(task);
        }catch(DateTimeParseException e) {
            log.error("Failed to parse LocalDateTime: {}", e.getMessage());
            return Optional.empty();
        }
    }


}
