package mnomoko.android.com.happyweather.model;

/**
 * Created by mnomoko on 02/07/15.
 */
public class Weather {

    String icon;
    String city;
    int min;
    int max;
    String date;

    public Weather(String ic, String ci, int mi, int ma, String d) {
        icon = ic;
        city = ci;
        min = mi;
        max = ma;
        date = d;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getImage() {
        return icon;
    }

    public String getDate() {
        return date;
    }
}
