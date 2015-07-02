package mnomoko.android.com.happyweather.data.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by mnomoko on 29/06/15.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    private static String URL_ICON = "http://openweathermap.org/img/w/";

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
        this.bmImage.setVisibility(View.INVISIBLE);
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = URL_ICON + urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        bmImage.setVisibility(View.INVISIBLE);
    }
}
//public class DownloadImageTask {
//
//    public static Bitmap getImageWeather(String s) {
//
//        String url = "http://openweathermap.org/img/w/";
//        url += s;
//
//        Bitmap mIcon11 = null;
//        try {
//            InputStream in = new URL(url).openStream();
//            mIcon11 = BitmapFactory.decodeStream(in);
//        } catch (Exception e) {
//            Log.e("Error", e.getMessage());
//            e.printStackTrace();
//        }
//        return mIcon11;
//    }
//}
