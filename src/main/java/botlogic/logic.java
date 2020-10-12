package botlogic;

import keyboards.keys;

public class logic {

    public message checkArgs(String command, String chatID, Long userID) {
        switch (command) {
            case "/start":
                DB.createDB();
                return new message(chatID, "Привет", keys.Start());

            case "Погода сейчас":
                return new message(chatID, DB.readWeather(userID, "now"), keys.Main());

            case "Погода сегодня":
                return new message(chatID, DB.readWeather(userID, "today"), keys.Main());

            case "Погода завтра":
                return new message(chatID, DB.readWeather(userID, "tomorrow"), keys.Main());

            case "Помощь":
                return new message(chatID, "Я могу показать тебе погоду:\n в данный момент\n на сегодня \n на завтра\n" +
                        " но сначала дай знать где ты☻\n(это будет всего 1 раз)");
            case "/stop":
                return new message(chatID, "Пока");

            default:
                return new message(chatID, "такой команды нет");
        }
    }
}