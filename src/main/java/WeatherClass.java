import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class WeatherClass {

    /**
     * @param x - latitude(широта)
     * @param y - longtitude(долгота)
     * @return строка(json файл с данными
     */
    public static String get_json_string(Double x, Double y) throws IOException {
        if (x == null || y == null) return "";

        FileInputStream fileInputStream;
        Properties prop = new Properties();
        fileInputStream = new FileInputStream("src/main/resources/config.properties");
        prop.load(fileInputStream);
        String BOT_WEATHER_API = prop.getProperty("BOT_WEATHER_API");

        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + x + "&lon=" + y + "&appid=" + BOT_WEATHER_API + "&lang=ru&units=metric";

        return Jsoup.connect(url).ignoreContentType(true).execute().body();
    }

    /**
     * @param jsonData json строка с данными о погоде
     * @return возращает погоду в данный момент
     */
    public static Model jsonToCurrent(String jsonData) throws ParseException {
        Model model = new Model();
        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONObject currentData = (JSONObject) parsedData.get("current");

        SimpleDateFormat formater = new SimpleDateFormat("dd-MMMM-yyyy");
        long time = (long) currentData.get("dt");
        Date date = new Date(time * 1000);
        String new_date = formater.format(date);

        JSONArray weather = (JSONArray) currentData.get("weather");
        JSONObject description = (JSONObject) weather.get(0);

        model.setDatetime(new_date);
        model.setTemp(currentData.get("temp"));
        model.setFeelsLike(currentData.get("feels_like"));
        model.setHumidity(currentData.get("humidity"));
        model.setWeatherDescription((String) description.get("description"));

        return model;
    }

    /**
     * @param jsonData json строка с данными о погоде
     * @return возваращет json с удобной погодой на неделю
     */
    public static String jsonToDaily(String jsonData) throws ParseException {
        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONArray dailyData = (JSONArray) parsedData.get("daily");

        JSONObject recievData = new JSONObject();

        for (Object dayKey : dailyData) {
            JSONObject tempData = new JSONObject();

            JSONObject dayData = (JSONObject) dayKey;
            JSONObject allTemp = (JSONObject) dayData.get("temp");

            JSONArray weather = (JSONArray) dayData.get("weather");
            JSONObject desc = (JSONObject) weather.get(0);

            long time = (long) dayData.get("dt");
            SimpleDateFormat formater = new SimpleDateFormat("dd-MMMM-yyyy");
            Date date = new Date(time * 1000);

            String new_date = formater.format(date);

            Object eve = allTemp.get("eve");
            Object day = allTemp.get("day");
            Object morn = allTemp.get("morn");
            Object night = allTemp.get("night");
            String description = (String) desc.get("description");

            tempData.put("eve", eve);
            tempData.put("day", day);
            tempData.put("morn", morn);
            tempData.put("night", night);
            tempData.put("description", description);
            tempData.toJSONString();

            recievData.put(new_date, tempData);
        }
        recievData.toJSONString();
        return String.valueOf(recievData);
    }

    /**
     * @param jsonData json с данными о погоде
     * @return возваращет погоду на сегодня
     */
    public static Model weatherNow(String jsonData) throws ParseException {

        Model model = new Model();
        SimpleDateFormat formater = new SimpleDateFormat("dd-MMMM-yyyy");
        Date date = new Date();
        String new_date = formater.format(date);

        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONObject currentDay = (JSONObject) parsedData.get(new_date);

        model.setEve(currentDay.get("eve"));
        model.setNight(currentDay.get("night"));
        model.setWeatherDescription((String) currentDay.get("description"));
        model.setDay(currentDay.get("day"));
        model.setMorn(currentDay.get("morn"));

        return model;
    }

    /**
     * @param jsonData json на неделю полученный из jsonToDaily
     * @return погода на завтра
     */
    public static Model weatherTomorrow(String jsonData) throws ParseException {
        Model model = new Model();

        SimpleDateFormat formater = new SimpleDateFormat("dd-MMMM-yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();

        String new_date = formater.format(date);

        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONObject currentDay = (JSONObject) parsedData.get(new_date);

        model.setEve(currentDay.get("eve"));
        model.setNight(currentDay.get("night"));
        model.setWeatherDescription((String) currentDay.get("description"));
        model.setDay(currentDay.get("day"));
        model.setMorn(currentDay.get("morn"));

        return model;
    }

}
