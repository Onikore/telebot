import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

public class WeatherClass {

    private static String BOT_WEATHER_API;

    /**
     * @param x - latitude(широта)
     * @param y - longtitude(долгота)
     * @return строка(json файл с данными
     */
    public static String get_json_string(String x, String y) throws IOException {
        if (x == null || y == null) return "";

        FileInputStream fileInputStream;
        Properties prop = new Properties();
        fileInputStream = new FileInputStream("src/main/resources/config.properties");
        prop.load(fileInputStream);
        BOT_WEATHER_API = prop.getProperty("BOT_WEATHER_API");

        String url ="https://api.openweathermap.org/data/2.5/onecall?lat=" + x + "&lon=" + y + "&appid=" + BOT_WEATHER_API + "&lang=ru&exclude=daily&units=metric";

        return Jsoup.connect(url).ignoreContentType(true).execute().body();
    }

    public static JSONObject parserCurrentWeather(String response) throws ParseException {
        JSONObject weatherJson = (JSONObject) JSONValue.parseWithException(response);
        JSONObject currentWeather = (JSONObject) weatherJson.get("current");

        JSONArray currentDescrip = (JSONArray) currentWeather.get("weather");
        JSONObject descript = (JSONObject) currentDescrip.get(0);

        JSONArray jsonHourlyWeather = new JSONArray();
        JSONObject jsonCurrentWeather = new JSONObject();
        JSONObject sendWeather = new JSONObject();

// получили погоду в настоящее время
        Long currentTemp = (Long) currentWeather.get("temp");
        String currentDescription = (String) descript.get("description");
//        получили погоду на день
        JSONArray hourlyWeather = (JSONArray) weatherJson.get("hourly");
        //дикие манипуляции с джсонами
        for (Object hourKey : hourlyWeather) {
            JSONObject hourData = (JSONObject) hourKey;
            JSONObject tempData = new JSONObject();

            long time = (long) hourData.get("dt");
            Date date = new Date(time * 1000);

            JSONArray hourDesc = (JSONArray) hourData.get("weather");
            JSONObject hourTemp = (JSONObject) hourDesc.get(0);

            int timeHour = date.getHours(); // to json
            Object tempHour = hourData.get("temp");
            String description = (String) hourTemp.get("description");
            tempData.put("datetime", timeHour);
            tempData.put("temp", tempHour);
            tempData.put("description", description);
            jsonHourlyWeather.add(tempData);
        }

        jsonCurrentWeather.put("temp", currentTemp);
        jsonCurrentWeather.put("description", currentDescription);
        jsonCurrentWeather.toJSONString();
        jsonHourlyWeather.toJSONString();
        sendWeather.put("current", jsonCurrentWeather);
        sendWeather.put("hourly", jsonHourlyWeather);
        sendWeather.toJSONString();
        return sendWeather;
    }

    public static void main(String[] args) throws IOException, ParseException {
        System.out.print(parserCurrentWeather(get_json_string("56.85","60.61")));

    }
}
