package base_logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static consts.Constants.PATH;
import static errors.MyErrors.configNotFound;

public class Config {
    private static final Logger log = Logger.getLogger(Config.class.getName());

    private Config() {
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
