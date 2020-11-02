package main;

import keyboards.Keys;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import somelogic.Answer;
import somelogic.Config;
import somelogic.Logic;

import java.util.logging.Level;
import java.util.logging.Logger;

import static somelogic.DB.close;

public class Bot extends TelegramLongPollingBot {
    private static final Logger logger = Logger.getLogger(Bot.class.getName());

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            logger.log(Level.WARNING, "ОШИБКА ", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String chatID = String.valueOf(message.getChatId());
        Long userID = message.getChat().getId();
        String userName = message.getChat().getUserName();

        if (message.hasLocation()) {
            Float latitude = message.getLocation().getLatitude();
            Float longitude = message.getLocation().getLongitude();

            Logic logic = new Logic();
            logic.location(userName, userID, latitude, longitude);
            Answer msg = new Answer(chatID, "Я тебя записал, cпасибо", Keys.main());
            sender(msg.sendMsg());
        }

        if (update.hasMessage() && message.hasText()) {
            Logic logic = new Logic();
            Answer answer = logic.getReply(message.getText(), chatID, userID);
            sender(answer.sendMsg());
            close();
        }
    }


    public void sender(SendMessage x) {
        try {
            execute(x);
        } catch (TelegramApiException e) {
            logger.log(Level.WARNING, "ОШИБКА ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return Config.getInfo("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return Config.getInfo("BOT_TOKEN");
    }
}