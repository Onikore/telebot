package somelogic;

import keyboards.Keys;

import static somelogic.DB.*;

public class Logic {

    public Answer getReply(String command, String chatID, Long userID) {
        switch (command) {
            case "/start":
                createDB();
                return new Answer(chatID, "Привет", Keys.start());

            case "Погода сейчас":
                return new Answer(chatID, getForecast(getUserCoords(userID), "now"), Keys.main());

            case "Погода сегодня":
                return new Answer(chatID, getForecast(getUserCoords(userID), "today"), Keys.main());

            case "Погода завтра":
                return new Answer(chatID, getForecast(getUserCoords(userID), "tomorrow"), Keys.main());

            case "Помощь":
                return new Answer(chatID, "Я могу показать тебе погоду:\n в данный момент\n на сегодня \n на завтра\n" +
                        " но сначала дай знать где ты☻\n(это будет всего 1 раз)");

            case "/stop":
                return new Answer(chatID, "Пока");

            default:
                return new Answer(chatID, "такой команды нет");
        }
    }

    public void location(String username, Long userId, float lantitude, float longtitude) {
        setUserData(username,
                userId,
                lantitude,
                longtitude);
    }
}