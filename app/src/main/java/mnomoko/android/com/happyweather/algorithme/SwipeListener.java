package mnomoko.android.com.happyweather.algorithme;

import android.view.MotionEvent;
import android.widget.ViewFlipper;

/**
 * Created by mnomoko on 04/07/15.
 */
public interface SwipeListener {

    boolean dispatchTouchEvent(MotionEvent event);

    void swipeLeft(ViewFlipper viewFlipper);
    void swipeRight(ViewFlipper viewFlipper);
}
