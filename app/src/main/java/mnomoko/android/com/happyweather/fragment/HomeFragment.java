package mnomoko.android.com.happyweather.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.adapters.DailyAdapter;
import mnomoko.android.com.happyweather.algorithme.AutoResizeTextView;
import mnomoko.android.com.happyweather.data.loader.DataLoader;
import mnomoko.android.com.happyweather.model.Weather;

/**
 * Created by mnomoko on 28/06/15.
 */
public class HomeFragment extends Fragment {

    SharedPreferences sharedpreferences;
    FragmentManager fm;
    ImageView imgViewWeather;
    TextView tvNameDegres;
    AutoResizeTextView tvNameCity;
    TextView tvNameMinDegres;
    TextView tvNameMaxDegres;
    TextView tvNameHumidity;
    TextView tvNameWind;
    ListView lvDaily;
    CheckBox checkbox;
    Button btnChangeLinvingCity;
    View root;
    RelativeLayout layout;
    LinearLayout linearlayout;

    String city;
    String favorites;

    ArrayList<Weather> weathers;

    String name;
    String temp;
    String tempMin;
    String tempMax;
    String wind;
    String humidity;
    String icon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        root = inflater.inflate(R.layout.home_fragment, container, false);

        if(getActivity().getBaseContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // code to do for Portrait Mode
            layout = (RelativeLayout) root.findViewById(R.id.background);
            DrawerActivity.setBackgroundView(layout, getActivity(), R.drawable.sun);
        } else {
            // code to do for Landscape Mode
            linearlayout = (LinearLayout) root.findViewById(R.id.background);
            DrawerActivity.setBackgroundView(linearlayout, getActivity(), R.drawable.sun);
        }

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
        checkbox.setHighlightColor(Color.BLACK);
//        checkbox.setChecked(false);
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(checkbox.isChecked()) {
                    checkbox.setHighlightColor(Color.YELLOW);
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
        hideCheckbox();

        btnChangeLinvingCity = (Button) root.findViewById(R.id.btnChangeLinvingCity);
        btnChangeLinvingCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFavoriteFragment alertdFragment = new AddFavoriteFragment(new YourDialogFragmentDismissHandler());
                alertdFragment.show(fm, getResources().getString(R.string.favorite));
            }
        });

        lvDaily = (ListView) root.findViewById(R.id.lvDaily);
        weathers = new ArrayList<Weather>();

//        sharedpreferences = getActivity().getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);
        final String favo = sharedpreferences.getString(DrawerActivity.APP_DATA_LIVING_CITY, null);

        Log.e("SharedPreferences", sharedpreferences.getAll().toString());

        if(savedInstanceState == null) {

            boolean connect = ((DrawerActivity) getActivity()).checkConnection();
            if (connect) {

                showCheckbox();
                if (favo == null) {
                    //show a dialog which add a name in favo
                    AddFavoriteFragment alertdFragment = new AddFavoriteFragment(new YourDialogFragmentDismissHandler());

//            Log.e("HomeFragment.class", fm.toString());
                    alertdFragment.show(fm, getResources().getString(R.string.favorite));
                } else {
//            tvNameDegres.setText(favo);
                    new LaunchRequest((DrawerActivity) getActivity()).execute(favo);
                }
            } else {

                ((DrawerActivity) getActivity()).noConnection();
                hideCheckbox();
//                ((DrawerActivity) getActivity()).showConnectionError();
            }
        } else {

            icon = savedInstanceState.getString("ICON");
            imgViewWeather.setImageResource(getActivity().getResources().getIdentifier("_"+icon, "drawable", getActivity().getPackageName()));

            name = savedInstanceState.getString("NAME");
            tvNameCity.setText(name, AutoResizeTextView.BufferType.NORMAL);
            tvNameCity.enableSizeCache(false);
            tvNameCity.setMinWidth(40);

            temp = savedInstanceState.getString("TEMP");
            tvNameDegres.setText(temp + " C°");

            tempMin = savedInstanceState.getString("MIN");
            tvNameMinDegres.setText("min : " + tempMin + " C°");

            tempMax = savedInstanceState.getString("MAX");
            tvNameMaxDegres.setText("max : " + tempMax + " C°");

            humidity = savedInstanceState.getString("HUMIDITY");
            tvNameHumidity.setText(getResources().getString(R.string.humidity) + " : " + humidity);

            wind = savedInstanceState.getString("WIND");
            tvNameWind.setText(getResources().getString(R.string.wind) + " : " + wind);


            weathers = savedInstanceState.getParcelableArrayList("WEATHER");
            lvDaily.setAdapter(new DailyAdapter(getActivity(), weathers));
        }

            return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("NAME", name);
        outState.putString("TEMP", temp);
        outState.putString("MIN", tempMin);
        outState.putString("MAX", tempMax);
        outState.putString("WIND", wind);
        outState.putString("HUMIDITY", humidity);
        outState.putString("ICON", icon);
        outState.putParcelableArrayList("WEATHER", weathers);
    }

    public void showCheckbox() {
        checkbox.setVisibility(View.VISIBLE);
    }

    public void hideCheckbox() {
        checkbox.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
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
//            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        @Override
        protected String doInBackground(String... strings) {

            String content = DataLoader.getWeatherCity(strings[0]);

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
                     * Updating parsed JSON data into ListView
                     */

                    try {
                        JSONObject object = new JSONObject(json);

                        name = object.getJSONObject("city").getString("name");

                        JSONArray list = object.getJSONArray("list");
                        JSONObject first = list.getJSONObject(0);

                        city = name + "," + object.getJSONObject("city").getString("country");
                        name = city;
                        Log.e("HomeFragment", "favorites = "+favorites);
                        if(favorites != null) {
                            List<String> array = Arrays.asList(favorites.split(";"));
                            for(String a : array) {
                                Log.e("HomeFragment", "fav = "+a);
                                if (a.equals(city)){
                                    checkbox.setChecked(true);
                                    checkbox.setHighlightColor(Color.YELLOW);
                                }
                                else {
                                    checkbox.setChecked(false);
                                }
                            }
//                    if(favorites != "") {
//                    }
                        }

                        temp = first.getJSONObject("temp").getString("day");
                        tempMin = first.getJSONObject("temp").getString("min");
                        tempMax = first.getJSONObject("temp").getString("max");
                        String pressure = first.getString("pressure");
                        wind = first.getString("speed");
                        humidity = first.getString("humidity");

                        icon = first.getJSONArray("weather").getJSONObject(0).getString("icon");
                        String main = first.getJSONArray("weather").getJSONObject(0).getString("main"); //FOR WALLPAPER
                        //new DownloadImageTask((ImageView) root.findViewById(R.id.imgViewWeather)).execute(icon + ".png");
                        imgViewWeather.setImageResource(_context.getResources().getIdentifier("_"+icon, "drawable", _context.getPackageName()));

                        tvNameCity.setText(city, AutoResizeTextView.BufferType.NORMAL);
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
                        lvDaily.invalidate();

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                    catch (JSONException e) {
                        Log.e(getActivity().getLocalClassName(), "_"+e.getMessage());
                    }

                }
            });

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main_two, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_refresh) {

            if(((DrawerActivity)getActivity()).checkConnection()) {

                String favo = sharedpreferences.getString(DrawerActivity.APP_DATA_LIVING_CITY, null);
                if(favo != null) {
                    new LaunchRequest((DrawerActivity) getActivity()).execute(favo);
                }
            }
            else {

                ((DrawerActivity)getActivity()).noConnection();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class YourDialogFragmentDismissHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // refresh your textview's here
            sharedpreferences = getActivity().getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);
            String favo = sharedpreferences.getString(DrawerActivity.APP_DATA_LIVING_CITY, null);

            if(((DrawerActivity)getActivity()).checkConnection()) {
                if (favo == null) {
                    //show a dialog which add a name in favo
                    AddFavoriteFragment alertdFragment = new AddFavoriteFragment(new YourDialogFragmentDismissHandler());
//            Log.e("HomeFragment.class", fm.toString());
                    alertdFragment.show(fm, getResources().getString(R.string.favorite));
                } else {
                    weathers = new ArrayList<>();
                    new LaunchRequest((DrawerActivity) getActivity()).execute(favo);
                }
            }

        }
    }

}
