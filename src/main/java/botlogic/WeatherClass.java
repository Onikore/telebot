package botlogic;

import models.weatherModel;
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

    public static String get_json_string(Double x, Double y) throws IOException {
        if (x == null || y == null) return "";
        String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&appid=%s&lang=ru&units=metric",
                x,
                y,
                GetBotInfo.getWeatherAPI());

        return Jsoup.connect(url).ignoreContentType(true).execute().body();
    }


    public static weatherModel jsonToCurrent(String jsonData) throws ParseException {
        weatherModel model = new weatherModel();
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

            tempData.put("eve", allTemp.get("eve"));
            tempData.put("day", allTemp.get("day"));
            tempData.put("morn", allTemp.get("morn"));
            tempData.put("night", allTemp.get("night"));
            tempData.put("description", desc.get("description"));
            tempData.toJSONString();

            String new_date = formater.format(date);
            recievData.put(new_date, tempData);
        }
        recievData.toJSONString();
        return String.valueOf(recievData);
    }


    public static weatherModel weatherNow(String jsonData) throws ParseException {

        weatherModel model = new weatherModel();
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


    public static weatherModel weatherTomorrow(String jsonData) throws ParseException {
        weatherModel model = new weatherModel();

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
