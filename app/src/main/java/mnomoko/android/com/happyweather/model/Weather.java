package mnomoko.android.com.happyweather.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mnomoko on 02/07/15.
 */
public class Weather implements Parcelable {

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

    protected Weather(Parcel in) {
        icon = in.readString();
        city = in.readString();
        min = in.readInt();
        max = in.readInt();
        date = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(icon);
        dest.writeString(city);
        dest.writeInt(min);
        dest.writeInt(max);
        dest.writeString(date);
    }

    public static final Creator<Weather> CREATOR = new Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel in) {
            return new Weather(in);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };
}
