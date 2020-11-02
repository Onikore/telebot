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

    public static void setUserData(String username, Long userId, float lantitude, float longtitude) {
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

    public static double[] getUserCoords(long userId) {
        String checkWheather = "SELECT user_latitude,user_longtitude FROM users_data WHERE user_id=?;";
        double[] coords = new double[2];
        try {
            connect();
            stmt = con.prepareStatement(checkWheather);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();

            double userX = rs.getDouble("user_latitude");
            double userY = rs.getDouble("user_longtitude");
            coords = new double[]{userX, userY};

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coords;
    }

    public static String getForecast(double[] coords, String mode) {
        String result = "ЧТото сломалось";
        try {
            String jsontemp = WeatherParser.getJsonContent(coords[0], coords[1]);
            String jsonFromDaily = WeatherParser.getParsedStructure(jsontemp);

            switch (mode) {
                case "today":
                    WeatherModel modelNow = WeatherParser.getTodayForecast(jsonFromDaily);
                    result = TEMPERATURE + "\n\n" +
                            EVE + modelNow.getEve() + " C\n" +
                            DAY + modelNow.getDay() + " C\n" +
                            MORN + modelNow.getMorn() + " C\n" +
                            NIGHT + modelNow.getNight() + " C\n" +
                            DESCRIPTION + modelNow.getWeatherDescription() + "\n";
                    break;
                case "now":
                    WeatherModel modelToday = WeatherParser.getNowForecast(jsontemp);
                    result = NOW + modelToday.getDatetime() + "\n\n" +
                            TEMPERATURE + modelToday.getTemp() + " C\n" +
                            FEELSLIKE + modelToday.getFeelsLike() + " C\n" +
                            HUMIDITY + modelToday.getHumidity() + " %\n" +
                            DESCRIPTION + modelToday.getWeatherDescription() + "\n";
                    break;
                case "tomorrow":
                    WeatherModel modelTomorrow = WeatherParser.getTomorrowForecast(jsonFromDaily);
                    result = TEMPERATURE + "\n\n" +
                            EVE + modelTomorrow.getEve() + " C\n" +
                            DAY + modelTomorrow.getDay() + " C\n" +
                            MORN + modelTomorrow.getMorn() + " C\n" +
                            NIGHT + modelTomorrow.getNight() + " C\n" +
                            DESCRIPTION + modelTomorrow.getWeatherDescription() + "\n";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + mode);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return result;
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
