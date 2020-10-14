package somelogic;

import models.WeatherModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeatherClass {
    private static final String DAYFORMAT = "dd-MMMM-yyyy";

    private WeatherClass() {
    }

    public static String getJsonString(Double x, Double y) throws IOException {
        if (x == null || y == null) return "";
        String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&appid=%s&lang=ru&units=metric",
                x,
                y,
                GetBotInfo.getInfo("BOT_WEATHER_API"));

        return Jsoup.connect(url).ignoreContentType(true).execute().body();
    }


    public static WeatherModel jsonToCurrent(String jsonData) throws ParseException {
        WeatherModel model = new WeatherModel();
        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONObject currentData = (JSONObject) parsedData.get("current");

        SimpleDateFormat formater = new SimpleDateFormat(DAYFORMAT);
        long time = (long) currentData.get("dt");
        Date date = new Date(time * 1000);
        String newDate = formater.format(date);

        JSONArray weather = (JSONArray) currentData.get("weather");
        JSONObject description = (JSONObject) weather.get(0);

        model.setDatetime(newDate);
        model.setTemp(currentData.get("temp"));
        model.setFeelsLike(currentData.get("feels_like"));
        model.setHumidity(currentData.get("humidity"));
        model.setWeatherDescription((String) description.get("description"));

        return model;
    }


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
            SimpleDateFormat formater = new SimpleDateFormat(DAYFORMAT);
            Date date = new Date(time * 1000);

            tempData.put("eve", allTemp.get("eve"));
            tempData.put("day", allTemp.get("day"));
            tempData.put("morn", allTemp.get("morn"));
            tempData.put("night", allTemp.get("night"));
            tempData.put("description", desc.get("description"));
            tempData.toJSONString();

            String newDate = formater.format(date);
            recievData.put(newDate, tempData);
        }
        recievData.toJSONString();
        return String.valueOf(recievData);
    }


    public static WeatherModel weatherNow(String jsonData) throws ParseException {

        WeatherModel model = new WeatherModel();
        SimpleDateFormat formater = new SimpleDateFormat(DAYFORMAT);
        Date date = new Date();
        String newDate = formater.format(date);

        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONObject currentDay = (JSONObject) parsedData.get(newDate);

        model.setEve(currentDay.get("eve"));
        model.setNight(currentDay.get("night"));
        model.setWeatherDescription((String) currentDay.get("description"));
        model.setDay(currentDay.get("day"));
        model.setMorn(currentDay.get("morn"));

        return model;
    }


    public static WeatherModel weatherTomorrow(String jsonData) throws ParseException {
        WeatherModel model = new WeatherModel();

        SimpleDateFormat formater = new SimpleDateFormat(DAYFORMAT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();

        String newDate = formater.format(date);

        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONObject currentDay = (JSONObject) parsedData.get(newDate);

        model.setEve(currentDay.get("eve"));
        model.setNight(currentDay.get("night"));
        model.setWeatherDescription((String) currentDay.get("description"));
        model.setDay(currentDay.get("day"));
        model.setMorn(currentDay.get("morn"));

        return model;
    }

}
