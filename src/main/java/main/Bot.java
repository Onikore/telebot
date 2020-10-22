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
import somelogic.GetBotInfo;
import somelogic.Logic;
import somelogic.MyMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

import static somelogic.DB.close;
import static somelogic.DB.writeDB;

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

        if (message.hasLocation()) {
            writeDB(message.getChat().getUserName(),
                    message.getChat().getId(),
                    message.getLocation().getLatitude(),
                    message.getLocation().getLongitude());
            MyMessage msh = new MyMessage(chatID, "Я тебя записал, cпасибо", Keys.main());
            sender(msh.sendMsg());
        }

        if (update.hasMessage() && message.hasText()) {
            Logic commands = new Logic();
            MyMessage a = commands.checkArgs(message.getText(), chatID, userID);
            sender(a.sendMsg());
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
        return GetBotInfo.getInfo("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return GetBotInfo.getInfo("BOT_TOKEN");
    }
}