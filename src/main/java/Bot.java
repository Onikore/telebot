import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Bot extends TelegramLongPollingBot {

    private static String BOT_NAME;
    private static String BOT_TOKEN;

    public static void main(String[] args) {
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream("src/main/resources/config.properties");
            prop.load(fileInputStream);
            BOT_NAME = prop.getProperty("BOT_NAME");
            BOT_TOKEN = prop.getProperty("BOT_TOKEN");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateReceived(Update update){

        Message message = update.getMessage();
        String chatID = String.valueOf(update.getMessage().getChatId());
        Long userID = message.getChat().getId();

        if(message.hasLocation()) {
            DB.CreateDB();
            float latitude = message.getLocation().getLatitude();
            float longtitude = message.getLocation().getLongitude();

            DB.WriteDB(message.getChat().getUserName(),
                    message.getChat().getId(),
                    latitude,
                    longtitude);
            sendMsgWithKey(chatID, "Я тебя записал, cпасибо", keys.Main());
            DB.Close();
        }

        if(update.hasMessage() && message.hasText()){
            switch (message.getText()) {
                case "/start":
                    sendMsgWithKey(chatID,"Привет",keys.Start());
                    break;

                case "Погода сейчас":
                    sendMsgWithKey(chatID, DB.ReadWeather(userID, "now"), keys.Main());
                    DB.Close();
                    break;

                case "Погода сегодня":
                    sendMsgWithKey(chatID, DB.ReadWeather(userID, "today"), keys.Main());
                    DB.Close();
                    break;

                case "Погода завтра":
                    sendMsgWithKey(chatID, DB.ReadWeather(userID, "tomorrow"), keys.Main());
                    break;

                case "Помощь":
                    sendMsg(chatID, "Я могу показать тебе погоду:\n в данный момент\n на сегодня \n на завтра\n" +
                            " но сначала дай знать где ты☻\n(это будет всего 1 раз)");
                    break;
                case "/stop":
                    sendMsg(chatID, "Жаль что ты уходишь");
                    break;
                default:
                    sendMsg(chatID, "такой команды нет");
                    break;
            }
        }
    }

    public synchronized void sendMsg(String chatId, String s) {
        Logger logger = Logger.getLogger(Bot.class.getName());
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "Exception: " + e.toString());
        }
    }

    public synchronized void sendMsgWithKey(String chatId, String s, ReplyKeyboard mode) {
        Logger logger = Logger.getLogger(Bot.class.getName());
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        sendMessage.setReplyMarkup(mode);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "Exception: " + e.toString());
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}