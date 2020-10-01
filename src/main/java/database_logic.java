import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.*;

public class database_logic {


    public static Connection connection; // аналог connect
    public static Statement statmt; // аналог cursor
    public static ResultSet resSet; // аналог execute

    /**
     * Подключается к базе данных(далее БД)
     */
    public static void Connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/users_location.db");
        System.out.println("База Подключена!");
    }

    /**
     * Создает таблицу в БД
     */
    public static void CreateDB() throws SQLException {
        statmt = connection.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users_data' " +
                "('user_id' VARCHAR PRIMARY KEY UNIQUE , " +
                "'user_name' VARCHAR ," +
                "'user_latitude' DOUBLE, " +
                "'user_longtitude' DOUBLE);");
        System.out.println("Таблица создана или уже существует.");
    }

    public static void WriteDB(String username, Long userId, float lantitude, float longtitude) throws SQLException {
        Statement stat = connection.createStatement();

        String insert_str = "INSERT INTO 'users_data' ('user_id', 'user_name','user_latitude','user_longtitude') VALUES (?,?,?,?);";
        String check_id = "SELECT COUNT(*) FROM 'users_data' WHERE 'user_id'" + "=" + userId;

        ResultSet resultSet = stat.executeQuery(check_id);
        if (resultSet == null) {
            PreparedStatement statement = connection.prepareStatement(insert_str);
            try {
                statement.setInt(1, Math.toIntExact(userId));
                statement.setString(2, username);
                statement.setFloat(3, lantitude);
                statement.setFloat(4, longtitude);

                if (statement.executeUpdate() == 1) {
                    System.out.println("Таблица заполнена");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Пользователь с ID " + userId + " уже сущетвует");
        }
    }

    /**
     * Вывод всей БД в консоль
     */
    public static String ReadDB(long userId) throws SQLException {
        Statement stat = connection.createStatement();
        String checkWheather = "SELECT 'user_latitude','user_longtitude'FROM 'users_data' WHERE 'user_id'" + "=" + userId;
        ResultSet resres = stat.executeQuery(checkWheather);

        Double user_altitude = resres.getDouble("user_latitude");
        Double user_longtitude = resres.getDouble("user_longtitude");
        String result = "null";
        try {
            String jsontemp = WeatherClass.get_json_string(user_altitude, user_longtitude);
            Model model = WeatherClass.jsonToCurrent(jsontemp);
            result = "Сегодня: " + model.getDatetime() + "\n" +
                    "Температура: " + model.getTemp() + "\n" +
                    "Чувствуется как: " + model.getFeelsLike() + "\n" +
                    "Влажность: " + model.getHumidity() + "\n" +
                    "Погодные условия: " + model.getWeatherDescription() + "\n";

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    // --------Закрытие--------
    public static void CloseDB() throws SQLException {
        connection.close();
        statmt.close();
        resSet.close();
        System.out.println("Соединения закрыты");
    }

    public static void main(String[] args) {
        try {
            ReadDB(283134908);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
