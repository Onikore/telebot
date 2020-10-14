package errors;

public class MyErrors {
    private MyErrors() {
    }

    public static String configNotFound() {
        return "\nСОЗДАЙТЕ ФАЙЛ config.properties ПО ПУТИ src/main/resources/" +
                "\n ВНЕСИТЕ СВОЙ ДАННЫЕ ДЛЯ БОТА: ИМЯ:\n\t BOT_NAME = ИМЯ БОТА" +
                "\nТОКЕН:\n\tBOT_TOKEN=ТОКЕН БОТА " +
                "\nAPI ДЛЯ OPENWEATHERMAP API:\n\t BOT_WEATHER_API = ВАШ API";
    }
}
