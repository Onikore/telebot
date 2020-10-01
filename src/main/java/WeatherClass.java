import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;


import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

        String url ="https://api.openweathermap.org/data/2.5/onecall?lat=" + x + "&lon=" + y + "&appid=" + BOT_WEATHER_API + "&lang=ru&units=metric";

        return Jsoup.connect(url).ignoreContentType(true).execute().body();
    }

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
        model.setTemp((double)(long) currentData.get("temp"));
        model.setFeelsLike((Double) currentData.get("feels_like"));
        model.setHumidity((double) (long) currentData.get("humidity"));
        model.setWeatherDescription((String) description.get("description"));

        return model;
    }

    public static JSONObject jsonToHourly(String jsonData) throws ParseException {
        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(jsonData);
        JSONArray dailyData = (JSONArray) parsedData.get("daily");

        JSONObject recievData = new JSONObject();

        for (Object dayKey: dailyData){
            JSONObject tempData = new JSONObject();

            JSONObject dayData = (JSONObject) dayKey;
            JSONObject allTemp = (JSONObject) dayData.get("temp");

            JSONArray weather = (JSONArray) dayData.get("weather");
            JSONObject desc = (JSONObject) weather.get(0);

            long time = (long) dayData.get("dt");

            SimpleDateFormat formater = new SimpleDateFormat("dd-MMMM-yyyy");
            Date date = new Date(time * 1000);
            String new_date = formater.format(date);

            String eve = String.valueOf(allTemp.get("eve"));
            String day = String.valueOf(allTemp.get("day"));
            String morn = String.valueOf(allTemp.get("morn"));
            String night = String.valueOf(allTemp.get("night"));
            String description = String.valueOf(desc.get("description"));

            tempData.put("eve",eve);
            tempData.put("day",day);
            tempData.put("morn",morn);
            tempData.put("night",night);
            tempData.put("description",description);
            tempData.toJSONString();

            recievData.put(new_date,tempData);
        }
        recievData.toJSONString();
        return recievData;
    }

//    public static void main(String[] args) throws IOException, ParseException {
//        System.out.print(jsonToHourly(get_json_string("56.85","60.61")));
//
//    }
}
