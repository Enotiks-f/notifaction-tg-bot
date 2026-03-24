-- liquibase formatted sql

-- changeset author:1
CREATE TABLE IF NOT EXISTS notification_task (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 chat_id BIGINT  NOT NULL,
    message TEXT NOT NULL,
    send_time TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    retry_count INTEGER DEFAULT 0
    );

-- changeset author:2
CREATE INDEX IF NOT EXISTS idx_notification_task_send_time ON notification_task(send_time);
CREATE INDEX IF NOT EXISTS idx_notification_task_status ON notification_task(status);
CREATE INDEX IF NOT EXISTS idx_notification_task_chat_id ON notification_task(chat_id);

-- changeset author:3
COMMENT ON TABLE notification_task IS 'Таблица для хранения задач на отправку уведомлений';
COMMENT ON COLUMN notification_task.id IS 'Первичный ключ';
COMMENT ON COLUMN notification_task.chat_id IS 'Идентификатор чата для отправки уведомления';
COMMENT ON COLUMN notification_task.message IS 'Текст уведомления';
COMMENT ON COLUMN notification_task.send_time IS 'Время отправки уведомления';
COMMENT ON COLUMN notification_task.status IS 'Статус задачи (PENDING, SENT, FAILED)';
COMMENT ON COLUMN notification_task.created_at IS 'Время создания задачи';
COMMENT ON COLUMN notification_task.updated_at IS 'Время последнего обновления';
COMMENT ON COLUMN notification_task.retry_count IS 'Количество попыток отправки';