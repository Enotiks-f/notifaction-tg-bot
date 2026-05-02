📝 Telegram Бот для напоминаний

Бот для создания и отправки запланированных напоминаний в Telegram.
🚀 Возможности

    Разбор сообщений в формате: ДД.ММ.ГГГГ ЧЧ:ММ Текст напоминания

    Сохранение напоминаний в базу данных

    Автоматическая отправка в указанное время

    Повторная отправка при ошибке (до 3 попыток)

    Отслеживание статуса (PENDING/SENT/FAILED)

🛠️ Технологии

    Java 17

    Spring Boot

    TelegramBot API (pengrad)

    PostgreSQL

    Spring Scheduler

📦 Настройка

    Создайте бота через @BotFather

    Настройте application.properties:

properties

telegram.bot.token=ТОКЕН_ВАШЕГО_БОТА
spring.datasource.url=jdbc:postgresql://localhost:5432/telegram_bot
spring.datasource.username=ваше_имя
spring.datasource.password=ваш_пароль

    Запустите приложение:

bash

./mvnw spring-boot:run

💬 Использование

Отправьте боту сообщение:
text

25.12.2025 19:00 Купить новогодние подарки

Ответ бота: ✅ Напоминание успешно сохранено!
