import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class WeatherClass {

    private static String BOT_WEATHER_API;
//    private static String weather_state = "";
//    private String temp_now = "";
//    private String temp_min = "";
//    private String temp_max = "";

    /**
     *
     * @param x - latitude(широта)
     * @param y - longtitude(долгота)
     */
    public static void init(String x, String y) throws IOException {

        FileInputStream fileInputStream;
        Properties prop = new Properties();
        try {
            fileInputStream = new FileInputStream("src/main/resources/config.properties");
            prop.load(fileInputStream);
            BOT_WEATHER_API = prop.getProperty("BOT_WEATHER_API");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String url = "http://api.openweathermap.org/data/2.5/forecast?lat="+x+"&lon="+y+"&appid="+BOT_WEATHER_API+"&lang=ru&units=metric";
        String inputLine;
        StringBuilder response = new StringBuilder();
//        выполняем гет запрос по нашей ссылке
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.print(response);
    }

    public static void main(String[] args) throws IOException {
        init("20","20");
    }
}
