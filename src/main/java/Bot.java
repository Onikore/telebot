import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Метод для приема сообщений.
     *
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update){

        Message message = update.getMessage();
        String chatID = String.valueOf(update.getMessage().getChatId());

        if(message.hasLocation()) {
            sendMsg(chatID,
                    "ЛОКАЦИЯ ЕСТЬ");
            float latitude = message.getLocation().getLatitude();
            float longtitude = message.getLocation().getLongitude();
            sendMsg(chatID,
                    "ЗАХОЖУ В ТРАЙ");
            try {
                DB.WriteDB(message.getChat().getUserName(),
                        message.getChat().getId(),
                        latitude,
                        longtitude);
                sendMsg(chatID,
                        "Спасибо");
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            sendMsg(chatID,
                    "ВЫХОЖУ");
        }

        if(update.hasMessage() && message.hasText()){
            switch (message.getText()){
                case "/start":
                    sendMsg(chatID,
                            "здарова");
                    break;
                case "Помощь":
                    sendMsg(chatID,
                            "я ничего не умею");
                    break;
                case "Погода":
                    try {
                        sendMsg(chatID,
                                (DB.ReadDB(message.getChat().getId())));
                    } catch (SQLException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    sendMsg(chatID,
                            "такой команды нет хех)");
                    break;
            }
        }
    }

    /**
     * Метод для настройки сообщения и его отправки.
     *  @param chatId id чата
     * @param s      Строка, которую необходимот отправить в качестве сообщения.
     */
    public synchronized void sendMsg(String chatId, String s) {
        Logger logger = Logger.getLogger(Bot.class.getName());

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        setButtonsForStart(sendMessage);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "Exception: " + e.toString());
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public synchronized void setButtonsForStart(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);


        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Получить геолокацию").setRequestLocation(true));
        keyboardFirstRow.add(new KeyboardButton("Погода"));

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("Помощь"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}