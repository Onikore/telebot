package somelogic;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static consts.Constants.PATH;
import static errors.MyErrors.configNotFound;

public class GetBotInfo {
    private static final Logger log = Logger.getLogger(GetBotInfo.class.getName());

    private GetBotInfo() {
    }

    public static String getInfo(String field) {
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream(PATH);
            prop.load(fileInputStream);
        } catch (IOException e) {
            log.log(Level.WARNING, configNotFound());
        }
        return prop.getProperty(field);
    }
}
