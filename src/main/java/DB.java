import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;

public class DB {
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    private static final String className = "org.sqlite.JDBC";
    private static final String driverURL = "jdbc:sqlite:src/main/resources/users_location.db";

    public static void Connect() throws ClassNotFoundException, SQLException {
        Class.forName(className);
        con = DriverManager.getConnection(driverURL);
        System.out.println("База Подключена!");
    }

    public static void CreateDB() {
        try {
            Connect();
            stmt = con.createStatement();
            stmt.execute("CREATE TABLE if not exists 'users_data' " +
                    "('user_id' VARCHAR PRIMARY KEY UNIQUE , " +
                    "'user_name' VARCHAR ," +
                    "'user_latitude' DOUBLE, " +
                    "'user_longtitude' DOUBLE);");
            System.out.println("Таблица создана или уже существует.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void WriteDB(String username, Long userId, float lantitude, float longtitude) {
        String insert_str = "INSERT INTO users_data (user_id, user_name,user_latitude,user_longtitude) VALUES (?,?,?,?);";
        String check_id = "SELECT COUNT(*) FROM users_data WHERE user_id" + "='" + userId + "'";
        try {
            Connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery(check_id);
            if (rs == null) {
                PreparedStatement statement = con.prepareStatement(insert_str);
                try {
                    statement.setInt(1, Math.toIntExact(userId));
                    statement.setString(2, username);
                    statement.setFloat(3, lantitude);
                    statement.setFloat(4, longtitude);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else System.out.println("Пользователь с ID " + userId + " уже сущетвует");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String ReadDB(long userId) {
        String checkWheather = "SELECT user_latitude,user_longtitude FROM users_data WHERE user_id" + "='" + userId + "'";
        String res = "Что то сломалось☺";
        try {
            Connect();
            stmt = con.createStatement();
            rs = stmt.executeQuery(checkWheather);
            while (rs.next()) {
                Double user_x = rs.getDouble("user_latitude");
                Double user_y = rs.getDouble("user_longtitude");

                String jsontemp = WeatherClass.get_json_string(user_x, user_y);
                Model model = WeatherClass.jsonToCurrent(jsontemp);

                res = "Сегодня: " + model.getDatetime() + "\n\n" +
                        "Температура: " + model.getTemp() + " C" + "\n" +
                        "Ощущается как: " + model.getFeelsLike() + " C" + "\n" +
                        "Влажность: " + model.getHumidity() + " %" + "\n" +
                        "Погодные условия: " + model.getWeatherDescription() + "\n";
            }
        } catch (ParseException | IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static void Close() {
        try {
            con.close();
            stmt.close();
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        WriteDB("onikore",
//                (283134908L),
//                56.81480407714844f,
//                60.7106819152832f);
//    }
}

