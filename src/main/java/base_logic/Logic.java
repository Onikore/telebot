package base_logic;

import keyboards.Keys;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logic {

    private final Logger logger = Logger.getLogger(Logic.class.getName());

    public Answer getReply(String command, long chatID, long userID) {
        UserRepository repo = new UserRepository();
        WeatherParser wp = null;

        try {
            wp = new WeatherParser(repo.getUserCords(userID));
        } catch (IOException | ParseException e) {
            logger.log(Level.WARNING,"ОШИБКА",e);
        }

        switch (command) {
            case "/start":
                repo.createDB();
                return new Answer(chatID, "Привет", Keys.start());

            case "Погода сейчас":
                assert wp != null;
                return new Answer(chatID, wp.getForecast("now"), Keys.main());

            case "Погода сегодня":
                assert wp != null;
                return new Answer(chatID, wp.getForecast("today"), Keys.main());

            case "Погода завтра":
                assert wp != null;
                return new Answer(chatID, wp.getForecast("tomorrow"), Keys.main());

            case "Помощь":
                return new Answer(chatID, "Я могу показать тебе погоду:\n в данный момент\n на сегодня \n на завтра\n" +
                        " но сначала дай знать где ты☻\n(это будет всего 1 раз)");

            case "/stop":
                return new Answer(chatID, "Пока");

            default:
                return new Answer(chatID, "такой команды нет");
        }
    }

    public Answer setLocation(long userId, float lantitude, float longtitude, long chatID) {
        UserRepository dms = new UserRepository();
        dms.setUserCords(userId,lantitude,longtitude);
        return new Answer(chatID, "Я тебя записал, cпасибо", Keys.main());
    }
}