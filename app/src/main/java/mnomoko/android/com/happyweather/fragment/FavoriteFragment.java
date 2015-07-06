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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
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
import java.util.List;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.adapters.DailyAdapter;
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
    TextView name;
    TextView degrees;


    SharedPreferences sharedpreferences;
    FragmentManager fm;
    ImageView imgViewWeather;
    TextView tvNameDegres;
    TextView tvNameCity;
    //    AutoResizeTextView tvNameCity;
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

    String city;
    String favorites;

    GestureDetector detector;


    //    List<Weather> weathers;
    Animation slide_in_left, slide_out_right;

//    View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.favorite_fragment, null);

        viewFlipper = (ViewFlipper) root.findViewById(R.id.viewFlipper);
        circlePagerIndicator = (CirclePagerIndicator) root.findViewById(R.id.circlePageIndicator);
        circlePagerIndicator.setViewFlipper(viewFlipper);
        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new CustomGestureDetector(viewFlipper, circlePagerIndicator));

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

        setFlipperContent();

        return root;
    }

//    @Override
//    public void onSaveInstanceState(Bundle bundle) {
//        View v;
//
//        ImageView _imgViewWeather;
//        TextView _tvNameDegres;
//        TextView _tvNameCity;
//        TextView _tvNameMinDegres;
//        TextView _tvNameMaxDegres;
//        TextView _tvNameHumidity;
//        TextView _tvNameWind;
//        ListView _lvDaily;
//        CheckBox _checkbox;
//
//        for(int i = 0; i < viewFlipper.getChildCount(); i++) {
//            v = viewFlipper.getChildAt(i);
//
//            _imgViewWeather = (ImageView) v.findViewById(R.id.imgViewWeather);
//            _tvNameCity = (TextView) v.findViewById(R.id.tvNameCity);
//            _tvNameDegres = (TextView) v.findViewById(R.id.tvNameDegrees);
//            _tvNameMinDegres = (TextView) v.findViewById(R.id.tvNameMinDegres);
//            _tvNameMaxDegres = (TextView) v.findViewById(R.id.tvNameMaxDegres);
//            _tvNameHumidity = (TextView) v.findViewById(R.id.tvNameHumidity);
//            _tvNameWind = (TextView) v.findViewById(R.id.tvNameWind);
//            _lvDaily = (ListView) v.findViewById(R.id.lvDaily);
//
//            bundle.putString("_tvNameCity", _tvNameCity.getText().toString());
//            bundle.putString("_tvNameDegres", _tvNameDegres.getText().toString());
//            bundle.putString("_tvNameMinDegres", _tvNameMinDegres.getText().toString());
//            bundle.putString("_tvNameMaxDegres", _tvNameMaxDegres.getText().toString());
//            bundle.putString("_tvNameHumidity", _tvNameHumidity.getText().toString());
//            bundle.putString("_tvNameWind", _tvNameWind.getText().toString());
//        }
//        bundle.putInt("number", viewFlipper.getChildCount());
//    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        int size = savedInstanceState.getInt("number");
//        for(int i = 0; i < size; i++) {
//
//        }
//    }

    private void setFlipperContent() {
//        List<Stuff> aux=getStuffList();
        String[] cities = favorites.split(";");
        int end = cities.length;

        Log.e("FavoriteFragment", cities.length + " : " + favorites);
        for (int i = 0; i < end; i++) {
            Log.e("Favorite", cities[i]);

            new LaunchRequest((DrawerActivity)getActivity()).execute(cities[i]);

        }
        circlePagerIndicator.invalidate();
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
                        Log.e("HomeFragment", "+" + favorites);

                        LayoutInflater inflater = (LayoutInflater) getActivity()
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.favorite_fragment_item, null);

                        layout = (LinearLayout) view.findViewById(R.id.background);
//                layoutSubView = (LinearLayout) view.findViewById(R.id.background);

//                layout = (RelativeLayout) view.findViewById(R.id.background);

                        List<Weather> weathers = new ArrayList<>();

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
                        tvNameCity = (TextView) view.findViewById(R.id.tvNameCity);
                        tvNameDegres = (TextView) view.findViewById(R.id.tvNameDegrees);
                        tvNameDegres.setTextSize(tvNameDegres.getTextSize() * (3 / 2));
                        tvNameMinDegres = (TextView) view.findViewById(R.id.tvNameMinDegres);
                        tvNameMaxDegres = (TextView) view.findViewById(R.id.tvNameMaxDegres);
                        tvNameHumidity = (TextView) view.findViewById(R.id.tvNameHumidity);
                        tvNameWind = (TextView) view.findViewById(R.id.tvNameWind);

                        lvDaily = (ListView) view.findViewById(R.id.lvDaily);
                        lvDaily.setOnTouchListener(new View.OnTouchListener(/*((FavoriteFragment.this).getActivity()), gestureListener*/) {
                            //                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
//                        switch ((motionEvent.getAction())) {
//                            case MotionEvent.ACTION_DOWN:
//                                detector.onTouchEvent(motionEvent);
//                                break;
//                            case MotionEvent.ACTION_UP:
//                                detector.onTouchEvent(motionEvent);
//                                break;
//                            case MotionEvent.ACTION_MOVE:
//                                detector.onTouchEvent(motionEvent);
//                                break;
//                        }
//                        return true;
//                    }
//                });
                            int before = -1;

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                switch (event.getAction()) {
                                    case MotionEvent.AXIS_HSCROLL:
                                        detector.onTouchEvent(event);
                                        break;
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
                        int size = tvNameCity.getText().length();
                        if (size < 6) {
                            tvNameCity.setTextSize(tvNameCity.getTextSize() * (5 / 3));
                        } else if (size < 13) {
                            tvNameCity.setTextSize(tvNameCity.getTextSize() * (3 / 2));
                        }
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
                        FavoriteFragment.setBackgroundView(layout, getActivity(), R.drawable.sun);
//                FavoriteFragment.setBackgroundView(bigLayout, getActivity(), R.drawable.sun);


//                lvDaily.invalidate();

                        viewFlipper.addView(view);
                        circlePagerIndicator.setCurrentDisplayedChild(0);


                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        Log.e(getActivity().getLocalClassName(), "_" + e.getMessage());
                    }
                }
            });

        }
    }

    public void setFlipperContent(List<Weather> vawe) {

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
//
//    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                               float velocityY) {
//            Log.e("FavoriteFragment", "move");
//            try {
//                if (Math.abs(e1.getY() - e2.getY()) > 250)
//                    return false;
//                // right to left swipe
//                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    viewFlipper.setInAnimation(slide_in_left);
//                    viewFlipper.setOutAnimation(slide_in_left);
//                    viewFlipper.showNext();
//                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    viewFlipper.setInAnimation(slide_out_right);
//                    viewFlipper.setOutAnimation(slide_out_right);
//                    viewFlipper.showPrevious();
//                }
//            } catch (Exception e) {
//                // nothing
//            }
//            return false;
//        }
//    }

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

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        ViewFlipper viewFlipper;
        CirclePagerIndicator indicator;

        public CustomGestureDetector(ViewFlipper viewFlipper, CirclePagerIndicator indicator) {
            this.viewFlipper = viewFlipper;
            this.indicator = indicator;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            int displayedChild = viewFlipper.getDisplayedChild();
            int childCount = viewFlipper.getChildCount();


            Log.i("FavoriteFragment", "onFling has been called!");
//            final int SWIPE_MIN_DISTANCE = 120;
//            final int SWIPE_MAX_OFF_PATH = 250;
//            final int SWIPE_THRESHOLD_VELOCITY = 200;
//            try {
//                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
//                    return false;
//                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Log.i("FavoriteFragment", "Right to Left");
//                    swiftLeft(viewFlipper);
////                    viewFlipper.showNext();
////            indicator.setCurrentDisplayedChild(displayedChild);
//                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
//                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                    Log.i("FavoriteFragment", "Left to Right");
//
//                    swipeRight(viewFlipper);
////                    viewFlipper.showPrevious();
//
////            indicator.setCurrentDisplayedChild(displayedChild);
//                }
//            } catch (Exception e) {
//                // nothing
//            }

            float sensibility = 50;
            if ((e1.getX() - e2.getX()) > sensibility) {
                if (displayedChild == childCount - 1) {
                    return false;
                } else {
//                    viewFlipper.showPrevious();
                    swipeLeft(viewFlipper);
                    indicator.setCurrentDisplayedChild(viewFlipper.getDisplayedChild());
                }
            } else if ((e2.getX() - e1.getX()) > sensibility) {
                if (displayedChild <= 0) {
                    return false;
                } else {
//                    viewFlipper.showNext();
                    swipeRight(viewFlipper);
                    indicator.setCurrentDisplayedChild(viewFlipper.getDisplayedChild());
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
//            return true;
        }
    }
}