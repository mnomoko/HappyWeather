package mnomoko.android.com.happyweather.algorithme;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Created by mnomoko on 04/07/15.
 */
public class CustomViewFlipper extends ViewFlipper {
    public CustomViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

//        drawDots(canvas);
    }
}
