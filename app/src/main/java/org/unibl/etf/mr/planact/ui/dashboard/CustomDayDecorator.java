package org.unibl.etf.mr.planact.ui.dashboard;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.unibl.etf.mr.planact.R;

import java.util.HashSet;

public class CustomDayDecorator implements DayViewDecorator {
    private HashSet<CalendarDay> markedDates;

    Context context;

    public CustomDayDecorator(Context context) {
        markedDates = new HashSet<>();
        this.context = context;
    }

    public void addMarkedDate(CalendarDay date) {
        markedDates.add(date);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Check if the current day should be decorated
        return markedDates.contains(day);
    }



    @Override
    public void decorate(DayViewFacade view) {


        view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_circle_24));

    }
}