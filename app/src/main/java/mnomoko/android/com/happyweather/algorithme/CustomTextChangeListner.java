package mnomoko.android.com.happyweather.algorithme;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.adapters.AutocompleteDBCustomArrayAdapter;
import mnomoko.android.com.happyweather.database.City;

/**
 * Created by mnomoko on 05/07/15.
 */
public class CustomTextChangeListner implements TextWatcher {

    private Context context;

    public CustomTextChangeListner(Context context) {
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        try{

            // if you want to see in the logcat what the user types
            Log.e("Unknown", "User input: " + charSequence);

            DrawerActivity activity = ((DrawerActivity) context);

            // update the adapater
//            activity.myAdapter.notifyDataSetChanged();
            activity.getArrayAdapter().notifyDataSetChanged();

            // get suggestions from the database
            City[] myObjs = activity.databaseH.read(charSequence.toString());

            // update the adapter
//            activity.myAdapter = new AutocompleteDBCustomArrayAdapter(activity, myObjs);
            activity.setArrayAdapter(new AutocompleteDBCustomArrayAdapter(activity, myObjs));

//            activity.myAutoComplete.setAdapter(activity.myAdapter);
            activity.getMyAutoComplete().setAdapter(activity.getArrayAdapter());

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
