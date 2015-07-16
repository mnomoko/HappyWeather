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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.algorithme.CustomAutoCompleteTextViewDB;
import mnomoko.android.com.happyweather.algorithme.CustomTextChangeListner;
import mnomoko.android.com.happyweather.data.loader.DataLoader;
import mnomoko.android.com.happyweather.database.City;
import mnomoko.android.com.happyweather.database.MySqlLiteHelper;

/**
 * Created by mnomoko on 28/06/15.
 */
public class AddFavoriteFragment extends DialogFragment {

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
//            myAutoComplete.setOnItemClickListener(new AdapterView.OnClickListener() {

//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        InputMethodManager imm = (InputMethodManager) getActivity()
//                                .getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
//                    }
//                    return false;
//                }}
//            );

            myAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {


                    if(((DrawerActivity) getActivity()).checkConnection()) {
                        LinearLayout rl = (LinearLayout) arg1;
                        TextView tv = (TextView) rl.getChildAt(0);
                        TextView tv2 = (TextView) rl.getChildAt(1);
                        myAutoComplete.setText(tv.getText().toString());

                        String city = tv.getText().toString() + "," + tv2.getText().toString().toUpperCase();

                        getChildFragmentManager().beginTransaction().detach(AddFavoriteFragment.this).commit();

                        run(tv.getText().toString(), tv2.getText().toString());
                    }
                    else {

                        myAutoComplete.setText("");
                        ((DrawerActivity) getActivity()).noConnection();
                    }
                }

            });

            // add the listener so it will tries to suggest while the user types
            myAutoComplete.addTextChangedListener(new CustomTextChangeListner((DrawerActivity)getActivity()));
//
//            // set the custom ArrayAdapter
            myAdapter = ((DrawerActivity) getActivity()).getArrayAdapter();
            ((DrawerActivity) getActivity()).setMyAutoComplete(myAutoComplete);
            myAutoComplete.setAdapter(myAdapter);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


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
        try {
            new RequestTaskChecker((DrawerActivity)getActivity()).execute(ville).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
                        AddFavoriteFragment.this.dismiss();
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
}
