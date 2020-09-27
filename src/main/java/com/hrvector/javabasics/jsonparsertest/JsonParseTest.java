package com.hrvector.javabasics.jsonparsertest;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;


public class JsonParseTest {

    private static final String filePath = "src/main/java/forecast.json";

    public static void main(String[] args) {
        try {
            // считывание файла JSON
            FileReader reader = new FileReader(filePath);
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
            // получение строки из объекта
            String firstName = (String) jsonObject.get("firstname");
            System.out.println("The first name is: " + firstName);
            // получение номера из объекта
            long id = (long) jsonObject.get("id");
            System.out.println("The id is: " + id);
            // получение массива
            JSONArray lang = (JSONArray) jsonObject.get("languages");
            // берем элементы массива
            for (int i = 0; i < lang.size(); i++) {
                System.out.println("The " + i + " element of the array: " + lang.get(i));
            }
            Iterator i = lang.iterator();
            // берем каждое значение из массива json отдельно
            while (i.hasNext()) {
                JSONObject innerObj = (JSONObject) i.next();
                System.out.println("language " + innerObj.get("lang") +
                        " with level " + innerObj.get("knowledge"));
            }
            // обрабатываем структуру в объекте
            JSONObject structure = (JSONObject) jsonObject.get("job");
            System.out.println("Into job structure, name: " + structure.get("name"));

        } catch (IOException | NullPointerException | ParseException ex) {
            ex.printStackTrace();
        }
    }
}
