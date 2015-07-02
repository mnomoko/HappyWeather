package mnomoko.android.com.happyweather.fragment;

import android.app.Fragment;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import mnomoko.android.com.happyweather.R;

/**
 * Created by mnomoko on 28/06/15.
 */
public class LocationFragment extends Fragment implements LocationListener {

    private TextView latituteField;
    private TextView longitudeField;
    private TextView addressField;
    private LocationManager locationManager;
    private String provider;
    private static Criteria criteria = new Criteria();

    TextView degres;
    TextView humidite;
    TextView vent;

    View view;
    TextView city;
    String add;
    private ImageView imgView;
    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    ImageView imgIcon;
    private static String MY_URL_STRING = "http://openweathermap.org/img/w/";

    public String weatherXML = null;

    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //setContentView(R.layout.activity_location);

        View root = inflater.inflate(R.layout.location_fragment, null);

        addressField = (TextView) root.findViewById(R.id.address);

        degres = (TextView) root.findViewById(R.id.textViewLocatDegres);
        humidite = (TextView) root.findViewById(R.id.textViewLocatHumidite);
        vent = (TextView) root.findViewById(R.id.textViewLocatVent);
        imgIcon= (ImageView) root.findViewById(R.id.imageView1);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);

        } else {
            //latituteField.setText("Not available !");
            //longitudeField.setText("Not available");
        }


        return root;

    }


    public void finder(String add){
        //add  = addressField.getText().toString();

        String uri = "http://api.openweathermap.org/data/2.5/weather?q=" + add;

        new RequestTask().execute(uri);
        //Intent it = new Intent(Malocation.this, Accueil.class);
        //it.putExtra("city", add);
        //startActivity(it);

    }

    public class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = null;

            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {

                weatherXML = result;

//                WeatherMatcher wm = new WeatherMatcher(weatherXML);
//
//                degres.setText((CharSequence) wm.getDegrees());
//                humidite.setText((CharSequence) wm.getHumidity());
//                vent.setText((CharSequence) wm.getWind());

//                String moncode = WeatherMatcher.getIcon();
//                new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
//                        .execute(MY_URL_STRING+moncode+".png");
            }
        }

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        locationManager.requestLocationUpdates(provider, 400, 1, getActivity());
//    }
//
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        locationManager.removeUpdates(getActivity());
//    }

    @Override
    public void onLocationChanged(Location location) {

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            //int maxLines = address.get(0).getMaxAddressLineIndex();
            //for (int i=0; i<maxLines; i++) {
            //String addressStr = address.get(0).getAddressLine(0);
            String maville = address.get(0).getLocality();
            builder.append(maville);
            Log.e("MyLocalization.class", maville);
//            finder(maville);
            //builder.append(" ");
            //}

            String fnialAddress = builder.toString(); //addresse complete.

            addressField.setText(fnialAddress); //addresse finale.
            finder(fnialAddress);
            Log.e("MyLocalization.class", fnialAddress);

        } catch (IOException e) {}
        catch (NullPointerException e) {}
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(String provider) {
        //Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        //Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
    }
}
