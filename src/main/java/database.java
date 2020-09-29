import java.sql.SQLException;

public class database {
    public static void writeToDb(String username, Long userId, float lantitude, float longtitude) throws ClassNotFoundException, SQLException {
        database_logic.Connect();
        database_logic.CreateDB();
        database_logic.WriteDB(username,userId,lantitude,longtitude);
        database_logic.ReadDB();
        database_logic.CloseDB();
    }
}
