package mnomoko.android.com.happyweather.data.loader;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnomoko on 28/06/15.
 */
public class DataLoader {

    public static String getWeatherCity(String city, String country) {

        return "";
    }

    public static String getWeatherLocation(String lon, String lat) {
        //api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}


        String link = "http://api.openweathermap.org/data/2.5/weather?";

        String response = null;
        try {


            String request = link + "lat=" + lat + "&lon=" + lon + "&units=metric";
            Log.e("DataLoader", request);
//            String request = text + city + extend;
            URL url = new URL(request);
            Log.e("DataLoader", request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(true);

            int responseCode = conn.getResponseCode();

            if(responseCode == 200) {
                InputStream inputstream = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
                response = "";
                String line;
                while ((line = br.readLine()) != null){
                    response += line;
                }
                Log.e("JSONObject", response);
            }
        }
        catch (MalformedURLException m) {
            Log.e("DataLoader.class", "1 "+m.getMessage());
        }
        catch (ProtocolException p) {
            Log.e("DataLoader.class", "2 "+p.getMessage());
        }
        catch (IOException i) {
            Log.e("DataLoader.class", "3 " + i.getMessage());
        }
//        catch (Exception e) {
//            Log.e("DataLoader.class", ""+e.getMessage());
//        }
        return response;
    }

    public static String getWeatherCity(String city) {

        String hourly = "http://api.openweathermap.org/data/2.5/forecast?q=paris,fr&units=metric&cnt=5";
        hourly = "http://api.openweathermap.org/data/2.5/forecast/hourly?q=";
        String hourlyExtend = "&units=metric&cnt=8";

        String daily = "http://api.openweathermap.org/data/2.5/forecast/daily?q=paris,fr&units=metric&cnt=5";
        daily = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
        String dailyExtend = "&units=metric&cnt=8";

        String text = "http://api.openweathermap.org/data/2.5/weather?q=";
        text = daily;
        String extend = "&units=metric&cnt=7";
        String response = null;

        try {

//            File f = new File(city);
//            if(f.exists() && !f.isDirectory()) {
//
//                String cache = DrawerActivity.readCacheFile(city);
//                String[] array = cache.split("----------");
//                long timestamp = Long.parseLong(array[0]);
//                String value = array[1];
//
//                Calendar calendar = Calendar.getInstance();
//                java.util.Date now = calendar.getTime();
//                java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
//
//                if(timestamp < currentTimestamp.getTime() - 600) {
//                    if (value != null)
//                        return value;
//                }
//
//            }

            String request = daily + city + dailyExtend;
//            String request = text + city + extend;
            URL url = new URL(request);
            Log.e("DataLoader", request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(true);

            int responseCode = conn.getResponseCode();

            if(responseCode == 200) {
                InputStream inputstream = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputstream));
                response = "";
                String line;
                while ((line = br.readLine()) != null){
                    response += line;
                }
                Log.e("JSONObject", response);
            }


//            long t = System.currentTimeMillis();
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append(t);
//            stringBuilder.append("----------");
//            stringBuilder.append(response);
//
//            DrawerActivity.writeCacheFile(city, stringBuilder.toString());



        }
        catch (MalformedURLException m) {
            Log.e("DataLoader.class", "1 "+m.getMessage());
        }
        catch (ProtocolException p) {
            Log.e("DataLoader.class", "2 "+p.getMessage());
        }
        catch (IOException i) {
            Log.e("DataLoader.class", "3 " + i.getMessage());
        }
//        catch (Exception e) {
//            Log.e("DataLoader.class", ""+e.getMessage());
//        }
        return response;
    }

    public static List<String> loadCitiesFile() {

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
                    new InputStreamReader(in, /*StandardCharsets.UTF_8*/ "UTF-8"));
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
}
