package somelogic;

import keyboards.Keys;

import static somelogic.DB.*;

public class Logic {

    public MyMessage checkArgs(String command, String chatID, Long userID) {
        switch (command) {
            case "/start":
                createDB();
                return new MyMessage(chatID, "Привет", Keys.start());

            case "Погода сейчас":
                return new MyMessage(chatID, readWeather(userID, "now"), Keys.main());

            case "Погода сегодня":
                return new MyMessage(chatID, readWeather(userID, "today"), Keys.main());

            case "Погода завтра":
                return new MyMessage(chatID, readWeather(userID, "tomorrow"), Keys.main());

            case "Помощь":
                return new MyMessage(chatID, "Я могу показать тебе погоду:\n в данный момент\n на сегодня \n на завтра\n" +
                        " но сначала дай знать где ты☻\n(это будет всего 1 раз)");
            case "/stop":
                return new MyMessage(chatID, "Пока");

            default:
                return new MyMessage(chatID, "такой команды нет");
        }
    }
}