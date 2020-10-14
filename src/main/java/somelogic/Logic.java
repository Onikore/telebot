package somelogic;

import keyboards.Keys;

public class Logic {

    public Message checkArgs(String command, String chatID, Long userID) {
        switch (command) {
            case "/start":
                DB.createDB();
                return new Message(chatID, "Привет", Keys.start());

            case "Погода сейчас":
                return new Message(chatID, DB.readWeather(userID, "now"), Keys.main());

            case "Погода сегодня":
                return new Message(chatID, DB.readWeather(userID, "today"), Keys.main());

            case "Погода завтра":
                return new Message(chatID, DB.readWeather(userID, "tomorrow"), Keys.main());

            case "Помощь":
                return new Message(chatID, "Я могу показать тебе погоду:\n в данный момент\n на сегодня \n на завтра\n" +
                        " но сначала дай знать где ты☻\n(это будет всего 1 раз)");
            case "/stop":
                return new Message(chatID, "Пока");

            default:
                return new Message(chatID, "такой команды нет");
        }
    }
}