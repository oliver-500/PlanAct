package org.unibl.etf.mr.planact.list;


import static android.graphics.Color.rgb;

import static com.google.android.material.checkbox.MaterialCheckBox.STATE_CHECKED;
import static com.google.android.material.checkbox.MaterialCheckBox.STATE_UNCHECKED;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;
import org.unibl.etf.mr.planact.AppController;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.activitydb.ActivityDatabase;
import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.util.CalendarUtil;
import org.unibl.etf.mr.planact.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class NotificationListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static int periodPositionCounter = 0;

    private int numberOfExtraViews = 0;

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_EXTRA = 1;
    public TreeSet<Activity> items;

    private Context context;
    private ActivityDatabase database = AppController.getmInstance().getDatabase();

    public void setItems(TreeSet<Activity> items) {

        periodPositionCounter = 0;
        this.items = items;
        calculateExtraViewsPositions();
    }


    public void addItem(Activity item) {
        this.items.add(item);
    }


    private MainListAdapter.OnItemClickListener listener;

    public NotificationListAdapter(Context context, TreeSet<Activity> items, MainListAdapter.OnItemClickListener onClick) {
        this.context = context;

        this.items = items;

        periodPositionCounter = 0;

        calculateExtraViewsPositions();
        listener = onClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                View normalView = inflater.inflate(R.layout.notification_list_item, parent, false);
                return new ActivityItemViewHolder(normalView);

            case VIEW_TYPE_EXTRA:
                View extraView = inflater.inflate(R.layout.notification_period_layout, parent, false);
                return new PeriodItemViewHolder(extraView);

            default:
                throw new IllegalArgumentException("Invalid view type");
        }


    }

    public class ActivityItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvDueToPeriod;
        public View layout;
        public MaterialCheckBox cbIsDone;
        public boolean isExpanded = false;

        public ActivityItemViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            cbIsDone = itemView.findViewById(R.id.cbIsDoneNotification);


            tvDueToPeriod = itemView.findViewById(R.id.tvDueToPeriod);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    public class PeriodItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPeriod;
        public View layout;


        int periodPosition = 0;
        public boolean isExpanded = false;

        public PeriodItemViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            tvPeriod = itemView.findViewById(R.id.tvNotificationPeriod);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_NORMAL: {
                ActivityItemViewHolder activityItemViewHolder = (ActivityItemViewHolder) holder;
                Activity item = new ArrayList<>(items).get(position - numberOfExtraViews);
                activityItemViewHolder.tvName.setText(item.getName());
                String date = CalendarUtil.formatDateForView(item.getTime().toString());
                activityItemViewHolder.tvDueToPeriod.setText(date);
                holder.itemView.setOnClickListener(v -> {
                    listener.onItemClick(item);
                });
                activityItemViewHolder.cbIsDone.addOnCheckedStateChangedListener((checkBox, state) ->
                {
                    if(state == STATE_CHECKED){
                        item.setDone(true);
                        database.getActivityDao().updateActivity(item);
                    }
                    else if(state == STATE_UNCHECKED){
                        item.setDone(false);
                        database.getActivityDao().updateActivity(item);

                    }
                });
                break;
            }
            case VIEW_TYPE_EXTRA: {
                PeriodItemViewHolder activityItemViewHolder = (PeriodItemViewHolder) holder;
                numberOfExtraViews++;
                activityItemViewHolder.periodPosition = periodPositionCounter++;
                int severityLevel = activityItemViewHolder.periodPosition;

                int color = -1;
                String title = "";
                switch (severityLevel) {
                    case 0: {
                        //narandzasto
                        color = rgb(242, 179, 255);
                        title = context.getString(R.string.period_message_first);

                        break;
                    }
                    case 1: {
                        //zuto
                        color = rgb(153, 238, 255);
                        title = context.getString(R.string.period_message_second);

                        break;
                    }
                    case 2: {
                        //sivo
                        color = Color.LTGRAY;
                        title = context.getString(R.string.period_message_third);
                        break;
                    }
                }
                activityItemViewHolder.layout.setBackgroundColor(color);
                activityItemViewHolder.tvPeriod.setText(title);
                break;
            }
        }

    }

    private int[] positionsOfExtraViews = new int[Constants.notificationMaxNumberOfHours.length];

    public void calculateExtraViewsPositions() {

        LocalDateTime now = LocalDateTime.now();
        ArrayList<Activity> itemsList = new ArrayList<>(items);

        int position = 0;

        int numberOfExtraViews = 0;
        positionsOfExtraViews[0] = position;
        numberOfExtraViews++;

        long minutesDifference = -1;
        if(itemsList.size() != 0) minutesDifference = ChronoUnit.MINUTES.between(now, itemsList.get(position).getTime());

        while(minutesDifference >= 0 && minutesDifference <= 59){

            position++;
            if(position == itemsList.size()) break;

            minutesDifference = ChronoUnit.MINUTES.between(now, itemsList.get(position).getTime());

        }

        positionsOfExtraViews[1] = position + numberOfExtraViews;
        numberOfExtraViews++;

        while(minutesDifference >= 60 && minutesDifference <= (60 * 24)){

            position++;
            if(position == itemsList.size()) break;

            minutesDifference = ChronoUnit.MINUTES.between(now, itemsList.get(position).getTime());
        }

        positionsOfExtraViews[2] = position + numberOfExtraViews;



    }

    @Override
    public int getItemCount() {
        return items.size() + Constants.notificationMaxNumberOfHours.length;
    }

    boolean wasPreviousViewExtraView = false;

    @Override
    public int getItemViewType(int position) {


        for (int pos : positionsOfExtraViews) {
            if (pos == position) return VIEW_TYPE_EXTRA;
        }
        return VIEW_TYPE_NORMAL;

    }
}
