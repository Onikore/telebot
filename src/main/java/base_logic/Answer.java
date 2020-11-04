package base_logic;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class Answer {

    private final long chatId;
    private final String message;
    private ReplyKeyboard mode;

    public Answer(long chatId, String msg) {
        this.chatId = chatId;
        this.message = msg;
    }

    public Answer(long chatId, String msg, ReplyKeyboard mode) {
        this.chatId = chatId;
        this.message = msg;
        this.mode = mode;
    }

    public SendMessage sendMsg() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        if (mode != null) sendMessage.setReplyMarkup(mode);
        return sendMessage;
    }
}
