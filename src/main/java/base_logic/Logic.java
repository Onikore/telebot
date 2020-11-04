package base_logic;

import keyboards.Keys;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Logic {
    private final long chatID;
    private String command;
    private float longtitude;
    private float lantitude;
    private final long userID;

    public Logic(String command, long chatID, long userID) {
        this.chatID = chatID;
        this.command = command;
        this.userID = userID;
    }

    public Logic(long userId, float lantitude, float longtitude, long chatID) {
        this.chatID = chatID;
        this.longtitude = longtitude;
        this.lantitude = lantitude;
        this.userID = userId;
    }

    public Answer getReply() {
        DatabaseManagementSystem dms = new DatabaseManagementSystem(userID);
        WeatherParser wp = null;
        try {
            wp = new WeatherParser(dms.getUserCords());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        switch (command) {
            case "/start":
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

    public Answer setLocation() {
        DatabaseManagementSystem dms = new DatabaseManagementSystem(userID,
                lantitude,
                longtitude);
        dms.setUserCords();
        return new Answer(chatID, "Я тебя записал, cпасибо", Keys.main());
    }
}