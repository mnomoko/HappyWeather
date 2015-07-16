package mnomoko.android.com.happyweather.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import mnomoko.android.com.happyweather.R;
import mnomoko.android.com.happyweather.activities.DrawerActivity;
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

    View root;

    RelativeLayout searchLayout;
    LinearLayout resultLayout;
    RelativeLayout layout;

    ResultFragment resultFragment = null;

//    TextView tvWriteText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setContentView(R.layout.search_weather);

        setRetainInstance(true);

        root = inflater.inflate(R.layout.search_fragment, container, false);

        layout = (RelativeLayout) root.findViewById(R.id.background);

        DrawerActivity.setBackgroundView(layout, getActivity(), R.drawable.sun);

//        resultFragment=new ResultFragment();

//        ViewGroup parent = (ViewGroup) root.findViewById(R.id.container);

//        city = (EditText) root.findViewById(R.id.editTextCitySearch);

        resultLayout = (LinearLayout) root.findViewById(R.id.result_container);
        searchLayout = (RelativeLayout) root.findViewById(R.id.search_container);


        // instantiate database handler
        databaseH = new MySqlLiteHelper((DrawerActivity)getActivity());

        // autocompletetextview is in activity_main.xml
        myAutoComplete = (CustomAutoCompleteTextViewDB) root.findViewById(R.id.myautocomplete);

        if(savedInstanceState != null) {

            resultLayout.setVisibility(View.GONE);
            searchLayout.setVisibility(View.VISIBLE);
        }
        else {

            try{


//            tvWriteText = (TextView) root.findViewById(R.id.editTextCitySearch);

                myAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                        //Check is connection network has been find
                        if(((DrawerActivity)getActivity()).checkConnection()) {

                            LinearLayout rl = (LinearLayout) arg1;
                            TextView tv = (TextView) rl.getChildAt(0);
                            TextView tv2 = (TextView) rl.getChildAt(1);
                            myAutoComplete.setText(tv.getText().toString());

                            String city = tv.getText().toString() + "," + tv2.getText().toString().toUpperCase();

                            Bundle bundle = new Bundle();
                            bundle.putString("city", city);

                            if (resultFragment == null) {
                                resultFragment = new ResultFragment();
                            } else {
                                getFragmentManager().beginTransaction().remove(resultFragment);
                                resultFragment = new ResultFragment();
                            }
                            //set Fragmentclass Arguments
                            resultFragment.setArguments(bundle);


                            ((DrawerActivity) getActivity()).setResultFragment(resultFragment);

//                        getFragmentManager().beginTransaction().replace(R.id.result_container, resultFragment, ResultFragment.class.getSimpleName()).commit();


//                    resultFragment.changeCity(city);

                            resultLayout.setVisibility(View.VISIBLE);
                            searchLayout.setVisibility(View.GONE);

                            View view = getActivity().getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

//                    getChildFragmentManager().beginTransaction().add(R.id.container,  resultFragment).commit();
                            getChildFragmentManager().executePendingTransactions();

                            Log.e("Unknown TEST", tv.getText().toString() + " : " + tv2.getText().toString());
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
        }


//        return super.onCreateView(inflater, container, savedInstanceState);

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


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
}
