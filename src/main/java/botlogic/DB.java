package botlogic;

import models.weatherModel;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;

public class DB {
    private static final String className = "org.sqlite.JDBC";
    private static final String driverURL = "jdbc:sqlite:src/main/resources/users_location.db";
    private static Connection con;
    private static PreparedStatement stmt;
    private static ResultSet rs;

    public static void connect() {
        try {
            Class.forName(className);
            con = DriverManager.getConnection(driverURL);
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
        String insert_str = "INSERT INTO users_data (user_id, user_name,user_latitude,user_longtitude) VALUES (?,?,?,?);";
        String check_id = "SELECT COUNT(*) FROM users_data WHERE user_id = ?;";
        try {
            connect();

            stmt = con.prepareStatement(check_id);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();

            if (rs.getLong(1) == 0) {
                PreparedStatement state = con.prepareStatement(insert_str);
                state.setInt(1, Math.toIntExact(userId));
                state.setString(2, username);
                state.setFloat(3, lantitude);
                state.setFloat(4, longtitude);
                state.execute();
            } else {
                System.out.println("Пользователь с ID " + userId + " уже сущетвует");
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
                Double user_x = rs.getDouble("user_latitude");
                Double user_y = rs.getDouble("user_longtitude");

                String jsontemp = WeatherClass.get_json_string(user_x, user_y);
                String jsonFromDaily = WeatherClass.jsonToDaily(jsontemp);

                switch (mode) {
                    case "today":
                        weatherModel modelNow = WeatherClass.weatherNow(jsonFromDaily);
                        res = "Температура: " + "\n\n" +
                                "Утром: " + modelNow.getEve() + " C" + "\n" +
                                "Днем: " + modelNow.getDay() + " C" + "\n" +
                                "Вечером: " + modelNow.getMorn() + " C" + "\n" +
                                "Ночью: " + modelNow.getNight() + " C" + "\n" +
                                "Погодные условия: " + modelNow.getWeatherDescription() + "\n";
                        break;

                    case "now":
                        weatherModel modelToday = WeatherClass.jsonToCurrent(jsontemp);
                        res = "Сегодня: " + modelToday.getDatetime() + "\n\n" +
                                "Температура: " + modelToday.getTemp() + " C" + "\n" +
                                "Ощущается как: " + modelToday.getFeelsLike() + " C" + "\n" +
                                "Влажность: " + modelToday.getHumidity() + " %" + "\n" +
                                "Погодные условия: " + modelToday.getWeatherDescription() + "\n";
                        break;

                    case "tomorrow":
                        weatherModel modelTomorrow = WeatherClass.weatherTomorrow(jsonFromDaily);
                        res = "Температура: " + "\n\n" +
                                "Утром: " + modelTomorrow.getEve() + " C" + "\n" +
                                "Днем: " + modelTomorrow.getDay() + " C" + "\n" +
                                "Вечером: " + modelTomorrow.getMorn() + " C" + "\n" +
                                "Ночью: " + modelTomorrow.getNight() + " C" + "\n" +
                                "Погодные условия: " + modelTomorrow.getWeatherDescription() + "\n";
                        break;
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

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }
}
