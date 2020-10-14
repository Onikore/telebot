package models;

public class WeatherModel {
    private String datetime;
    private Object temp;
    private Object humidity;
    private Object feelsLike;
    private String weatherDescription;
    private Object eve;
    private Object night;
    private Object day;
    private Object morn;


    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Object getTemp() {
        return temp;
    }

    public void setTemp(Object temp) {
        this.temp = temp;
    }

    public Object getHumidity() {
        return humidity;
    }

    public void setHumidity(Object humidity) {
        this.humidity = humidity;
    }

    public Object getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(Object feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public Object getEve() {
        return eve;
    }

    public void setEve(Object eve) {
        this.eve = eve;
    }

    public Object getNight() {
        return night;
    }

    public void setNight(Object night) {
        this.night = night;
    }

    public Object getDay() {
        return day;
    }

    public void setDay(Object day) {
        this.day = day;
    }

    public Object getMorn() {
        return morn;
    }

    public void setMorn(Object morn) {
        this.morn = morn;
    }

}
