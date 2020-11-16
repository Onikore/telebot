package base_logic;

import java.sql.*;
import java.util.logging.Logger;

import static consts.Constants.CLASS;
import static consts.Constants.URL;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

public class UserRepository {
    private final Logger logger = Logger.getLogger(UserRepository.class.getName());
    private Connection con;
    private PreparedStatement stmt;
    private ResultSet rs;

    private void connect() {
        try {
            Class.forName(CLASS);
            con = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(WARNING, "ОШИБКА ПОДКЛЮЧЕНИЯ К БАЗЕ ДАННЫХ ", e);
        }
    }

    public void createDB() {
        String createSql = "CREATE TABLE if not exists 'users_data' " +
                "(user_id INT PRIMARY KEY UNIQUE , " +
                "user_latitude DOUBLE, " +
                "user_longtitude DOUBLE);";

        try (Statement a = con.createStatement()) {
            a.execute(createSql);
            logger.log(INFO, "Таблица создана");
        } catch (SQLException e) {
            logger.log(WARNING, "ОШИБКА СОЗДАНИЯ БАЗЫ ДАННЫХ ", e);
        }
    }

    private boolean checkState(long userId) {
        String check = "SELECT COUNT(*) FROM users_data WHERE user_id = ?;";
        boolean result = false;

        try {
            stmt = con.prepareStatement(check);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            result = rs.getLong(1) == 0;
        } catch (SQLException e) {
            logger.log(WARNING, "ОШИБКА ПРОВЕРКИ СУЩЕСТВОВАНИЯ ПОЛЬЗОВАТЕЛЯ", e);
        }
        return result;
    }

    public void setUserCords(long userId, float lantitude, float longtitude) {
        String insertstr = "INSERT INTO users_data (user_id,user_latitude,user_longtitude) VALUES (?,?,?);";
        connect();
        try {
            if (checkState(userId)) {
                stmt = con.prepareStatement(insertstr);
                stmt.setLong(1, userId);
                stmt.setFloat(2, lantitude);
                stmt.setFloat(3, longtitude);
                stmt.execute();
            } else {
                logger.log(INFO, "Пользователь с ID {0}  уже сущетвует", userId);
            }
        } catch(SQLException e){
            logger.log(WARNING, "ОШИБКА СОЗДАНИЯ ПОЛЬЗОВАТЕЛЯ", e);
        } finally{
            close();
        }
    }

    public double[] getUserCords(long userId) {
        String checkWheather = "SELECT user_latitude,user_longtitude FROM users_data WHERE user_id=?;";
        double[] coords = new double[2];
        double userX = 0;
        double userY = 0;
        connect();
        try {
            stmt = con.prepareStatement(checkWheather);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                userX = rs.getDouble("user_latitude");
                userY = rs.getDouble("user_longtitude");
            }
            coords = new double[]{userX, userY};
        } catch (SQLException e) {
            logger.log(WARNING, "ОШИБКА ПОЛУЧЕНИЯ КООРДИНАТ ", e);
        } finally {
            close();
        }
        return coords;
    }

    private void close() {
        try {
            if (con != null) con.close();
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        } catch (SQLException e) {
            logger.log(WARNING, "ОШИБКА ЗАКРЫТИЯ СОЕДИНЕНИЯ ", e);
        }
    }
}


