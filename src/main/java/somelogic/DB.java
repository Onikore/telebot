package somelogic;

import models.WeatherModel;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Logger;

import static consts.Constants.*;
import static java.lang.String.format;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

public class DB {
    private static final Logger logger = Logger.getLogger(DB.class.getName());
    private static Connection con;
    private static PreparedStatement stmt;
    private static ResultSet rs;

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
        connect();
        try (Statement a = con.createStatement()) {
            a.execute("CREATE TABLE if not exists 'users_data' " +
                    "('user_id' VARCHAR PRIMARY KEY UNIQUE , " +
                    "'user_name' VARCHAR ," +
                    "'user_latitude' DOUBLE, " +
                    "'user_longtitude' DOUBLE);");
            logger.log(INFO, "Таблица создана");
        } catch (SQLException e) {
            logger.log(WARNING, format("Ошибка: %s", e));
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
            if (rs.getLong(1) == 0) {
                stmt = con.prepareStatement(insertstr);
                stmt.setInt(1, Math.toIntExact(userId));
                stmt.setString(2, username);
                stmt.setFloat(3, lantitude);
                stmt.setFloat(4, longtitude);
                stmt.execute();
            } else {
                logger.log(INFO, "Пользователь с ID {0}  уже сущетвует", userId);
            }
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

                String jsontemp = WeatherParser.jsonString(userX, userY);
                String jsonFromDaily = WeatherParser.weatherDaily(jsontemp);

                if ("today".equals(mode)) {
                    WeatherModel modelNow = WeatherParser.weatherNow(jsonFromDaily);
                    res = TEMPERATURE + "\n\n" +
                            EVE + modelNow.getEve() + " C\n" +
                            DAY + modelNow.getDay() + " C\n" +
                            MORN + modelNow.getMorn() + " C\n" +
                            NIGHT + modelNow.getNight() + " C\n" +
                            DESCRIPTION + modelNow.getWeatherDescription() + "\n";
                } else if ("now".equals(mode)) {
                    WeatherModel modelToday = WeatherParser.parsedWeather(jsontemp);
                    res = NOW + modelToday.getDatetime() + "\n\n" +
                            TEMPERATURE + modelToday.getTemp() + " C\n" +
                            FEELSLIKE + modelToday.getFeelsLike() + " C\n" +
                            HUMIDITY + modelToday.getHumidity() + " %\n" +
                            DESCRIPTION + modelToday.getWeatherDescription() + "\n";
                } else if ("tomorrow".equals(mode)) {
                    WeatherModel modelTomorrow = WeatherParser.weatherTomorrow(jsonFromDaily);
                    res = TEMPERATURE + "\n\n" +
                            EVE + modelTomorrow.getEve() + " C\n" +
                            DAY + modelTomorrow.getDay() + " C\n" +
                            MORN + modelTomorrow.getMorn() + " C\n" +
                            NIGHT + modelTomorrow.getNight() + " C\n" +
                            DESCRIPTION + modelTomorrow.getWeatherDescription() + "\n";
                } else {
                    throw new IllegalStateException("Unexpected value: " + mode);
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
