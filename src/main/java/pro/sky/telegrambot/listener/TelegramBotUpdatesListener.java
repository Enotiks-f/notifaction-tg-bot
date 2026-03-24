package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.service.NotificitionTaskService;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
public class TelegramBotUpdatesListener implements UpdatesListener {


    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificitionTaskService notificitionTaskService;


    @PostConstruct
    public void init() {
        log.info("Initializing Telegram Bot...");

        telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private void processUpdate(Update update) {
        if (update.message() == null) {
            return;
        }

        Long chatId = update.message().chat().id();
        String messageText = update.message().text();

        if (messageText == null || messageText.isBlank()) {
            return;
        }

        log.info("Received message from {}: {}", chatId, messageText);

        // Проверяем команду /start
        if ("/start".equals(messageText)) {
            sendWelcomeMessage(chatId);
            return;
        }


        boolean success = notificitionTaskService.processNotificationMessage(chatId, messageText);

        if (success) {
            sendSuccessMessage(chatId);
        } else {
            sendErrorMessage(chatId);
        }
    }


    private void sendWelcomeMessage(Long chatId) {
        String welcomeText = """
            Привет! Я бот для создания напоминаний.
            
            Чтобы создать напоминание, отправьте сообщение в формате:
            ДД.ММ.ГГГГ ЧЧ:ММ Текст напоминания

            Например:
            01.01.2025 20:00 Сделать домашнюю работу

            Я сохраню напоминание и отправлю его в указанное время.
            """;

        SendMessage request = new SendMessage(chatId, welcomeText);
        telegramBot.execute(request);
        log.info("Sent welcome message to chat {}", chatId);
    }

    private void sendSuccessMessage(Long chatId) {
        String successText = "✅ Напоминание успешно сохранено! Я отправлю его в указанное время.";
        SendMessage request = new SendMessage(chatId, successText);
        telegramBot.execute(request);
    }

    private void sendErrorMessage(Long chatId) {
        String errorText = """
            ❌ Не удалось распознать формат сообщения.

            Пожалуйста, используйте формат:
            ДД.ММ.ГГГГ ЧЧ:ММ Текст напоминания

            Пример:
            01.01.2025 20:00 Сделать домашнюю работу
            """;

        SendMessage request = new SendMessage(chatId, errorText);
        telegramBot.execute(request);
        log.warn("Sent error message to chat {}", chatId);
    }

    @Override
    public int process(List<Update> list) {
        return 0;
    }
}
