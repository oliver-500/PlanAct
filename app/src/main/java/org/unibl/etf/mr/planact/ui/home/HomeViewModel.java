package org.unibl.etf.mr.planact.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.threeten.bp.LocalDateTime;
import org.unibl.etf.mr.planact.AppController;
import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.activitydb.model.Location;
import org.unibl.etf.mr.planact.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {



    private MutableLiveData<List<Activity>> items =  new MutableLiveData<>();

    private MutableLiveData<TreeSet<Activity>> notificationItems =  new MutableLiveData<>();

    public HomeViewModel() {
        items.setValue(new ArrayList<>());
        suggestions.setValue(new ArrayList<>());
        this.notificationItems.setValue(items.getValue().stream().collect(Collectors.toCollection(() -> new TreeSet<Activity>((i1, i2) -> {
            if (i2.getTime().compareTo(i1.getTime()) == 0)
                return (int) (i1.getActivity_id() - i2.getActivity_id());
            return i1.getTime().compareTo(i2.getTime());
        }))));

    }

    private void filterNotificationItems(){
        this.notificationItems.getValue().clear();
        this.notificationItems.getValue().addAll(this.items.getValue().stream().filter(i -> (LocalDateTime.now().plusHours(Constants.notificationMaxNumberOfHours[2]).compareTo(i.getTime()) > 0
                && LocalDateTime.now().compareTo(i.getTime()) < 0) && !i.getDone()).collect(Collectors.toList()));

    }

    public void loadActivities(){
        items.getValue().clear();

        items.getValue().addAll(AppController.getmInstance().getDatabase().getActivityDao().getActivities());

    }

    public void load(){
        loadActivities();
        filterNotificationItems();
    }


    public void addSuggestions(List<String> newSuggestions){
        suggestions.setValue(newSuggestions);
    }
    public void searchItemsByTitle(String title){
        items.getValue().clear();
        items.getValue().addAll(AppController.getmInstance().getDatabase().getActivityDao().search(title));

    }
    public MutableLiveData<List<String>> suggestions =  new MutableLiveData<>();
    public LiveData<List<Activity>> getItems(){
        return items;
    }

    public LiveData<TreeSet<Activity>>getNotificationItems (){ return notificationItems;}
    public LiveData<List<String>> getSuggestions() {return suggestions;}

    public void addItem(Activity activity){

        List<Activity> currentList = items.getValue();
        currentList.add(activity);
        items.setValue(currentList);

    }

    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private Activity editingItem;

    public void setEditingItem(Activity activity){
        this.editingItem = activity;
    }

    public Activity getEditingItem(){
        return editingItem;
    }

    public void clearEditingItem(){
        this.editingItem = null;
    }


}