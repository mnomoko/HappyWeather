package mnomoko.android.com.happyweather.activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.algorithme.CustomAutoCompleteTextViewDB;
import mnomoko.android.com.happyweather.database.City;
import mnomoko.android.com.happyweather.database.MySqlLiteHelper;
import mnomoko.android.com.happyweather.fragment.FavoriteFragment;
import mnomoko.android.com.happyweather.fragment.HomeFragment;
import mnomoko.android.com.happyweather.fragment.SearchFragment;
import mnomoko.android.com.happyweather.fragment.parent.FragmentHanger;

/**
 * Created by mnomoko on 28/06/15.
 */
public class DrawerActivity extends AppCompatActivity implements FragmentHanger.TaskStatusCallback, SearchView.OnQueryTextListener, TextWatcher {

    public static final String APP_DATA = "Application_Happy_Weather";
    public static final String APP_DATA_LIVING_CITY = "Application_Happy_Weather_Living_City";
    public static final String APP_DATA_FAVORITES_CITY = "Application_Happy_Weather_Favorites_City";

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

    ActionBar actionBar;
    private SearchView searchView;

    public SharedPreferences sharedpreferences;

    //FOR AUTOCOMPLETE DB
    public CustomAutoCompleteTextViewDB myAutoComplete;

    // for database operations
    public MySqlLiteHelper databaseH;

    //FIN AUTOCUSTOM DB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        context = getBaseContext();

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
        if(searchFragment != null) {
            return searchFragment.getArrayAdapter();
        }
        return null;
    }

    public void setArrayAdapter(ArrayAdapter<City> adapter) {
        if(searchFragment != null) {
            searchFragment.setArrayAdapter(adapter);
        }
    }

    public CustomAutoCompleteTextViewDB getMyAutoComplete() {
        if(searchFragment != null) {
            return searchFragment.getMyAutoComplete();
        }
        return null;
    }

    public void setMyAutoComplete(CustomAutoCompleteTextViewDB myAutoComplete) {
        if(searchFragment != null) {
            searchFragment.setMyAutoComplete(myAutoComplete);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

//        searchAction = menu.add(0, SEARCH_ACTION_ID, 0 , getString(R.string.action_search));
//        searchAction.setShowAsAction(SHOW_AS_ACTION_ALWAYS | SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//        searchView = new CustomSearchView(getBaseContext());
//        searchView.setOnQueryTextListener(searchQueryListener);
//        searchView.setIconifiedByDefault(true);
//        searchAction.setActionView(searchView);

//        /*final SearchView */searchView = (SearchView) menu.findItem(R.id.action_websearch).getActionView();
//        final SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
//        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
//        searchView.setOnQueryTextListener(this);
//
//        final int resource_edit_text = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
//        ((EditText) searchView.findViewById(resource_edit_text)).addTextChangedListener(this);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.action_websearch).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    public void seeHourlyWeather(View v) {

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
            case R.id.action_websearch:
                searchView.setIconified(false);
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
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

        switch (position) {
            case 0:
//                fragment = new HomeFragment();
                if(homeFragment == null) {
                    homeFragment = new HomeFragment();
                }
                fragment = homeFragment;
                ft.replace(R.id.container, fragment).commit();
                break;
            case 1:
//                fragment = new FavoriteFragment();
                if(favoriteFragment == null) {
                    favoriteFragment = new FavoriteFragment();
                }
                fragment = favoriteFragment;
                ft.replace(R.id.container, fragment).commit();
                break;
            case 2:
//                fragment = new SearchFragment();
                if(searchFragment == null) {
                    searchFragment = new SearchFragment();
                }
                fragment = searchFragment;
                ft.replace(R.id.container, fragment).commit();
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
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
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
