import java.sql.SQLException;

public class database {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        database_logic.Connect();
        database_logic.CreateDB();
        database_logic.WriteDB();
        database_logic.ReadDB();
        database_logic.CloseDB();
    }
}
