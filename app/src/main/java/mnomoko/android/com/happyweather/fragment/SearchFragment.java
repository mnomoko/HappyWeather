package mnomoko.android.com.happyweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.adapters.AutocompleteDBCustomArrayAdapter;
import mnomoko.android.com.happyweather.algorithme.CustomAutoCompleteTextViewDB;
import mnomoko.android.com.happyweather.algorithme.CustomTextChangeListner;
import mnomoko.android.com.happyweather.database.City;
import mnomoko.android.com.happyweather.database.MySqlLiteHelper;

/**
 * Created by mnomoko on 28/06/15.
 */
public class SearchFragment extends Fragment {

    View view;
    EditText city;
    String citizen;

    MySqlLiteHelper databaseH;
    CustomAutoCompleteTextViewDB myAutoComplete;
    ArrayAdapter<City> myAdapter;

//    TextView tvWriteText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setContentView(R.layout.search_weather);

        View root = inflater.inflate(R.layout.search_fragment, null);

//        ViewGroup parent = (ViewGroup) root.findViewById(R.id.container);

//        city = (EditText) root.findViewById(R.id.editTextCitySearch);

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

                    Bundle bundle=new Bundle();
                    bundle.putString("city", "city");
                    //set Fragmentclass Arguments
                    ResultFragment resultFragment=new ResultFragment();
                    resultFragment.setArguments(bundle);

                    getChildFragmentManager().beginTransaction().add(R.id.container,  resultFragment).commit();
                    getChildFragmentManager().executePendingTransactions();

                    Log.e("Unknown TEST", tv.getText().toString() + " : " + tv2.getText().toString());
                }

            });

            // add the listener so it will tries to suggest while the user types
            myAutoComplete.addTextChangedListener(new CustomTextChangeListner((DrawerActivity)getActivity()));

            // ObjectItemData has no value at first
            City[] ObjectItemData = new City[0];

            // set the custom ArrayAdapter
            myAdapter = new AutocompleteDBCustomArrayAdapter((DrawerActivity)getActivity(), ObjectItemData);
            myAutoComplete.setAdapter(myAdapter);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        return super.onCreateView(inflater, container, savedInstanceState);

        return root;
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

//    public void finder(View view) {
//        citizen = city.getText().toString();
//
//        String uri = "http://www.openweathermap.org/data/2.5/weather?q=" + citizen;
//
//        new RequestTask().execute(uri);
//        Intent it = new Intent(getActivity(), null);
//        it.putExtra("name", citizen);
//        startActivity(it);
//
//    }

/*
    public class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                HttpGet request = new HttpGet(uri[0]);
                Log.e("uri[0]", request.getURI().toString());
                response = httpclient.execute(request);
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    String content = EntityUtils.toString(response.getEntity());
                    responseString = content;
                    return responseString;
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Log.e("ClientProtocolException", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException", e.getMessage());
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String msg = jsonObject.getString("message");
                    if (msg.equalsIgnoreCase("Error: Not found name")) {
                        Log.e("TAG", "City not found");
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this entry?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        city.setText("");
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        // Use the data
                        Intent it = new Intent(getActivity(), null);
                        it.putExtra("name", citizen);
                        startActivity(it);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
*/
}