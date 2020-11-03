package base_logic;

import java.sql.*;
import java.util.logging.Logger;

import static consts.Constants.CLASS;
import static consts.Constants.URL;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

public class DatabaseManagementSystem {
    private final Logger logger = Logger.getLogger(DatabaseManagementSystem.class.getName());
    private Connection con;
    private PreparedStatement stmt;
    private ResultSet rs;
    private final long userId;
    private float lantitude;
    private float longtitude;

    public DatabaseManagementSystem(long userId) {
        connect();
        createDB();
        this.userId = userId;
    }

    public DatabaseManagementSystem(long userId, float lantitude, float longtitude) {
        connect();
        createDB();
        this.userId = userId;
        this.lantitude = lantitude;
        this.longtitude = longtitude;
    }

    // public static void main(String[] args) {
    //     DatabaseManagementSystem dms = new DatabaseManagementSystem(123, 123, 123);
    //     dms.getUserCords();
    // }

    private void connect() {
        try {
            Class.forName(CLASS);
            con = DriverManager.getConnection(URL);
            logger.log(INFO, "База подключена");
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(WARNING, "ОШИБКА ПОДКЛЮЧЕНИЯ К БАЗЕ ДАННЫХ ", e);
        }
    }

    private void createDB() {
        try (Statement a = con.createStatement()) {
            a.execute("CREATE TABLE if not exists 'users_data' " +
                    "('user_id' INT PRIMARY KEY UNIQUE , " +
                    "'user_latitude' DOUBLE, " +
                    "'user_longtitude' DOUBLE);");
            logger.log(INFO, "Таблица создана");
        } catch (SQLException e) {
            logger.log(WARNING, "ОШИБКА СОЗДАНИЯ БАЗЫ ДАННЫХ ", e);
        } finally {
            close();
        }
    }

    public void setUserCords() {
        String insertstr = "INSERT INTO users_data (user_id,user_latitude,user_longtitude) VALUES (?,?,?);";
        String check = "SELECT COUNT(*) FROM users_data WHERE user_id = ?;";
        try {
        	//TODO фикс nullpointerexception
            stmt =con.prepareStatement(check);
            stmt.setLong(1, userId);
            rs = stmt.executeQuery();
            if (rs.getLong(1) == 0) {
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

    public double[] getUserCords() {
        String checkWheather = "SELECT user_latitude,user_longtitude FROM users_data WHERE user_id=?;";
        double[] coords = new double[2];
        double userX = 0;
        double userY = 0;
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


