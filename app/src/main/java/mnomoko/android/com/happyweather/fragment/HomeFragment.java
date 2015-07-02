package mnomoko.android.com.happyweather.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.adapters.DailyAdapter;
import mnomoko.android.com.happyweather.data.loader.DataLoader;
import mnomoko.android.com.happyweather.data.loader.DownloadImageTask;
import mnomoko.android.com.happyweather.model.Weather;

/**
 * Created by mnomoko on 28/06/15.
 */
public class HomeFragment extends Fragment {

    SharedPreferences sharedpreferences;
    FragmentManager fm;
    ImageView imgViewWeather;
    TextView tvNameDegres;
    TextView tvNameCity;
    TextView tvNameMinDegres;
    TextView tvNameMaxDegres;
    TextView tvNameHumidity;
    TextView tvNameWind;
    ListView lvDaily;
    View root;
    RelativeLayout layout;

    List<Weather> weathers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.home_fragment, container, false);

        layout = (RelativeLayout) root.findViewById(R.id.background);
        //@TODO
        //Show layout with widget

        fm = getChildFragmentManager();
        imgViewWeather = (ImageView) root.findViewById(R.id.imgViewWeather);
        tvNameCity = (TextView) root.findViewById(R.id.tvNameCity);
//        /*int tvNameCityHeight = */tvNameCity.getLayoutParams().height *= 3;
//        /*int tvNameCityWidth = */tvNameCity.getLayoutParams().width *= 3;
        tvNameDegres = (TextView) root.findViewById(R.id.tvNameDegrees);
        tvNameDegres.setTextSize(tvNameDegres.getTextSize() * (3/2));
        tvNameMinDegres = (TextView) root.findViewById(R.id.tvNameMinDegres);
        tvNameMaxDegres = (TextView) root.findViewById(R.id.tvNameMaxDegres);
        tvNameHumidity = (TextView) root.findViewById(R.id.tvNameHumidity);
        tvNameWind = (TextView) root.findViewById(R.id.tvNameWind);

        lvDaily = (ListView) root.findViewById(R.id.lvDaily);
        weathers = new ArrayList<Weather>();

        sharedpreferences = getActivity().getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);
        String favo = sharedpreferences.getString(DrawerActivity.APP_DATA_LIVING_CITY, null);

        Log.e("SharedPreferences", sharedpreferences.getAll().toString());

        if(favo == null) {
            //show a dialog which add a city in favo
            AddFavoriteFragment alertdFragment = new AddFavoriteFragment(new YourDialogFragmentDismissHandler());
//            Log.e("HomeFragment.class", fm.toString());
            alertdFragment.show(fm, getResources().getString(R.string.favorite));
        }
        else {
//            tvNameDegres.setText(favo);
            new LaunchRequest((DrawerActivity)getActivity()).execute(favo);
        }

        return root;
    }

    public class LaunchRequest extends AsyncTask<String, String, String> {

        /** application context. */
        private Context context;

        private ProgressDialog dialog;
        private DrawerActivity activity;
        public LaunchRequest(DrawerActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Chargement..");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String content = DataLoader.getWeatherCity(strings[0]);

            return content;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject object = new JSONObject(s);

                String name = object.getJSONObject("city").getString("name");

                JSONArray list = object.getJSONArray("list");
                JSONObject first = list.getJSONObject(0);

                String temp = first.getJSONObject("temp").getString("day");
                String tempMin = first.getJSONObject("temp").getString("min");
                String tempMax = first.getJSONObject("temp").getString("max");
                String pressure = first.getString("pressure");
                String wind = first.getString("speed");
                String humidity = first.getString("humidity");

                String icon = first.getJSONArray("weather").getJSONObject(0).getString("icon");
                String main = first.getJSONArray("weather").getJSONObject(0).getString("main"); //FOR WALLPAPER
                new DownloadImageTask((ImageView) root.findViewById(R.id.imgViewWeather)).execute(icon + ".png");

                tvNameCity.setText(name);
                tvNameDegres.setText(temp + " C°");
                tvNameMinDegres.setText("min : " + tempMin + " C°");
                tvNameMaxDegres.setText("max : " + tempMax + " C°");
                tvNameHumidity.setText(getResources().getString(R.string.humidity) + " : " + humidity);
                tvNameWind.setText(getResources().getString(R.string.wind) + " : " + wind);

//                String name = object.getString("name");
//                tvNameCity.setText(name);
//                String tempMin = object.getJSONObject("main").getString("temp_min");
//                String tempMax = object.getJSONObject("main").getString("temp_max");
//                String humidity = object.getJSONObject("main").getString("humidity");
//                String wind = object.getJSONObject("wind").getString("speed");

                SimpleDateFormat sdf = new SimpleDateFormat("EEEE d");

                for(int i = 1; i < list.length(); i++) {
                    JSONObject json = list.getJSONObject(i);

                    String lvIcon = json.getJSONArray("weather").getJSONObject(0).getString("icon");
                    String lvCity = name;
                    String lvTempMin = json.getJSONObject("temp").getString("min");
                    String lvTempMax = json.getJSONObject("temp").getString("max");

                    long timestamp = json.getLong("dt") * 1000L;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(timestamp);
                    String lvDate = sdf.format(cal.getTime());

                    weathers.add(new Weather(lvIcon, lvCity, Double.valueOf(lvTempMin).intValue(),
                            Double.valueOf(lvTempMax).intValue(), lvDate));
                }
                lvDaily.setAdapter(new DailyAdapter(getActivity(), weathers));

//                WallpaperManager myWallpaperManager
//                        = WallpaperManager.getInstance(getActivity().getApplicationContext());
//                    myWallpaperManager.setResource(R.drawable.sun);

//                layout.setBackground(HomeFragment.getDrawable(getActivity(), R.drawable.sun));
                HomeFragment.setBackgroundView(layout, getActivity(), R.drawable.sun);

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            catch (JSONException e) {
                Log.e(getActivity().getLocalClassName(), "_"+e.getMessage());
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(Context context, int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resource, null);
        }

        return context.getResources().getDrawable(resource);
    }

    public static void setBackgroundView(RelativeLayout layout, Context context, int drawable) {

        if (Build.VERSION.SDK_INT >= 16) {
            layout.setBackground(HomeFragment.getDrawable(context, drawable));
        }else{
            layout.setBackgroundDrawable(HomeFragment.getDrawable(context, drawable));
        }

    }

    private class YourDialogFragmentDismissHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // refresh your textview's here
            sharedpreferences = getActivity().getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);
            String favo = sharedpreferences.getString(DrawerActivity.APP_DATA_LIVING_CITY, null);

            if(favo == null) {
                //show a dialog which add a city in favo
                AddFavoriteFragment alertdFragment = new AddFavoriteFragment(new YourDialogFragmentDismissHandler());
//            Log.e("HomeFragment.class", fm.toString());
                alertdFragment.show(fm, getResources().getString(R.string.favorite));
            }
            else {
                new LaunchRequest((DrawerActivity)getActivity()).execute(favo);
            }

        }
    }

}
