import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.FileReader;
import java.io.IOException;



public class JsonParseTest {

    private static final String filePath = "src/main/java/forecast.json";

    public static void main(String[] args) {
        try {
            FileReader reader = new FileReader(filePath);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            JSONArray list_array = (JSONArray) jsonObject.get("list");

            for (Object key : list_array) {
                JSONObject innerObj = (JSONObject) key;

                JSONArray weather_array = (JSONArray) innerObj.get("weather");

                for (Object weather_key : weather_array) {
                    JSONObject d = (JSONObject) weather_key;

                    System.out.println(d.get("description"));
                }
            }
        } catch (IOException | NullPointerException | ParseException ex) {
            ex.printStackTrace();
        }
    }
}
