package mnomoko.android.com.happyweather.algorithme;

import android.content.Context;
import android.support.v7.widget.SearchView;

/**
 * Created by mnomoko on 05/07/15.
 */
public class CustomSearchView extends SearchView {

    private boolean expanded;

    public CustomSearchView(Context context) {
        super(context);
    }

    @Override
    public void onActionViewExpanded() {
        super.onActionViewExpanded();
        expanded = true;
    }

    @Override
    public void onActionViewCollapsed() {
        super.onActionViewCollapsed();
        expanded = false;
    }

    public boolean isExpanded() {
        return expanded;
    }
}
