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
    public static void CreateDB() throws ClassNotFoundException, SQLException {
        statmt = connection.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users_data' " +
                "('user_id' VARCHAR PRIMARY KEY UNIQUE , " +
                "'user_name' VARCHAR ,"+
                "'user_latitude' DOUBLE, " +
                "'user_longtitude' DOUBLE);");
        System.out.println("Таблица создана или уже существует.");
    }

    /**
     * Метод записи данных в БД
     */
    public static void WriteDB(String username, Long userId, float lantitude, float longtitude) throws SQLException {
        String str = "INSERT INTO 'users_data' ('user_id', 'user_name','user_latitude','user_longtitude') VALUES (?,?,?,?);";
        PreparedStatement statement = connection.prepareStatement(str);
        try {
            statement.setInt(1, Math.toIntExact(userId));
            statement.setString(2, username);
            statement.setFloat(3, lantitude);
            statement.setFloat(4, longtitude);

            if (statement.executeUpdate() == 1){
                System.out.println("Таблица заполнена");
            }
        } catch (SQLException  e){
            e.printStackTrace();
        }
    }

    /**
     * Вывод всей БД в консоль
     */
    public static void ReadDB() throws ClassNotFoundException, SQLException {
        resSet = statmt.executeQuery("SELECT * FROM users_data");

        while (resSet.next()) {
            int user_id = resSet.getInt("user_id");
            String user_altitude = resSet.getString("user_altitude");
            String user_longtitude = resSet.getString("user_longtitude");
            System.out.println("ID = " + user_id);
            System.out.println("latitude = " + user_altitude);
            System.out.println("longtitude = " + user_longtitude);
            System.out.println();
        }
        System.out.println("Таблица выведена");
    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException {
        connection.close();
        statmt.close();
        resSet.close();
        System.out.println("Соединения закрыты");
    }
}
