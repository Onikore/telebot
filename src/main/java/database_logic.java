import java.sql.*;

public class database_logic {

    public static Connection connection; // аналог connect
    public static Statement statmt; // аналог cursor
    public static ResultSet resSet; // аналог execute

    /**
     * Подключается к базе данных(далее БД)
     */
    public static void Connect() throws ClassNotFoundException, SQLException {
        connection = null;
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
                "('user_id' INTEGER PRIMARY KEY UNIQUE, " +
                "'user_latitude' DOUBLE, " +
                "'user_longtitude' DOUBLE);");
        System.out.println("Таблица создана или уже существует.");
    }

    /**
     * Метод записи данных в БД
     */
    public static void WriteDB() throws SQLException {
//        statmt.execute("INSERT INTO 'users' ('name', 'phone') VALUES ('Petya', 125453); ");
        System.out.println("Таблица заполнена");
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
