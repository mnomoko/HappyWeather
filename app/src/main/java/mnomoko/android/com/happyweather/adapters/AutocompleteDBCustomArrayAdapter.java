package mnomoko.android.com.happyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
import mnomoko.android.com.happyweather.database.City;

/**
 * Created by mnomoko on 05/07/15.
 */
public class AutocompleteDBCustomArrayAdapter extends ArrayAdapter<City> {

    Context mContext;
    int layoutResourceId;
    City data[] = null;

    public AutocompleteDBCustomArrayAdapter(Context context, City[] objects) {
        super(context, R.layout.list_view_row, objects);

        this.mContext = context;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try{

            if(convertView==null){
                // inflate the layout
                LayoutInflater inflater = ((DrawerActivity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_view_row, parent, false);
            }

            // object item based on the position
            City objectItem = data[position];

            // get the TextView and then set the text (item name) and tag (item ID) values
            TextView textViewItem = (TextView) convertView.findViewById(R.id.tvNameNameCity);
            textViewItem.setText(objectItem.getName());
            TextView textViewCode = (TextView) convertView.findViewById(R.id.tvNameCodeCity);
            textViewCode.setText(objectItem.getCode());

            // in case you want to add some style, you can do something like:
            //textViewItem.setBackgroundColor(Color.WHITE);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;

    }
}
