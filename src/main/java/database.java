import java.sql.SQLException;

public class database {
    public static void writeToDb(String username, long userId, float lantitude, float longtitude) throws ClassNotFoundException, SQLException {
        database_logic.Connect();
        database_logic.CreateDB();
        database_logic.WriteDB(username,userId,lantitude,longtitude);
        database_logic.CloseDB();
    }
    public static String readDB(long userId) throws SQLException, ClassNotFoundException {
        database_logic.Connect();
        database_logic.CreateDB();
        database_logic.ReadDB(userId);
        database_logic.CloseDB();
    }
}
