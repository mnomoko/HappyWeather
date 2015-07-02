package mnomoko.android.com.happyweather.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mnomoko.android.com.happyweather.R;

/**
 * Created by mnomoko on 01/07/15.
 */
public class HourlyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.hourly_fragment, null);


        return root;
    }
}
