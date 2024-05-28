package org.unibl.etf.mr.planact.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {
    private View disableScrollChild;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDisableScrollChild(View child) {
        this.disableScrollChild = child;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isTouchInsideView(ev, disableScrollChild)) {
            // Disable scrolling if the touch is inside the specified child
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isTouchInsideView(MotionEvent ev, View view) {
        if (view == null) {
            return false;
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        float x = ev.getRawX();
        float y = ev.getRawY();

        return x > location[0] && x < location[0] + view.getWidth() &&
                y > location[1] && y < location[1] + view.getHeight();
    }
}

