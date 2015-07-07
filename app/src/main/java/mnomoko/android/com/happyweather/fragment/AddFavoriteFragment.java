package mnomoko.android.com.happyweather.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.algorithme.CustomAutoCompleteTextViewDB;
import mnomoko.android.com.happyweather.algorithme.CustomTextChangeListner;
import mnomoko.android.com.happyweather.algorithme.PlaceJSONParser;
import mnomoko.android.com.happyweather.data.loader.DataLoader;
import mnomoko.android.com.happyweather.database.City;
import mnomoko.android.com.happyweather.database.MySqlLiteHelper;

/**
 * Created by mnomoko on 28/06/15.
 */
public class AddFavoriteFragment extends DialogFragment {

    Map<String, String> countries;

    AutoCompleteTextView atvPlacesFav;
    PlacesTask placesTask;
    ParserTask parserTask;

//    View root;
    Button btnSave;

    public String ville;
    SharedPreferences sharedpref;

    Handler handler;
    //---------------------------------------


    View view;
    EditText city;
    String citizen;

    MySqlLiteHelper databaseH;
    CustomAutoCompleteTextViewDB myAutoComplete;
    ArrayAdapter<City> myAdapter;

    View root;

    RelativeLayout searchLayout;
    LinearLayout resultLayout;
    RelativeLayout layout;

    ResultFragment resultFragment = null;

    public AddFavoriteFragment() {}

    public AddFavoriteFragment(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        handler.sendEmptyMessage(0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        root = getActivity().getLayoutInflater().inflate(R.layout.add_favorite_fragment, null);

        sharedpref = getActivity().getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);

        resultLayout = (LinearLayout) root.findViewById(R.id.result_container);
        searchLayout = (RelativeLayout) root.findViewById(R.id.search_container);

        try{

            // instantiate database handler
            databaseH = new MySqlLiteHelper((DrawerActivity)getActivity());

            // autocompletetextview is in activity_main.xml
            myAutoComplete = (CustomAutoCompleteTextViewDB) root.findViewById(R.id.myautocomplete);

//            tvWriteText = (TextView) root.findViewById(R.id.editTextCitySearch);

            myAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                    LinearLayout rl = (LinearLayout) arg1;
                    TextView tv = (TextView) rl.getChildAt(0);
                    TextView tv2 = (TextView) rl.getChildAt(1);
                    myAutoComplete.setText(tv.getText().toString());

                    String city = tv.getText().toString() + "," + tv2.getText().toString().toUpperCase();

                    run(tv.getText().toString(), tv2.getText().toString());
//                    Bundle bundle=new Bundle();
//                    bundle.putString("city", city);
//
//                    if(resultFragment == null) {
//                        resultFragment = new ResultFragment();
//                    }
//                    else {
//                        getChildFragmentManager().beginTransaction().remove(resultFragment).commit();
//                        resultFragment = new ResultFragment();
//                    }
//                    //set Fragmentclass Arguments
//                    resultFragment.setArguments(bundle);
//
//
//
//                    getChildFragmentManager().beginTransaction().add(R.id.result_container, resultFragment).commit();
//
//
////                    resultFragment.changeCity(city);
//
//                    resultLayout.setVisibility(View.VISIBLE);
//                    searchLayout.setVisibility(View.GONE);
//
//                    View view = getActivity().getCurrentFocus();
//                    if (view != null) {
//                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
//                                Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    }
//
////                    getChildFragmentManager().beginTransaction().add(R.id.container,  resultFragment).commit();
//                    getChildFragmentManager().executePendingTransactions();
//
//                    Log.e("Unknown TEST", tv.getText().toString() + " : " + tv2.getText().toString());
                }

            });

            // add the listener so it will tries to suggest while the user types
            myAutoComplete.addTextChangedListener(new CustomTextChangeListner((DrawerActivity)getActivity()));

//            // ObjectItemData has no value at first
//            City[] ObjectItemData = new City[0];
//
//            // set the custom ArrayAdapter
//            myAdapter = new AutocompleteDBCustomArrayAdapter((DrawerActivity)getActivity(), ObjectItemData);
            myAdapter = ((DrawerActivity) getActivity()).getArrayAdapter();
            ((DrawerActivity) getActivity()).setMyAutoComplete(myAutoComplete);
            myAutoComplete.setAdapter(myAdapter);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        btnSave = (Button) root.findViewById(R.id.addFavoSave);
//        btnSave.setEnabled(false);
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                run();
//                getDialog().dismiss();
//            }
//        });
//
//        countries = new HashMap<String, String>();
//        for (String iso : Locale.getISOCountries()) {
//            Locale l = new Locale("", iso);
//            countries.put(l.getDisplayCountry().toUpperCase(), iso); //IS ADDED TO UPPERCASE
//        }
//
//        atvPlacesFav = (AutoCompleteTextView) root.findViewById(R.id.atv_places_add_fav);
//        atvPlacesFav.setThreshold(1);
//
//        atvPlacesFav.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                placesTask = new PlacesTask();
//                placesTask.execute(s.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String value = atvPlacesFav.getText().toString();
//                if(value.contains(",")) {
//                    value = value.split(",")[1];
//                    Log.e("AddFavoriteFragment", value.replace(" ", "").toUpperCase());
//                    if (countries.containsKey(value.replace(" ", "").toUpperCase())) {
//                        btnSave.setEnabled(true);
//                    }
//                }
//            }
//        });

        builder.setView(root);

        return builder
                // Set Dialog Icon
                .setIcon(R.drawable.ic_launcher)
                .setMessage(R.string.addCity)
                .create();
    }

    public ArrayAdapter<City> getArrayAdapter() {
        return myAdapter;
    }

    public void setArrayAdapter(ArrayAdapter<City> adapter) {
        myAdapter = adapter;
    }

    public CustomAutoCompleteTextViewDB getMyAutoComplete() {
        return myAutoComplete;
    }

    public void setMyAutoComplete(CustomAutoCompleteTextViewDB autoComplete) {
        myAutoComplete = autoComplete;
    }

    public void showSearchFragment() {
        resultLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);
        myAutoComplete.setText("");

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void run(String vill, String pays){

        //Toast.makeText(AddFavoriteCity.this, vill + "," + pays, Toast.LENGTH_LONG).show();
        ville = vill + "," + pays;
//        ville = ville.replace(" ", "-");
        new RequestTaskChecker((DrawerActivity)getActivity()).execute(ville);

    }

    public class RequestTaskChecker extends AsyncTask<String, String, String> {

        /** application context. */
        private Context context;

        private ProgressDialog dialog;
        private DrawerActivity activity;
        public RequestTaskChecker(DrawerActivity activity) {
            this.activity = activity;
            context = activity;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... uri) {

            String content = DataLoader.getWeatherCity(uri[0]);
            Log.e("uri[0]", uri[0]);

            return content;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Chargement..");
            this.dialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("onPostExecute", "enter");
            if(result != null) {
                Log.e("onPostExecute", "on first if");

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getInt("cod") == 200 ) {
//                    if (jsonObject.has("message") && !jsonObject.isNull("message")) {
                        // Use the data
//                        if(sharedpreferences == null) {
//                        sharedpref =  getActivity().getSharedPreferences(DrawerActivity.APP_DATA, Context.MODE_PRIVATE);
//                        }
                        SharedPreferences.Editor editor = sharedpref.edit();
                        editor.putString(DrawerActivity.APP_DATA_LIVING_CITY, ville);

                        editor.commit();
//                        Intent it = new Intent(AddFavoriteCity.this, Accueil.class);
//                        it.putExtra("name", sharedpreferences.getString(City, ""));
//                        startActivity(it);
                    }
                    else {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        activity.dialogErrorCity();
                    }
                } catch (JSONException e) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    activity.dialogErrorCity();
                    e.printStackTrace();
                }
            }
            else {
                activity.dialogErrorCity();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }



    // Fetches all places from GooglePlaces AutoComplete Web Service
    public class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyDPUXBDbqyDrirRomdxQm971kvgbnI1vAg";

            String input="";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=(cities)";
//            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input+"&"+types+"&"+sensor+"&"+key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

            try{
                // Fetching the data from we service
                data = downloadUrl(url);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }
    /** A class to parse the Google Places in JSON format */
    public class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            //test = places.toString();
            //Log.e("JSON_PlacesAPI", test);
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[] { "description"};
            int[] to = new int[] { android.R.id.text1 };

            // Creating a SimpleAdapter for the AutoCompleteTextView
            SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            atvPlacesFav.setAdapter(adapter);
        }
    }

    /** A method to download json data from url */
    public String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception url", e.getMessage());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
