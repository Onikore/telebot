package somelogic;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class message {

    private final String chatId;
    private final String s;
    private ReplyKeyboard mode;

    public message(String chatId, String s) {
        this.chatId = chatId;
        this.s = s;
    }

    public message(String chatId, String s, ReplyKeyboard mode) {
        this.chatId = chatId;
        this.s = s;
        this.mode = mode;
    }

    public synchronized SendMessage sendMsg() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        if (mode != null) sendMessage.setReplyMarkup(mode);

        return sendMessage;
    }
}
