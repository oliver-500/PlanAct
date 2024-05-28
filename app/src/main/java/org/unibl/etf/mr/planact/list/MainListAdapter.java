package org.unibl.etf.mr.planact.list;

import static com.google.android.material.checkbox.MaterialCheckBox.STATE_CHECKED;
import static com.google.android.material.checkbox.MaterialCheckBox.STATE_UNCHECKED;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;

import org.unibl.etf.mr.planact.AppController;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.activitydb.ActivityDatabase;
import org.unibl.etf.mr.planact.activitydb.model.Activity;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder>{


    public TreeSet<Activity> items;

    private LinearLayout layout;
//    private View root;
    private Context context;

    private ActivityDatabase database = AppController.getmInstance().getDatabase();

    public interface OnItemClickListener{
        void onItemClick(Activity item);
    }
    private OnItemClickListener listener;

    public void setItems(List<Activity> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public void addItem(Activity item){
        this.items.add(item);
    }

    public MainListAdapter(Context context, List<Activity> items, OnItemClickListener onClick) {
        this.context = context;
        this.items = new TreeSet<Activity>((i1, i2) ->{
            if(i2.getTime().compareTo(i1.getTime()) == 0) return (int)(i1.getActivity_id() - i2.getActivity_id());
            return i2.getTime().compareTo(i1.getTime());
        });
        this.setItems(items);

        listener = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_item, parent, false);

        layout = v.findViewById(R.id.LLItem);

        layout.setClipToOutline(true);
        ViewHolder vh = new ViewHolder(v);
//        root = v;
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MainListAdapter.ViewHolder holder, int position) {
        final Activity item = new ArrayList<>(items).get(position);

        holder.tvName.setText(item.getName());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDate date = null;
        LocalTime time = null;

        String dateTime = item.getTime().toString();
        String dateString = dateTime.split("T")[0];
        String timeString = dateTime.split("T")[1];
        try{

            date = LocalDate.parse(dateString, dateFormatter);

            time = LocalTime.parse(timeString, timeFormatter);
            LocalDateTime exactDateTime = date.atTime(time);
            holder.tvDateTime.setText(exactDateTime.format(dateTimeFormatter));
        }
        catch(DateTimeParseException e){


            return;
        }



        holder.cbIsDone.setChecked(item.getDone());

        holder.tvLocation.setText(item.getLocation());

        holder.cbIsDone.addOnCheckedStateChangedListener((checkBox, state) ->
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


//        holder.itemView.findViewById(R.id.ibDetails).setOnClickListener( v -> {
//            listener.onItemClick(item);
//        });
//        MaterialButton ibMoreDesc =  holder.itemView.findViewById(R.id.ibMoreDescription);
        holder.itemView.setOnClickListener(v -> {
//            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) layoutt.getLayoutParams();
//            layoutParams.height = (int) context.getResources().getDimension(R.dimen.expanded_list_item_size); // replace R.dimen.new_height with the desired height dimension resource
//            layoutt.setLayoutParams(layoutParams);

            listener.onItemClick(item);

//            if(holder.isExpanded){
//                holder.tvDescription.setMaxLines(1);dsa
//                //holder.tvDescription.setScrollContainer(false);
//                holder.isExpanded = false;
//                //ibMoreDesc.setVisibility(View.VISIBLE);
//
//            }
//            else {
//                holder.tvDescription.setMaxLines(5);
//                holder.tvDescription.setScrollContainer(true);
//                holder.isExpanded = true;
//                //ibMoreDesc.setVisibility(View.INVISIBLE);
//            }

        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvDateTime;
        public MaterialCheckBox cbIsDone;
        public TextView tvLocation;
        public View layout;
        public boolean isExpanded = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            cbIsDone = itemView.findViewById(R.id.cbIsDone);
            tvName = itemView.findViewById(R.id.tvName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}
