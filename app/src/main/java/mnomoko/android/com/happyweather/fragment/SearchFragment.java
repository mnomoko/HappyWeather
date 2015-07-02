package mnomoko.android.com.happyweather.fragment;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import mnomoko.android.com.happyweather.R;

/**
 * Created by mnomoko on 28/06/15.
 */
public class SearchFragment extends Fragment {

    View view;
    EditText city;
    String citizen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setContentView(R.layout.search_weather);

        View root = inflater.inflate(R.layout.search_location, null);

        ViewGroup parent = (ViewGroup) root.findViewById(R.id.container);

        city = (EditText) root.findViewById(R.id.editTextCitySearch);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void finder(View view){
        citizen  = city.getText().toString();

        String uri = "http://www.openweathermap.org/data/2.5/weather?q=" + citizen;

        new RequestTask().execute(uri);
        Intent it = new Intent(getActivity(), null);
        it.putExtra("city", citizen);
        startActivity(it);

    }

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
                    if (msg.equalsIgnoreCase("Error: Not found city")) {
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
                        it.putExtra("city", citizen);
                        startActivity(it);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
