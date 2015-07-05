package mnomoko.android.com.happyweather.algorithme;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by mnomoko on 05/07/15.
 */
public class CustomAutoCompleteTextViewDB extends AutoCompleteTextView {

    public CustomAutoCompleteTextViewDB(Context context) {
        super(context);
    }

    public CustomAutoCompleteTextViewDB(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomAutoCompleteTextViewDB(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }
}

