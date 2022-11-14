package pro.sky.telegrambot.responder;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.logging.*;

@Service
public class TelegramBotReminder {
    private final Logger logger = (Logger) LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    private final NotificationTaskService notificationTaskService;

    public TelegramBotReminder(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void reminder() {
        try {
            logger.info("Checking tasks every minute");
            LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            Collection<NotificationTask> notificationTasks = notificationTaskService.findAllByTime(time);
            notificationTasks.forEach(notificationTask -> {
                Long notificationTaskId = notificationTask.getChatId();
                telegramBot.execute(new SendMessage(notificationTaskId, notificationTask.getNotification()));
            });
        } catch (IllegalArgumentException exception) {
            logger.error("An error occurred when the method *responseTelegramBot* was invoked.");
        }
    }
}
