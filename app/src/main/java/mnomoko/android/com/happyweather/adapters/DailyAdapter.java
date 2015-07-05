package mnomoko.android.com.happyweather.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.model.Weather;

/**
 * Created by mnomoko on 02/07/15.
 */
public class DailyAdapter extends ArrayAdapter<Weather> {

    private LayoutInflater inflater;
    private List<Weather> data;

    public DailyAdapter(Context context, List<Weather> objects) {
        super(context, R.layout.weather_item, objects);

        inflater= LayoutInflater.from(context);
        this.data=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //if it's not create convertView yet create new one and consume it
        if(convertView == null){
            //instantiate convertView using our employee_list_item
            convertView = inflater.inflate(R.layout.weather_item, null);
            //get new ViewHolder
            holder =new ViewHolder();
            //get all item in ListView item to corresponding fields in our ViewHolder class
            holder.image=(ImageView) convertView.findViewById(R.id.imgViewWeatherList);
            holder.date =(TextView) convertView.findViewById(R.id.tvNameDay);
            holder.minmax =(TextView) convertView.findViewById(R.id.tvNameMinMax);
            //set tag of convertView to the holder
            convertView.setTag(holder);
        }
        //if it's exist convertView then consume it
        else {
            holder =(ViewHolder) convertView.getTag();
        }

        String ic = data.get(position).getImage();
//        new DownloadImageTask(holder.image).execute(ic + ".png");
        holder.image.setImageResource(getContext().getResources().getIdentifier("_"+ic, "drawable", getContext().getPackageName()));

//        holder.image.setImageResource(resID);
        holder.date.setText((CharSequence) data.get(position).getDate());
        StringBuilder sb = new StringBuilder();
        sb.append(data.get(position).getMin());
        sb.append(" C°");
        sb.append(" / ");
        sb.append(data.get(position).getMax());
        sb.append(" C°");
        holder.minmax.setText((CharSequence) sb.toString());
        //return ListView item
        return convertView;
    }

    //ViewHolder class that hold over ListView Item
    static class ViewHolder{
        TextView date;
        TextView minmax;
        ImageView image;
    }
}
