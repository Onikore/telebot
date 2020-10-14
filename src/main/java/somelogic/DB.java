package somelogic;
import models.weatherModel;

import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.sql.*;

public class DB {
    private static final String CLASS = "org.sqlite.JDBC";
    private static final String URL = "jdbc:sqlite:src/main/resources/users_location.db";
    private static Connection con;
    private static PreparedStatement stmt;
    private static ResultSet rs;

    private static final String TEMPERATURE = "Температура: ";
    private static final String DAY ="Днем: ";
    private static final String MORN ="Вечером: ";
    private static final String EVE = "Утром: ";
    private static final String NIGHT ="Ночью: ";
    private static final String DESCRIPTION = "Погодные условия: ";
    private static final String NOW = "Сегодня: ";
    private static final String HUMIDITY = "Влажность: ";
    private static final String FEELSLIKE =  "Ощущается как: ";



    public static void connect() {
        try {
            Class.forName(CLASS);
            con = DriverManager.getConnection(URL);
            System.out.println("База Подключена!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createDB() {
        try {
            connect();
            con.createStatement().execute("CREATE TABLE if not exists 'users_data' " +
                    "('user_id' VARCHAR PRIMARY KEY UNIQUE , " +
                    "'user_name' VARCHAR ," +
                    "'user_latitude' DOUBLE, " +
                    "'user_longtitude' DOUBLE);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Таблица создана или уже существует.");
    }

    public static void writeDB(String username, Long userId, float lantitude, float longtitude) {
        String insertstr = "INSERT INTO users_data (user_id, user_name,user_latitude,user_longtitude) VALUES (?,?,?,?);";
        String check_id = "SELECT COUNT(*) FROM users_data WHERE user_id = ?;";
        try {
            connect();

            stmt = con.prepareStatement(check_id);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();

            if (rs.getLong(1) != 0) System.out.println("Пользователь с ID " + userId + " уже сущетвует");

            PreparedStatement state = con.prepareStatement(insertstr);
            state.setInt(1, Math.toIntExact(userId));
            state.setString(2, username);
            state.setFloat(3, lantitude);
            state.setFloat(4, longtitude);
            state.execute();
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
                Double user_x = rs.getDouble("user_latitude");
                Double user_y = rs.getDouble("user_longtitude");

                String jsontemp = WeatherClass.get_json_string(user_x, user_y);
                String jsonFromDaily = WeatherClass.jsonToDaily(jsontemp);

                if ("today".equals(mode)) {
                    weatherModel modelNow = WeatherClass.weatherNow(jsonFromDaily);
                    res = TEMPERATURE + "\n\n" +
                            EVE + modelNow.getEve() + " C" + "\n" +
                            DAY + modelNow.getDay() + " C" + "\n" +
                            MORN + modelNow.getMorn() + " C" + "\n" +
                            NIGHT + modelNow.getNight() + " C" + "\n" +
                            DESCRIPTION + modelNow.getWeatherDescription() + "\n";
                } else if ("now".equals(mode)) {
                    weatherModel modelToday = WeatherClass.jsonToCurrent(jsontemp);
                    res = NOW + modelToday.getDatetime() + "\n\n" +
                            TEMPERATURE + modelToday.getTemp() + " C" + "\n" +
                            FEELSLIKE + modelToday.getFeelsLike() + " C" + "\n" +
                            HUMIDITY + modelToday.getHumidity() + " %" + "\n" +
                            DESCRIPTION + modelToday.getWeatherDescription() + "\n";
                } else if ("tomorrow".equals(mode)) {
                    weatherModel modelTomorrow = WeatherClass.weatherTomorrow(jsonFromDaily);
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Deprecated
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
