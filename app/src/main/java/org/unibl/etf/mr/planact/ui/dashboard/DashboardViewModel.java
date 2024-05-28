package org.unibl.etf.mr.planact.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.unibl.etf.mr.planact.activitydb.model.Activity;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel extends ViewModel {



    private final MutableLiveData<List<Activity>> items;

    public DashboardViewModel() {

        items = new MutableLiveData<>();
        items.setValue(new ArrayList<>());
    }

    public LiveData<List<Activity>> getItems(){
        return items;
    }

    public void addItem(Activity activity){

        List<Activity> currentList = items.getValue();
        currentList.add(activity);
        items.setValue(currentList);



    }

}