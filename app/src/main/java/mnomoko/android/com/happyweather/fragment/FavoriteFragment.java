package mnomoko.android.com.happyweather.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import mnomoko.android.com.happyweather.R;

/**
 * Created by mnomoko on 28/06/15.
 */
public class FavoriteFragment extends Fragment {

    private ViewFlipper viewFlipper;
    private Float lastX;
    ImageView image;
    TextView city;
    TextView degrees;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.favorite_fragment, null);
        viewFlipper = (ViewFlipper) root.findViewById(R.id.viewFlipper);



        return root;
    }

    private void setFlipperContent() {
//        List<Stuff> aux=getStuffList();
        int end = 0; //aux.size();

        for (int i = 0; i < end; i++) {
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.flipper_container, null);

            viewFlipper.addView(view);

            image = (ImageView) view.findViewById(R.id.imgViewWeather);
//            image.setBackgroundResource(aux.get(i).getImg());
            city = (TextView) view.findViewById(R.id.tvNameCity);
//            city.setText(aux.get(i).getName());
            degrees = (TextView) view.findViewById(R.id.tvNameDegrees);
//            degrees.setText("$" + aux.get(i).getCafPrice());
        }
        setFlipperAnimation();
    }

    private void setFlipperAnimation() {

    }
}
