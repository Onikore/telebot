package consts;

public class Constants {
    //формат даты для weatherparser
    public static final String DAYFORMAT = "dd-MMMM-yyyy";
    // данные для db
    public static final String TEMPERATURE = "Температура: ";
    public static final String DAY = "Днем: ";
    public static final String MORN = "Вечером: ";
    public static final String EVE = "Утром: ";
    public static final String NIGHT = "Ночью: ";
    public static final String DESCRIPTION = "Погодные условия: ";
    public static final String NOW = "Сегодня: ";
    public static final String HUMIDITY = "Влажность: ";
    public static final String FEELSLIKE = "Ощущается как: ";
    public static final String CLASS = "org.sqlite.JDBC";
    public static final String URL = "jdbc:sqlite:src/main/resources/usersData.db";
    //путь до конфига в getbotinfo
    public static final String PATH = "src/main/resources/config.properties";

    private Constants() {
    }

}
