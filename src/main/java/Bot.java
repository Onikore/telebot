import botlogic.DB;
import botlogic.GetBotInfo;
import botlogic.logic;
import botlogic.message;
import keyboards.keys;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


public class Bot extends TelegramLongPollingBot {

    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String chatID = String.valueOf(message.getChatId());
        Long userID = message.getChat().getId();

        if (message.hasLocation()) {
            DB.writeDB(message.getChat().getUserName(),
                    message.getChat().getId(),
                    message.getLocation().getLatitude(),
                    message.getLocation().getLongitude());
            botlogic.message msh = new message(chatID, "Я тебя записал, cпасибо", keys.Main());
            sender(msh.sendMsg());
        }

        if (update.hasMessage() && message.hasText()) {
            logic commands = new logic();
            message a = commands.checkArgs(message.getText(), chatID, userID);
            sender(a.sendMsg());
        }
    }

    public void sender(SendMessage x) {
        try {
            execute(x);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return GetBotInfo.getName();
    }

    @Override
    public String getBotToken() {
        return GetBotInfo.getToken();
    }
}