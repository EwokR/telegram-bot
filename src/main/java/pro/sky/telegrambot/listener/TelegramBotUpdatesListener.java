package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Interaction;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.InteractionService;
import pro.sky.telegrambot.service.NotificationTaskService;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final InteractionService interactionService;
    private final NotificationTaskService notificationTaskService;
    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(InteractionService interactionService, NotificationTaskService notificationTaskService, TelegramBot telegramBot) {
        this.interactionService = interactionService;
        this.notificationTaskService = notificationTaskService;
        this.telegramBot = telegramBot;
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final Pattern PATTERN = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+|\\w]+)");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        Collection<Interaction> allInteractions = interactionService.getAllPossibleInteractions();
        boolean sent = false;
        for (Update update : updates){
            logger.info("Processing update: {}", update);
            if (update.message() != null) {
                String receivedMessage = update.message().text();
                if (receivedMessage.startsWith("/")) {
                    for (Interaction interaction : allInteractions) {
                        if (receivedMessage.equalsIgnoreCase(interaction.getRequest())) {
                            sent = true;
                            responseTelegramBot(update, interaction.getResponse());
                        }
                    }
                    if (!sent) {
                        responseTelegramBot(update, "This is illegal you know? Use /help to stay out trouble");
                    }
                } else {
                    addTask(update);
                }
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void addTask(Update update) {
        String receivedMessage = update.message().text();
        NotificationTask notificationTask = new NotificationTask();
        Matcher matcher = PATTERN.matcher(receivedMessage);
        if (matcher.matches()) {
            String date = matcher.group(1);
            String notification = matcher.group(3);
            try {
                LocalDateTime time = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
                notificationTask.setChatId(update.message().chat().id());
                notificationTask.setNotification(notification);
                notificationTask.setTime(time.truncatedTo(ChronoUnit.MINUTES));
                if (time.isBefore(LocalDateTime.now())) {
                    responseTelegramBot(update, "Traveling back in time is forbidden for a reason you know?");
                } else if (notificationTaskService.existByNotificationAndChatIdAndTime(notification, update.message().chat().id(), time)) {
                    logger.error("This notification already exists.");
                    responseTelegramBot(update, "This notification already exists.");
                } else {
                    addNotification(notificationTask);
                    responseTelegramBot(update, "I received notify task letter. It says this need to be done: \n"
                            + receivedMessage + "\n as soon as possible");
                }
            } catch (Exception e) {
                responseTelegramBot(update, "Received illegal time or date format!");
            }
        } else {
            responseTelegramBot(update,"Received illegal time or date format!");
        }
    }
    public void responseTelegramBot(Update update, String response) {
        SendMessage sentMessage = new SendMessage(update.message().chat().id(), response);
        SendResponse sentResponse = telegramBot.execute(sentMessage);
        if (!sentResponse.isOk()) {
            logger.error("An error occurred when the method *responseTelegramBot* was invoked.");
        }
    }

    public void addNotification(NotificationTask notificationTask) {
        logger.info("Method *addNotification* was invoked");
        notificationTaskService.saveNotificationTask(notificationTask);
    }
}
