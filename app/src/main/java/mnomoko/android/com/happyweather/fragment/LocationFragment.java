package mnomoko.android.com.happyweather.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.algorithme.AutoResizeTextView;
import mnomoko.android.com.happyweather.data.loader.DataLoader;

/**
 * Created by mnomoko on 28/06/15.
 */
public class LocationFragment extends DialogFragment {


    SharedPreferences sharedpreferences;
    FragmentManager fm;
    ImageView imgViewWeather;
    TextView tvNameDegres;
    AutoResizeTextView tvNameCity;
    TextView tvNameMinDegres;
    TextView tvNameMaxDegres;
    TextView tvNameHumidity;
    TextView tvNameWind;
    CheckBox checkbox;
    Button btnChangeLinvingCity;
    View root;
    RelativeLayout layout;

    String city;
    String favorites;

    String longitude;
    String latitude;

    public String weatherXML = null;

    private ProgressBar mProgressBar;

    public LocationFragment(String lon, String lat) {
        longitude = lon;
        latitude = lat;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        root = getActivity().getLayoutInflater().inflate(R.layout.location_fragment, null);

        //setContentView(R.layout.activity_location);

//        View root = inflater.inflate(R.layout.location_fragment, null);


        layout = (RelativeLayout) root.findViewById(R.id.background);

        city = null;

        sharedpreferences = getActivity().getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);
        favorites = sharedpreferences.getString(DrawerActivity.APP_DATA_FAVORITES_CITY, "");

//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putString(DrawerActivity.APP_DATA_FAVORITES_CITY, "");
//        editor.commit();

        fm = getChildFragmentManager();
        imgViewWeather = (ImageView) root.findViewById(R.id.imgViewWeather);
        tvNameCity = (AutoResizeTextView) root.findViewById(R.id.tvNameCity);
//        /*int tvNameCityHeight = */tvNameCity.getLayoutParams().height *= 3;
//        /*int tvNameCityWidth = */tvNameCity.getLayoutParams().width *= 3;
        tvNameDegres = (TextView) root.findViewById(R.id.tvNameDegrees);
        tvNameDegres.setTextSize(tvNameDegres.getTextSize() * (3/2));
        tvNameMinDegres = (TextView) root.findViewById(R.id.tvNameMinDegres);
        tvNameMaxDegres = (TextView) root.findViewById(R.id.tvNameMaxDegres);
        tvNameHumidity = (TextView) root.findViewById(R.id.tvNameHumidity);
        tvNameWind = (TextView) root.findViewById(R.id.tvNameWind);

        checkbox = (CheckBox) root.findViewById(R.id.checkBox1);
//        checkbox.setChecked(false);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(checkbox.isChecked()) {

                    favorites += city + ";";
                    editor.putString(DrawerActivity.APP_DATA_FAVORITES_CITY, favorites);
                    editor.commit();
                }
                else {
                    String temp = "";
                    for(String s : favorites.split(";")) {

                        if(!s.equals(city)) {
                            temp += s + ";";
                        }
                        editor.putString(DrawerActivity.APP_DATA_FAVORITES_CITY, temp);
                        editor.commit();
                    }
                }
            }
        });


        boolean connect = ((DrawerActivity)getActivity()).checkConnection();
        if(!connect) {

            ((DrawerActivity) getActivity()).noConnection();
//            ((DrawerActivity) getActivity()).showConnectionError();
        }
        else {
            new LaunchRequest((DrawerActivity) getActivity()).execute(longitude, latitude);
        }


        builder.setView(root);

        return builder
                // Set Dialog Icon
                .setIcon(R.drawable.ic_launcher)
                .setMessage(R.string.localisation)
                .create();

    }

    public class LaunchRequest extends AsyncTask<String, String, String> {

        /** application context. */
        private Context _context;

        private ProgressDialog dialog;
        private DrawerActivity activity;
        public LaunchRequest(DrawerActivity activity) {
            this.activity = activity;
            _context = activity;
            dialog = new ProgressDialog(_context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Chargement..");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String lon = strings[0];
            String lat = strings[1];

            String content = DataLoader.getWeatherLocation(lon, lat);
//            DataLoader.getWeatherCity(strings[0]);

            return content;
        }

        @Override
        protected void onPostExecute(String s) {

            final String json = s;
            /**
             * Update list ui after process finished.
             */
            //TODO update list ui here, use
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data
                     */

                    try {
                        JSONObject object = new JSONObject(json);

                        String name = object.getString("name");

                        city = name + "," + object.getJSONObject("sys").getString("country");
                        Log.e("HomeFragment", "favorites = " + favorites);
                        if(favorites != null) {
                            List<String> array = Arrays.asList(favorites.split(";"));
                            for(String a : array) {
                                Log.e("HomeFragment", "fav = "+a);
                                if (a.equals(city)){
                                    checkbox.setChecked(true);
                                }
                                else {
                                    checkbox.setChecked(false);
                                }
                            }
//                    if(favorites != "") {
//                    }
                        }

                        String temp = object.getJSONObject("main").getString("temp");
                        String tempMin = object.getJSONObject("main").getString("temp_min");
                        String tempMax = object.getJSONObject("main").getString("temp_max");
                        String humidity = object.getJSONObject("main").getString("humidity");
                        String wind = object.getJSONObject("wind").getString("speed");

                        String icon = object.getJSONArray("weather").getJSONObject(0).getString("icon");
                        String main = object.getJSONArray("weather").getJSONObject(0).getString("main"); //FOR WALLPAPER
                        //new DownloadImageTask((ImageView) root.findViewById(R.id.imgViewWeather)).execute(icon + ".png");
                        imgViewWeather.setImageResource(_context.getResources().getIdentifier("_"+icon, "drawable", _context.getPackageName()));

                        tvNameCity.setText(city, TextView.BufferType.EDITABLE);
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

//                WallpaperManager myWallpaperManager
//                        = WallpaperManager.getInstance(getActivity().getApplicationContext());
//                    myWallpaperManager.setResource(R.drawable.sun);

//                layout.setBackground(HomeFragment.getDrawable(getActivity(), R.drawable.sun));
//                        DrawerActivity.setBackgroundView(layout, getActivity(), R.drawable.sun);

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                    catch (JSONException e) {
                        Log.e(getActivity().getLocalClassName(), "_"+e.getMessage());
                    }

                }
            });

        }
    }
}
