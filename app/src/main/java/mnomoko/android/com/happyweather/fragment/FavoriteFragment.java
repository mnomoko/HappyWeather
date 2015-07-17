package mnomoko.android.com.happyweather.fragment;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.adapters.DailyAdapter;
import mnomoko.android.com.happyweather.algorithme.AutoResizeTextView;
import mnomoko.android.com.happyweather.algorithme.CirclePagerIndicator;
import mnomoko.android.com.happyweather.algorithme.SwipeListener;
import mnomoko.android.com.happyweather.data.loader.DataLoader;
import mnomoko.android.com.happyweather.model.Weather;

/**
 * Created by mnomoko on 28/06/15.
 */
public class FavoriteFragment extends Fragment implements SwipeListener {

    private ViewFlipper viewFlipper;
    CirclePagerIndicator circlePagerIndicator;
    private Float lastX;
    ImageView image;
//    TextView name;
    TextView degrees;


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
    LinearLayout layout;
//    RelativeLayout layout;


//    String name;
//    String temp;
//    String tempMin;
//    String tempMax;
//    String wind;
//    String humidity;
//    String icon;


    ArrayList<String> listname;
    ArrayList<String> listtemp;
    ArrayList<String> listtempMin;
    ArrayList<String> listtempMax;
    ArrayList<String> listwind;
    ArrayList<String> listhumidity;
    ArrayList<String> listicon;
    int current;

    ArrayList<Weather> weathers;
    ArrayList<ArrayList<Weather>> listWeather;

    String city;
    String favorites;

    GestureDetector detector;


    //    List<Weather> weathers;
    Animation slide_in_left, slide_out_right;

//    View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        listname = new ArrayList<>();
        listtemp = new ArrayList<>();
        listtempMin = new ArrayList<>();
        listtempMax = new ArrayList<>();
        listwind = new ArrayList<>();
        listhumidity = new ArrayList<>();
        listicon = new ArrayList<>();


        this.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        root = inflater.inflate(R.layout.favorite_fragment, null);

        viewFlipper = (ViewFlipper) root.findViewById(R.id.viewFlipper);
        circlePagerIndicator = (CirclePagerIndicator) root.findViewById(R.id.circlePageIndicator);
        circlePagerIndicator.setViewFlipper(viewFlipper);
        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new CustomGestureDetectorFlipper(viewFlipper, circlePagerIndicator));

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        detector = gestureDetector;

        slide_in_left = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_in_from_left);
        slide_out_right = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_in_from_right);

        viewFlipper.setInAnimation(slide_in_left);
        viewFlipper.setOutAnimation(slide_out_right);

        sharedpreferences = getActivity().getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);
        favorites = sharedpreferences.getString(DrawerActivity.APP_DATA_FAVORITES_CITY, "");

        Log.e("FavoriteFragment", "-"+favorites);

        Log.e("SharedPreferences", sharedpreferences.getAll().toString());

        listWeather = new ArrayList<>();

        if(savedInstanceState == null) {

            setFlipperContent();
        }
        else {

            ArrayList<ArrayList<Weather>> temp = new ArrayList<>();

            int size = savedInstanceState.getInt("SIZE");
            listicon = savedInstanceState.getStringArrayList("ICON");
            listname = savedInstanceState.getStringArrayList("NAME");
            listtemp = savedInstanceState.getStringArrayList("TEMP");
            listtempMin = savedInstanceState.getStringArrayList("MIN");
            listtempMax = savedInstanceState.getStringArrayList("MAX");
            listhumidity = savedInstanceState.getStringArrayList("HUMIDITY");
            listwind = savedInstanceState.getStringArrayList("WIND");

            for(int i = 0; i < size; i++) {

                LayoutInflater inflat = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflat.inflate(R.layout.favorite_fragment_item, null);

                layout = (LinearLayout) view.findViewById(R.id.background);

                //DEFINE
                imgViewWeather = (ImageView) view.findViewById(R.id.imgViewWeather);
//                tvNameCity = (AutoResizeTextView) view.findViewById(R.id.tvNameCity);
                tvNameCity = (AutoResizeTextView) view.findViewById(R.id.tvNameCity);
                tvNameDegres = (TextView) view.findViewById(R.id.tvNameDegrees);
                tvNameDegres.setTextSize(tvNameDegres.getTextSize() * (3 / 2));
                tvNameMinDegres = (TextView) view.findViewById(R.id.tvNameMinDegres);
                tvNameMaxDegres = (TextView) view.findViewById(R.id.tvNameMaxDegres);
                tvNameHumidity = (TextView) view.findViewById(R.id.tvNameHumidity);
                tvNameWind = (TextView) view.findViewById(R.id.tvNameWind);

                lvDaily = (ListView) view.findViewById(R.id.lvDaily);

                //SET
                imgViewWeather.setImageResource(getActivity().getResources().getIdentifier("_" + listicon.get(i), "drawable", getActivity().getPackageName()));

                tvNameCity.setText(listname.get(i), TextView.BufferType.NORMAL);

                tvNameDegres.setText(listtemp.get(i) + " C°");

                tvNameMinDegres.setText("min : " + listtempMin.get(i) + " C°");

                tvNameMaxDegres.setText("max : " + listtempMax.get(i) + " C°");

                tvNameHumidity.setText(getResources().getString(R.string.humidity) + " : " + listhumidity.get(i));

                tvNameWind.setText(getResources().getString(R.string.wind) + " : " + listwind.get(i));

                weathers = savedInstanceState.getParcelableArrayList("WEATHER_" + i);
                lvDaily.setAdapter(new DailyAdapter(getActivity(), weathers));
                lvDaily.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(detector.onTouchEvent(event)){
                            return true;
                        }
                        return false;
                    }
                });

                temp.add(weathers);

                FavoriteFragment.setBackgroundView(layout, getActivity(), R.drawable.sun);
                viewFlipper.addView(view);
            }
            int position = savedInstanceState.getInt("CURRENT");
            listWeather = temp;

            int i = 0;
            if(viewFlipper.getDisplayedChild() != position) {
//                do {
//                    viewFlipper.showNext();
//                    i++;
//                }
                while(i != position && i < viewFlipper.getChildCount()) {
                    i++;
                }
                viewFlipper.setDisplayedChild(i);
            }
        }

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("NAME", listname);
        outState.putStringArrayList("TEMP", listtemp);
        outState.putStringArrayList("MIN", listtempMin);
        outState.putStringArrayList("MAX", listtempMax);
        outState.putStringArrayList("WIND", listwind);
        outState.putStringArrayList("HUMIDITY", listhumidity);
        outState.putStringArrayList("ICON", listicon);
        for(int i = 0; i < listWeather.size(); i++) {
            ArrayList weath = listWeather.get(i);
            outState.putParcelableArrayList("WEATHER_" + i, weath);
        }
        outState.putInt("SIZE", listWeather.size());
        outState.putInt("CURRENT", viewFlipper.getDisplayedChild());
    }

    private void setFlipperContent() {
//        List<Stuff> aux=getStuffList();
        String[] cities = favorites.split(";");
        int end = cities.length;

        Log.e("FavoriteFragment", cities.length + " : " + favorites);


        boolean connect = ((DrawerActivity)getActivity()).checkConnection();
        if(!connect) {

            ((DrawerActivity) getActivity()).noConnection();
//                ((DrawerActivity) getActivity()).showConnectionError();
        }
        else {
            for (int i = 0; i < end; i++) {
                Log.e("Favorite", cities[i]);
                new LaunchRequest((DrawerActivity) getActivity()).execute(cities[i]);
            }
        }
        circlePagerIndicator.invalidate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main_two, menu);
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_refresh) {

            if(((DrawerActivity)getActivity()).checkConnection()) {

                String[] cities = favorites.split(";");
                int end = cities.length;
                for (int i = 0; i < end; i++) {
                    Log.e("Favorite", cities[i]);
                    viewFlipper.removeAllViews();
                    new LaunchRequest((DrawerActivity) getActivity()).execute(cities[i]);
                }
                return true;
            }
            else {
                ((DrawerActivity)getActivity()).noConnection();
                return false;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class LaunchRequest extends AsyncTask<String, String, String> {

        /** application context. */
        private Context context;

        private ProgressDialog dialog;
        private DrawerActivity activity;
        public LaunchRequest(DrawerActivity activity) {
            this.activity = activity;
            context = this.activity;
            dialog = new ProgressDialog(context);
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

                        String name = object.getJSONObject("city").getString("name");

                        JSONArray list = object.getJSONArray("list");
                        JSONObject first = list.getJSONObject(0);

                        city = name + "," + object.getJSONObject("city").getString("country");
                        name = city;
                        Log.e("HomeFragment", "+" + favorites);

                        LayoutInflater inflater = (LayoutInflater) getActivity()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.favorite_fragment_item, null);

                        layout = (LinearLayout) view.findViewById(R.id.background);
//                layoutSubView = (LinearLayout) view.findViewById(R.id.background);

//                layout = (RelativeLayout) view.findViewById(R.id.background);

                        weathers = new ArrayList<>();

                        checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
                        checkbox.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                if (checkbox.isChecked()) {

                                    favorites += city + ";";
                                    editor.putString(DrawerActivity.APP_DATA_FAVORITES_CITY, favorites);
                                    editor.commit();
                                } else {
                                    String temp = "";
                                    for (String s : favorites.split(";")) {

                                        if (!s.equals(city)) {
                                            temp += s + ";";
                                        }
                                        editor.putString(DrawerActivity.APP_DATA_FAVORITES_CITY, temp);
                                        editor.commit();
                                    }
                                }
                            }
                        });
                        if (favorites != null) {
                            if (Arrays.asList(favorites.split(";")).contains(city)) {
                                checkbox.setChecked(true);
                            }
                        }

                        imgViewWeather = (ImageView) view.findViewById(R.id.imgViewWeather);
//                tvNameCity = (AutoResizeTextView) view.findViewById(R.id.tvNameCity);
                        tvNameCity = (AutoResizeTextView) view.findViewById(R.id.tvNameCity);
                        tvNameDegres = (TextView) view.findViewById(R.id.tvNameDegrees);
                        tvNameDegres.setTextSize(tvNameDegres.getTextSize() * (3 / 2));
                        tvNameMinDegres = (TextView) view.findViewById(R.id.tvNameMinDegres);
                        tvNameMaxDegres = (TextView) view.findViewById(R.id.tvNameMaxDegres);
                        tvNameHumidity = (TextView) view.findViewById(R.id.tvNameHumidity);
                        tvNameWind = (TextView) view.findViewById(R.id.tvNameWind);

                        lvDaily = (ListView) view.findViewById(R.id.lvDaily);
                        lvDaily.setOnTouchListener(new View.OnTouchListener(/*((FavoriteFragment.this).getActivity()), gestureListener*/) {

                            int before = -1;

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.AXIS_HSCROLL:
                                        detector.onTouchEvent(event);
                                        return true;
//                                        break;
                                    case MotionEvent.ACTION_DOWN:
                                        before = ((ListView) v).getFirstVisiblePosition();
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        int now = ((ListView) v).getFirstVisiblePosition();
//                                if(now < before)
//                                else if(now > before)
                                        break;

                                }
                                return false;
                            }
                        });

                        String temp = first.getJSONObject("temp").getString("day");
                        String tempMin = first.getJSONObject("temp").getString("min");
                        String tempMax = first.getJSONObject("temp").getString("max");
                        String pressure = first.getString("pressure");
                        String wind = first.getString("speed");
                        String humidity = first.getString("humidity");

                        String icon = first.getJSONArray("weather").getJSONObject(0).getString("icon");
                        String main = first.getJSONArray("weather").getJSONObject(0).getString("main"); //FOR WALLPAPER
//                new DownloadImageTask((ImageView) root.findViewById(R.id.imgViewWeather)).execute(icon + ".png");
                        imgViewWeather.setImageResource(context.getResources().getIdentifier("_" + icon, "drawable", context.getPackageName()));

                        tvNameCity.setText(name, TextView.BufferType.NORMAL);
                        tvNameDegres.setText(temp + " C°");
                        tvNameMinDegres.setText("min : " + tempMin + " C°");
                        tvNameMaxDegres.setText("max : " + tempMax + " C°");
                        tvNameHumidity.setText(getResources().getString(R.string.humidity) + " : " + humidity);
                        tvNameWind.setText(getResources().getString(R.string.wind) + " : " + wind);

                        SimpleDateFormat sdf = new SimpleDateFormat("EEEE d");

                        for (int i = 1; i < list.length(); i++) {
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
                        lvDaily.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if(detector.onTouchEvent(event)){
                                    return true;
                                }
                                return false;
                            }
                        });
                        /*new CustomGestureDetectorListView(lvDaily));*/
                        FavoriteFragment.setBackgroundView(layout, getActivity(), R.drawable.sun);
//                FavoriteFragment.setBackgroundView(bigLayout, getActivity(), R.drawable.sun);


//                lvDaily.invalidate();
                        //FOR SAVEDINSTANCESTATE
                        listname.add(name);
                        listtemp.add(temp);
                        listtempMin.add(tempMin);
                        listtempMax.add(tempMax);
                        listwind.add(wind);
                        listhumidity.add(humidity);
                        listicon.add(icon);
                        listWeather.add(weathers);

                        viewFlipper.addView(view);
                        circlePagerIndicator.setCurrentDisplayedChild(0);


                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                    } catch (JSONException e) {
                        Log.e(getActivity().getLocalClassName(), "_" + e.getMessage());
                    }
                }
            });

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getActivity().dispatchTouchEvent(event);
        return detector.onTouchEvent(event);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(Context context, int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resource, null);
        }

        return context.getResources().getDrawable(resource);
    }

    public static void setBackgroundView(LinearLayout layout, Context context, int drawable) {
//    public static void setBackgroundView(RelativeLayout layout, Context context, int drawable) {

        if (Build.VERSION.SDK_INT >= 16) {
            layout.setBackground(FavoriteFragment.getDrawable(context, drawable));
        }else{
            layout.setBackgroundDrawable(FavoriteFragment.getDrawable(context, drawable));
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
                //show a dialog which add a name in favo
                AddFavoriteFragment alertdFragment = new AddFavoriteFragment(new YourDialogFragmentDismissHandler());
//            Log.e("HomeFragment.class", fm.toString());
                alertdFragment.show(fm, getResources().getString(R.string.favorite));
            }
            else {
                new LaunchRequest((DrawerActivity)getActivity()).execute(favo);
            }

        }
    }

    Animation slideInRightAnimation;
    Animation slideOutLeftAnimation;
    Animation slideInLeftAnimation;
    Animation slideOutRightAnimation;

    @Override
    public void swipeLeft(ViewFlipper viewFlipper) {
        slideInRightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_right);
        slideOutLeftAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_left);
        slideInRightAnimation.setDuration(150);
        slideOutLeftAnimation.setDuration(150);
        viewFlipper.setInAnimation(slideInRightAnimation);
        viewFlipper.setOutAnimation(slideOutLeftAnimation);
        viewFlipper.showNext();
    }

    @Override
    public void swipeRight(ViewFlipper viewFlipper) {
        slideInLeftAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_left);
        slideOutRightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_right);
        slideInLeftAnimation.setDuration(150);
        slideOutRightAnimation.setDuration(150);
        viewFlipper.setInAnimation(slideInLeftAnimation);
        viewFlipper.setOutAnimation(slideOutRightAnimation);
        viewFlipper.showPrevious();
    }

    class CustomGestureDetectorFlipper extends GestureDetector.SimpleOnGestureListener {

        ViewFlipper viewFlipper;
        CirclePagerIndicator indicator;

        public CustomGestureDetectorFlipper(ViewFlipper viewFlipper, CirclePagerIndicator indicator) {
            this.viewFlipper = viewFlipper;
            this.indicator = indicator;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            int displayedChild = viewFlipper.getDisplayedChild();
            int childCount = viewFlipper.getChildCount();

            float sensibility = 50;
            if ((e1.getX() - e2.getX()) > sensibility) {
                if (displayedChild == childCount - 1) {
                    return false;
                } else {
//                    viewFlipper.showPrevious();
                    swipeLeft(viewFlipper);
                    indicator.setCurrentDisplayedChild(viewFlipper.getDisplayedChild());
                    return true;
                }
            } else if ((e2.getX() - e1.getX()) > sensibility) {
                if (displayedChild <= 0) {
                    return false;
                } else {
//                    viewFlipper.showNext();
                    swipeRight(viewFlipper);
                    indicator.setCurrentDisplayedChild(viewFlipper.getDisplayedChild());
                    return true;
                }
            }

            return false;
        }
    }
}
