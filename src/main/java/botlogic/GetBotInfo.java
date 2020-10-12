package botlogic;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GetBotInfo {

    public static String getName() {
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream("src/main/resources/config.properties");
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty("BOT_NAME");
    }

    public static String getToken() {
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream("src/main/resources/config.properties");
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty("BOT_TOKEN");
    }

    public static String getWeatherAPI() {
        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream("src/main/resources/config.properties");
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty("BOT_WEATHER_API");
    }
}
