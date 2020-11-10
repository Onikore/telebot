package base_logic;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static consts.Constants.*;

public class WeatherParser {
    private final Logger logger = Logger.getLogger(WeatherParser.class.getName());
    private final Double x;
    private final Double y;
    private final String dataNow;
    private final String dailyData;

    public WeatherParser(double[] cords) throws IOException, ParseException {
        this.x = cords[0];
        this.y = cords[1];
        this.dataNow = getJsonContent();
        this.dailyData = getParsedStructure();
    }

    private String normalDate(long sec) {
        SimpleDateFormat formater = new SimpleDateFormat(DAYFORMAT);
        Date date = new Date(sec * 1000);
        return formater.format(date);
    }

    private String getJsonContent() throws IOException {
        if (x == null || y == null) throw new IllegalStateException("Unexpected value");
        String url = String.format("https://api.openweathermap.org/data/2.5/onecall?lat=%s&lon=%s&appid=%s&lang=ru&units=metric",
                x,
                y,
                Config.getInfo("BOT_WEATHER_API"));
        return Jsoup.connect(url).ignoreContentType(true).execute().body();
    }

    private String getParsedStructure() throws ParseException {
        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(dataNow);
        JSONArray daily = (JSONArray) parsedData.get("daily");
        JSONObject recievData = new JSONObject();

        for (Object dayKey : daily) {
            JSONObject tempData = new JSONObject();

            JSONObject dayData = (JSONObject) dayKey;
            JSONObject allTemp = (JSONObject) dayData.get("temp");
            JSONArray weather = (JSONArray) dayData.get("weather");
            JSONObject desc = (JSONObject) weather.get(0);

            long unixsecs = (long) dayData.get("dt");

            tempData.put("eve", allTemp.get("eve"));
            tempData.put("day", allTemp.get("day"));
            tempData.put("morn", allTemp.get("morn"));
            tempData.put("night", allTemp.get("night"));
            tempData.put("description", desc.get("description"));

            recievData.put(normalDate(unixsecs), tempData);
        }
        recievData.toJSONString();
        return String.valueOf(recievData);
    }

    public String getNowForecast() throws ParseException {
        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(dataNow);
        JSONObject currentData = (JSONObject) parsedData.get("current");
        long unixsecs = (long) currentData.get("dt");

        JSONArray weather = (JSONArray) currentData.get("weather");
        JSONObject description = (JSONObject) weather.get(0);

        return NOW + normalDate(unixsecs) + "\n\n" +
                TEMPERATURE + currentData.get("temp") + " C\n" +
                FEELSLIKE + currentData.get("feels_like") + " C\n" +
                HUMIDITY + currentData.get("humidity") + " %\n" +
                DESCRIPTION + description.get("description") + "\n";
    }

    public String getTodayForecast() throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat(DAYFORMAT);
        String newDate = formater.format(new Date());

        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(dailyData);
        JSONObject currentDay = (JSONObject) parsedData.get(newDate);

        return TEMPERATURE + "\n\n" +
                EVE + currentDay.get("eve") + " C\n" +
                DAY + currentDay.get("day") + " C\n" +
                MORN + currentDay.get("morn") + " C\n" +
                NIGHT + currentDay.get("night") + " C\n" +
                DESCRIPTION + currentDay.get("description") + "\n";
    }

    public String getTomorrowForecast() throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat(DAYFORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String newDate = formater.format(calendar.getTime());

        JSONObject parsedData = (JSONObject) JSONValue.parseWithException(dailyData);
        JSONObject currentDay = (JSONObject) parsedData.get(newDate);

        return TEMPERATURE + "\n\n" +
                EVE + currentDay.get("eve") + " C\n" +
                DAY + currentDay.get("day") + " C\n" +
                MORN + currentDay.get("morn") + " C\n" +
                NIGHT + currentDay.get("night") + " C\n" +
                DESCRIPTION + currentDay.get("description") + "\n";
    }

    public String getForecast(String mode) {
        String result = "Что-то сломалось";
        try {
            switch (mode) {
                case "today":
                    result = getTodayForecast();
                    break;
                case "now":
                    result = getNowForecast();
                    break;
                case "tomorrow":
                    result = getTomorrowForecast();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + mode);
            }
        } catch (ParseException e) {
            logger.log(Level.WARNING, "Ошибка ", e);
        }
        return result;
    }

}
