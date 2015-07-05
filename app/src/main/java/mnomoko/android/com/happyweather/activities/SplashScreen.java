package mnomoko.android.com.happyweather.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.database.City;
import mnomoko.android.com.happyweather.database.MySqlLiteHelper;

/**
 * Created by mnomoko on 05/07/15.
 */
public class SplashScreen extends Activity {

    private static String CITIES_FILE = "cities_file.txt";
    private static int SPLASH_TIME_OUT = 6000;
    List<String> content = null;

    String now_playing, earned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getLayoutInflater().inflate(R.layout.activity_splash, frameLayout);


        //check if data is already download and when, if it been too far in the pass or if there are nothing download
        //so the splash screen will appear and data will be load
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                setDataBase();

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
            }
        }, SPLASH_TIME_OUT);
    }

    public void setDataBase() {
        MySqlLiteHelper db = new MySqlLiteHelper(this);
//        for(City c : db.getAllCities()) {
//            db.deleteCity(c);
//        }
        int count = db.getRowCount();
        Log.e("___Count Row", ""+count);
        if(count < 10) {

            content = new ArrayList<>();

            new DownloadCityFile(db).execute();
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
            try {
                String fileName = "cities.txt"; //The file that will be saved on your computer
                URL url = new URL("http://openweathermap.org/help/city_list.txt"); //The file that you want to download

                HttpURLConnection.setFollowRedirects(true);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(false);
                con.setReadTimeout(20000);
                con.setRequestProperty("Connection", "keep-alive");

                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:16.0) Gecko/20100101 Firefox/16.0");
                ((HttpURLConnection) con).setRequestMethod("GET");
                //System.out.println(con.getContentLength()) ;
                con.setConnectTimeout(5000);
                BufferedInputStream in = new BufferedInputStream(con.getInputStream());
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(in, StandardCharsets.UTF_8));
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    System.out.println(responseCode);
                }
                StringBuffer buffer = new StringBuffer();
                int chars_read;
                //int total = 0;
//                FileOutputStream outputStream;
//                outputStream = openFileOutput(CITIES_FILE, Context.MODE_PRIVATE);
                String line = r.readLine();
                while (line != null)
                {

//                    Log.e("line", line);
//                    buffer.append(line);
                    page.add(line);

                    line = r.readLine();
                }
//                outputStream.write(buffer.toString().getBytes());
//                outputStream.close();
            }
            catch (IOException e) {
                Log.e("SplashScreen", "" + e.getMessage());
            }
            return page;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            super.onPostExecute(list);

            Log.e("____________s", ""+list.size());
            content.addAll(list);

            int i = 1;
            while(content.get(i)!= null && i < 100) {

                String s = content.get(i);
                String[] array = s.split("\t");

                Log.e("addCity", array[1] + ", " + array[4]);
                db.addCity(new City(array[1], array[4]));
                i++;
            }
        }
    }
}
