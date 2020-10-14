package somelogic;

import models.WeatherModel;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

public class DB {
    private static final String CLASS = "org.sqlite.JDBC";
    private static final String URL = "jdbc:sqlite:src/main/resources/usersData.db";
    private static Connection con;
    private static PreparedStatement stmt;
    private static ResultSet rs;
    private static final Logger logger = Logger.getLogger(DB.class.getName());

    private static final String TEMPERATURE = "Температура: ";
    private static final String DAY = "Днем: ";
    private static final String MORN = "Вечером: ";
    private static final String EVE = "Утром: ";
    private static final String NIGHT = "Ночью: ";
    private static final String DESCRIPTION = "Погодные условия: ";
    private static final String NOW = "Сегодня: ";
    private static final String HUMIDITY = "Влажность: ";
    private static final String FEELSLIKE = "Ощущается как: ";

    private DB() {
    }

    public static void connect() {
        try {
            Class.forName(CLASS);
            con = DriverManager.getConnection(URL);
            logger.log(INFO, "База подключена");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createDB() {
        boolean check = true;
        connect();
        try (Statement a = con.createStatement()) {
            a.execute("CREATE TABLE if not exists 'users_data' " +
                    "('user_id' VARCHAR PRIMARY KEY UNIQUE , " +
                    "'user_name' VARCHAR ," +
                    "'user_latitude' DOUBLE, " +
                    "'user_longtitude' DOUBLE);");
            logger.log(INFO, "Таблица создана");
        } catch (SQLException e) {
            check = false;
            logger.log(WARNING, format("Ошибка: %s", e));
        } finally {
            if (check) close();
        }
    }

    public static void writeDB(String username, Long userId, float lantitude, float longtitude) {
        String insertstr = "INSERT INTO users_data (user_id, user_name,user_latitude,user_longtitude) VALUES (?,?,?,?);";
        String check = "SELECT COUNT(*) FROM users_data WHERE user_id = ?;";
        try {
            connect();

            stmt = con.prepareStatement(check);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            if (rs.getLong(1) != 0) {
                logger.log(INFO, "Пользователь с ID {}  уже сущетвует", userId);
            }

            stmt = con.prepareStatement(insertstr);
            stmt.setInt(1, Math.toIntExact(userId));
            stmt.setString(2, username);
            stmt.setFloat(3, lantitude);
            stmt.setFloat(4, longtitude);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String readWeather(long userId, String mode) {
        String checkWheather = "SELECT user_latitude,user_longtitude FROM users_data WHERE user_id=?;";
        String res = "Что то сломалось";
        try {
            connect();
            stmt = con.prepareStatement(checkWheather);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Double userX = rs.getDouble("user_latitude");
                Double userY = rs.getDouble("user_longtitude");

                String jsontemp = WeatherClass.getJsonString(userX, userY);
                String jsonFromDaily = WeatherClass.jsonToDaily(jsontemp);

                if ("today".equals(mode)) {
                    WeatherModel modelNow = WeatherClass.weatherNow(jsonFromDaily);
                    res = TEMPERATURE + "\n\n" +
                            EVE + modelNow.getEve() + " C" + "\n" +
                            DAY + modelNow.getDay() + " C" + "\n" +
                            MORN + modelNow.getMorn() + " C" + "\n" +
                            NIGHT + modelNow.getNight() + " C" + "\n" +
                            DESCRIPTION + modelNow.getWeatherDescription() + "\n";
                } else if ("now".equals(mode)) {
                    WeatherModel modelToday = WeatherClass.jsonToCurrent(jsontemp);
                    res = NOW + modelToday.getDatetime() + "\n\n" +
                            TEMPERATURE + modelToday.getTemp() + " C" + "\n" +
                            FEELSLIKE + modelToday.getFeelsLike() + " C" + "\n" +
                            HUMIDITY + modelToday.getHumidity() + " %" + "\n" +
                            DESCRIPTION + modelToday.getWeatherDescription() + "\n";
                } else if ("tomorrow".equals(mode)) {
                    WeatherModel modelTomorrow = WeatherClass.weatherTomorrow(jsonFromDaily);
                    res = TEMPERATURE + "\n\n" +
                            EVE + modelTomorrow.getEve() + " C" + "\n" +
                            DAY + modelTomorrow.getDay() + " C" + "\n" +
                            MORN + modelTomorrow.getMorn() + " C" + "\n" +
                            NIGHT + modelTomorrow.getNight() + " C" + "\n" +
                            DESCRIPTION + modelTomorrow.getWeatherDescription() + "\n";
                }
            }
        } catch (ParseException | IOException | SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void close() {
        try {
            con.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
