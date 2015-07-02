package mnomoko.android.com.happyweather.model;

/**
 * Created by mnomoko on 02/07/15.
 */
public class WeatherHour {

    String icon;
    String city;
    int temp;
    int humidity;
    float speed;
    int from;
    int to;

    public WeatherHour(String ic, String ci, int te, int h, float s, int f, int t) {
        icon = ic;
        city = ci;
        temp = te;
        humidity = h;
        speed = s;
        from = f;
        to = t;
    }

}
