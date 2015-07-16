package mnomoko.android.com.happyweather.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.adapters.AutocompleteDBCustomArrayAdapter;
import mnomoko.android.com.happyweather.algorithme.CustomAutoCompleteTextViewDB;
import mnomoko.android.com.happyweather.database.City;
import mnomoko.android.com.happyweather.database.MySqlLiteHelper;
import mnomoko.android.com.happyweather.fragment.FavoriteFragment;
import mnomoko.android.com.happyweather.fragment.HomeFragment;
import mnomoko.android.com.happyweather.fragment.LocationFragment;
import mnomoko.android.com.happyweather.fragment.ResultFragment;
import mnomoko.android.com.happyweather.fragment.SearchFragment;
import mnomoko.android.com.happyweather.fragment.parent.FragmentHanger;

/**
 * Created by mnomoko on 28/06/15.
 */
public class DrawerActivity extends AppCompatActivity implements FragmentHanger.TaskStatusCallback, SearchView.OnQueryTextListener, TextWatcher,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String APP_DATA = "Application_Happy_Weather";
    public static final String APP_DATA_LIVING_CITY = "Application_Happy_Weather_Living_City";
    public static final String APP_DATA_FAVORITES_CITY = "Application_Happy_Weather_Favorites_City";
    public static final String APP_DATA_FIRST = "Application_Happy_Weather_First_Time";

    public static Context context;

    public FrameLayout frameLayout;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;

    private HomeFragment homeFragment;
    private FavoriteFragment favoriteFragment;
    private  SearchFragment searchFragment;
    private LocationFragment locationFragment;

    ArrayAdapter<City> myAdapter;

    public SharedPreferences sharedpreferences;

    //FOR AUTOCOMPLETE DB
    public CustomAutoCompleteTextViewDB myAutoComplete;

    // for database operations
    public MySqlLiteHelper databaseH;

    //FIN AUTOCUSTOM DB

    private static final String TAG = "LocationActivity";

    String longitude = null;
    String latitude = null;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;

    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getBaseContext();

        try {
            File httpCacheDir = new File(context.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        }
        catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }

        setContentView(R.layout.drawer_main);


        if (!isGooglePlayServicesAvailable()) {
//            finish();
            Log.e("GooglePlayServices", "not available !");
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // check if GPS enabled
//        gpsTracker = new GPSTracker(this);

        if(savedInstanceState == null)
        {
            homeFragment = new HomeFragment();
            favoriteFragment = new FavoriteFragment();

//            mFragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//
//            FragmentOne fragment = new FragmentOne();
//
//            fragmentTransaction.add(R.id.fragment_container, fragment);
//            fragmentTransaction.commit();
        }

        //FOR DB AUTOCOMPLETE
        // instantiate database handler
        databaseH = new MySqlLiteHelper(this);


        // ObjectItemData has no value at first
        City[] ObjectItemData = new City[0];

        // set the custom ArrayAdapter
        myAdapter = new AutocompleteDBCustomArrayAdapter(this, ObjectItemData);

//
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowCustomEnabled(true);

//        LayoutInflater inflator = (LayoutInflater) this
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflator.inflate(R.layout.actionbar, null);
//
//        actionBar.setCustomView(v);

        frameLayout = (FrameLayout)findViewById(R.id.container);

        mTitle = mDrawerTitle = getTitle();
        mPlanetTitles = getResources().getStringArray(R.array.titles_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
//        getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(mTitle);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    public ArrayAdapter<City> getArrayAdapter() {
//        if(searchFragment != null) {
//            return searchFragment.getArrayAdapter();
//        }
//        return null;
        if(myAdapter != null) {
            return myAdapter;
        }
        return null;
    }

    public void setArrayAdapter(ArrayAdapter<City> adapter) {
//        if(searchFragment != null) {
//            searchFragment.setArrayAdapter(adapter);
//        }
        myAdapter = adapter;
    }

    public CustomAutoCompleteTextViewDB getMyAutoComplete() {
//        if(searchFragment != null) {
//            return searchFragment.getMyAutoComplete();
//        }
//        return null;
        if(myAutoComplete != null) {
            return myAutoComplete;
        }
        return null;
    }

    public void setMyAutoComplete(CustomAutoCompleteTextViewDB myAutoComplete) {
//        if(searchFragment != null) {
//            searchFragment.setMyAutoComplete(myAutoComplete);
//        }
        this.myAutoComplete = myAutoComplete;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    public void seeHourlyWeather(View v) {

    }

    public boolean checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void setMobileData(Context context) {
        try {
            ConnectivityManager dataManager;
            dataManager  = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Method dataMtd = null;
            try {
                dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
                dataMtd.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "DA1_" + e.getMessage());
            }
            try {
                dataMtd.invoke(dataManager, true);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "DA_2" + e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(TAG, "DA_3" + e.getMessage());
            } catch (InvocationTargetException e) {
                Log.e(TAG, "DA_4" + e.getMessage());
            }
        }
        catch (Exception e) {
            Log.e(TAG, "DA_"+e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_location:

                updateUI();
                if(latitude != null && longitude != null) {
                    if (Double.valueOf(longitude) != 0 && Double.valueOf(latitude) != 0) {
                        LocationFragment locationFragment = new LocationFragment(longitude, latitude);
                        locationFragment.show(getSupportFragmentManager(), getResources().getString(R.string.favorite));
                    } else {

                        new AlertDialog.Builder(this)
                                .setTitle(R.string.error_localisation)
                                .setMessage(R.string.localisation_not_found)
                                .show();
                    }
                }
                else {

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.error_localisation)
                            .setMessage(R.string.enable_localisation)
                            .show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(int progress) {

    }

    @Override
    public void onPostExecute() {

    }

    @Override
    public void onCancelled() {

    }

    public void showSearchFragment() {
        searchFragment.showSearchFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");

        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }

        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        if(mCurrentLocation.getLatitude() != 0 || mCurrentLocation.getLongitude() != 0) {
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            String lat;
            String lng;

//            ProgressDialog pd = new ProgressDialog(this);
//            pd.setMessage("Localisation en cours..");
//            pd.show();
//            do {
//
//                lat = String.valueOf(mCurrentLocation.getLatitude());
//                lng = String.valueOf(mCurrentLocation.getLongitude());
////                Toast.makeText(this, "lat : " + lat + " && lng : " + lng, Toast.LENGTH_SHORT).show();
//                Log.e("Drawer__", "lat : " + lat + " && lng : " + lng);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            while (Double.valueOf(lng) != 0 || Double.valueOf(lat) != 0);
//
//            if (pd.isShowing()) {
//                pd.dismiss();
//            }

            lat = String.valueOf(mCurrentLocation.getLatitude());
            lng = String.valueOf(mCurrentLocation.getLongitude());

            longitude = lng;
            latitude = lat;

//            tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
//                    "Latitude: " + lat + "\n" +
//                    "Longitude: " + lng + "\n" +
//                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
//                    "Provider: " + mCurrentLocation.getProvider());
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    public void noConnection() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.data)
                .setMessage(R.string.no_connexion)
                .show();
    }

    public void showConnectionError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_connection)
                .setTitle(R.string.data)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_data, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction(Settings.ACTION_SETTINGS);
                                startActivity(intent);

                                finish();
                            }
                        }

                );
        builder.show();
    }

    public void setResultFragment(ResultFragment resultFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, resultFragment).addToBackStack(null).commit();
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment fragment;
        boolean beChanged = false;

        deleteBackStack();

        switch (position) {
            case 0:
//                fragment = new HomeFragment();
                if(homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                fragment = homeFragment;
                ft.replace(R.id.container, fragment).commit();
                beChanged = true;
                break;
            case 1:
//                fragment = new FavoriteFragment();

                sharedpreferences = this.getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);
                String favorites = sharedpreferences.getString(DrawerActivity.APP_DATA_FAVORITES_CITY, "");

                if(favorites != "") {
                    if (favoriteFragment == null) {
                        favoriteFragment = new FavoriteFragment();
                    }
                    fragment = favoriteFragment;
                    ft.replace(R.id.container, fragment).commit();
                    beChanged = true;
                }
                else {
                    final Fragment frag = homeFragment;
                    final FragmentTransaction transaction = ft;
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.favorite)
                            .setMessage(R.string.no_favorite)
                            .setCancelable(false)
                            .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    transaction.replace(R.id.container, frag).commit();
                                }
                            }).show();
                    beChanged = false;
                }
                break;
            case 2:
                if(searchFragment == null) {
                    searchFragment = new SearchFragment();
                }
                fragment = searchFragment;
                ft.replace(R.id.container, fragment).commit();
                beChanged = true;
                break;
        }
        // update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        // update selected item and title, then close the drawer
        if(beChanged) {
            mDrawerList.setItemChecked(position, true);
            setTitle(mPlanetTitles[position]);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
//        getActionBar().setTitle(mTitle);
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
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
            layout.setBackground(DrawerActivity.getDrawable(context, drawable));
        }else{
            layout.setBackgroundDrawable(DrawerActivity.getDrawable(context, drawable));
        }

    }

    public static void setBackgroundView(LinearLayout layout, Context context, int drawable) {

        if (Build.VERSION.SDK_INT >= 16) {
            layout.setBackground(DrawerActivity.getDrawable(context, drawable));
        }else{
            layout.setBackgroundDrawable(DrawerActivity.getDrawable(context, drawable));
        }

    }

    public void deleteBackStack() {
        FragmentManager fm = this.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public String dateFromUnix(long unix) {

        String[] days = {};

        long timestamp = unix*1000L;

//        Date date = new Date(unix*1000L); // *1000 is to convert seconds to milliseconds
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4")); // give a timezone reference for formating (see comment at the bottom
//        String formattedDate = sdf.format(date);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        int d = cal.get(Calendar.DAY_OF_WEEK);
        switch (d) {
            case Calendar.MONDAY:
                return "monday";
            case Calendar.TUESDAY:
                return "tuesday";
            case Calendar.WEDNESDAY:
                return "wednesday";
            case Calendar.THURSDAY:
                return "thursday";
            case Calendar.FRIDAY:
                return "friday";
            case Calendar.SATURDAY:
                return "saturday";
            case Calendar.SUNDAY:
                return "sunday";
        }

        return null;
    }

    public void dialogErrorCity() {
        new AlertDialog.Builder(DrawerActivity.this)
                .setTitle("Ville non trouv�e")
                .setMessage("Assurez vous de bien saisir le non de la ville ?")
                .setPositiveButton("fermer", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Implementation of TextWatcher, used to have a proper onQueryTextChange (it doesn't update when the last character is removed)
    @Override
    public void beforeTextChanged (final CharSequence charSequence, final int start, final int count, final int after) {}

    @Override
    public void onTextChanged (final CharSequence charSequence, final int start, final int before, final int after) {}

    @Override
    public void afterTextChanged (final Editable editable) {
        if (editable.length() > 0)
            onQueryTextChange(editable.toString());
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
        return false;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        mSearchView.setOnQueryTextListener(this);
//
//        return true;
//    }

    /** Reading contents of the temporary file, if already exists */
    public static String readCacheFile(String path) {

        File cDir = context.getCacheDir();
        path = cDir + "/" + path;


        String strLine="";
        StringBuilder text = new StringBuilder();
        try {
            FileReader fReader = new FileReader(new File(path));
            BufferedReader bReader = new BufferedReader(fReader);

            /** Reading the contents of the file , line by line */
            while ((strLine = bReader.readLine()) != null) {
                text.append(strLine + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    public static void writeCacheFile(String path, String content) {

        File cDir = context.getCacheDir();
        path = cDir + "/" + path;

        FileWriter writer=null;
        try {
            writer = new FileWriter(path);

            /** Saving the contents to the file*/
            writer.write(content);

            /** Closing the writer object */
            writer.close();

//            Toast.makeText(context, "Temporary save here : " + path, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
