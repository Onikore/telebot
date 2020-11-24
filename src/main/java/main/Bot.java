package main;

import base_logic.Answer;
import base_logic.Config;
import base_logic.Logic;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.logging.Level;
import java.util.logging.Logger;

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
        Logic logic = new Logic();
        Answer answer;
        Message message = update.getMessage();

        long chatID = message.getChatId();
        long userID = message.getChat().getId();

        if (message.hasLocation()) {
            Float latitude = message.getLocation().getLatitude();
            Float longitude = message.getLocation().getLongitude();
            answer = logic.setLocation(userID, latitude, longitude, chatID);
        } else if (update.hasMessage() && message.hasText()) {
            answer = logic.getReply(message.getText(), chatID, userID);
        } else {
            answer = new Answer(chatID, "я не понимаю");
        }
        sender(answer.sendMsg());
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