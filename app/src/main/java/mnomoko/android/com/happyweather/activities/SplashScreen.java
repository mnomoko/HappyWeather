package mnomoko.android.com.happyweather.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.data.loader.DataLoader;
import mnomoko.android.com.happyweather.database.MySqlLiteHelper;

/**
 * Created by mnomoko on 05/07/15.
 */
public class SplashScreen extends Activity {

    private static String CITIES_FILE = "cities_file.txt";
    private int SPLASH_TIME_OUT = 0;
    List<String> content = null;
    SharedPreferences sharedPreferences;

    RelativeLayout layout;

    String now_playing, earned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getLayoutInflater().inflate(R.layout.activity_splash, frameLayout);

        layout = (RelativeLayout) findViewById(R.id.background);

        DrawerActivity.setBackgroundView(layout, this, R.drawable.sun);

        sharedPreferences = getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);

        //
        int first = sharedPreferences.getInt(DrawerActivity.APP_DATA_FIRST, 0);
        if(first == 1) {
            SPLASH_TIME_OUT = 1500;
        }
        else {
            SPLASH_TIME_OUT = 5000;
        }
        SPLASH_TIME_OUT = 0;

//        check if data is already download and when, if it been too far in the pass or if there are nothing download
//        so the splash screen will appear and data will be load
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setDataBase();

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);


                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(DrawerActivity.APP_DATA_FIRST, 1);
            }
        }, SPLASH_TIME_OUT);

//        setDataBase();
//        Intent i = new Intent(SplashScreen.this, MainActivity.class);
//        startActivity(i);
    }

    public void setDataBase() {
        MySqlLiteHelper db = new MySqlLiteHelper(this);
//        db.dropTable();
        db.createTable();
//        for(City c : db.getAllCities()) {
//            db.deleteCity(c);
//        }

        int count = db.getRowCount();
        Log.e("___Count Row", "" + count);
        if(count < 10) {

            content = new ArrayList<>();

            try {
                new DownloadCityFile(db).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        //CSV
//            for(int i = 1; i<content.size(); i++) {


            /**
             * CRUD Operations
             * */
                // add Books
//                db.addCity(new City("paris", "FR"));
//            db.addCity(new City("saint-denis", "FR"));
//            db.addCity(new City("saint-ouen", "FR"));
//            db.addCity(new City("stains", "FR"));
//            db.addCity(new City("saint-clous", "FR"));
        }

    }

    //  http://openweathermap.org/help/city_list.txt

    /* * Method to read CSV file using CSVParser from Apache Commons CSV */
//    public static List<City> parseCSV() throws FileNotFoundException, IOException {
//        List<City> data = new ArrayList<>();
//
////        CSVParser parser = new CSVParser(new FileReader("countries.csv"), CSVFormat.DEFAULT.withHeader());
//        CSVParser parser = new CSVParser(new FileReader("countries.csv"), CSVFormat.DEFAULT.withHeader());
//        for (CSVRecord record : parser) {
//            System.out.printf("%s\t%s\n", record.get("nm"), record.get("countryCode"));
//            data.add(new City(record.get("nm"), record.get("countryCode")));
//        }
//        parser.close();
//        return data;
//    }

    public class DownloadCityFile extends AsyncTask<Void, Void, List<String>> {

        MySqlLiteHelper db;

        public DownloadCityFile(MySqlLiteHelper base) {
            db = base;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {

            List<String> page = new ArrayList<>();
            page = DataLoader.loadCitiesFile();

            return page;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            super.onPostExecute(list);

//            Log.e("____________s", ""+list.size());
//            content.addAll(list);

            db.addCities(list);

//            int i = 1;
//            while(content.get(i)!= null && i < 100) {
//
//                String s = content.get(i);
//                String[] array = s.split("\t");
//
//                Log.e("addCity", array[1] + ", " + array[4]);
//                db.addCity(new City(array[1], array[4]));
//                i++;
//            }

//            Long tsLong = System.currentTimeMillis()/1000;
//            String ts = tsLong.toString();
//            Log.e("SplashScreen", "Start : " + ts);

//            for(String s : list) {
//
//                String[] array = s.split("\t");
//
////                Log.e("addCity", array[1] + ", " + array[4]);
//                db.addCity(new City(array[1], array[4]));
//            }

//            tsLong = System.currentTimeMillis()/1000;
//            ts = tsLong.toString();
//            Log.e("SplashScreen", "End : " + ts);
        }
    }
}
