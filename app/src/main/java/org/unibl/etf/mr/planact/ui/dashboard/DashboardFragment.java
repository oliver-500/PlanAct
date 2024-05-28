package org.unibl.etf.mr.planact.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDateTime;
import org.unibl.etf.mr.planact.AppController;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.databinding.FragmentDashboardBinding;
import org.unibl.etf.mr.planact.list.MainListAdapter;
import org.unibl.etf.mr.planact.ui.home.HomeViewModel;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DashboardFragment extends Fragment implements  MainListAdapter.OnItemClickListener{

    private FragmentDashboardBinding binding;

    View root ;
    FrameLayout loadingLayout;
    ConstraintLayout cl;
    List<Calendar> selectedDays = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView recyclerView;
    private MainListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private DashboardViewModel dashboardViewModel;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);


        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        loadingLayout = root.findViewById(R.id.loadingLayout);
        cl = root.findViewById(R.id.cll);


        
        MaterialCalendarView mcv = root.findViewById(R.id.calendarView);


//        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Toast.makeText(requireContext(), "dadaa", Toast.LENGTH_LONG).show();
//                // ConstraintLayout has been laid out and is ready to be shown
//                // Hide the loading icon and show the ConstraintLayout
//                loadingLayout.setVisibility(View.INVISIBLE);
//                cl.setVisibility(View.VISIBLE);
//
//                // Remove the listener to prevent multiple invocations
//                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });




        mcv.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);



        mcv.setOnDateChangedListener((p1, p2, p3) ->
        {

            dashboardViewModel.getItems().getValue().clear();
            mAdapter.notifyDataSetChanged();

            Calendar calendar23 = p2.getCalendar();
            LocalDate localDate = LocalDate.of(calendar23.get(Calendar.YEAR), calendar23.get(Calendar.MONTH) + 1, calendar23.get(Calendar.DAY_OF_MONTH));
            List<Activity> activities = AppController.getmInstance().getDatabase().getActivityDao().getActivitiesWithDate(localDate.toString());

            for(Activity a : activities){
                dashboardViewModel.getItems().getValue().add(a);

                ;
            }
            mAdapter.setItems(activities);
            mAdapter.notifyDataSetChanged();

        });

        mcv.setOnDateLongClickListener((p1, p2) -> {
            Toast.makeText(this.getContext(), "Select a date to see corresponding activities", Toast.LENGTH_LONG).show();
        });

        List<Activity> activities2 = AppController.getmInstance().getDatabase().getActivityDao().getActivities();

        for (Activity a : activities2) {
            CustomDayDecorator customDecorator = new CustomDayDecorator(this.getContext());
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = a.getTime().atZone(zoneId);

            LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
            LocalDate localDate = localDateTime.toLocalDate();
            //Calendar c = GregorianCalendar.from(zonedDateTime);

            customDecorator.addMarkedDate(CalendarDay.from(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth()));
            mcv.addDecorators(customDecorator);
//            customDecorator.addMarkedDate(CalendarDay.from(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)));
//            mcv.addDecorators(customDecorator);

        }

        recyclerView = root.findViewById(R.id.rwCalendar);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MainListAdapter(requireContext(), AppController.getmInstance().getDatabase().getActivityDao().getActivities(), this::onItemClick);
        recyclerView.setAdapter(mAdapter);

        dashboardViewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        });




        this.root = root;
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        loadingLayout.setVisibility(View.INVISIBLE);
        cl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Activity item) {
        homeViewModel.setEditingItem(item);
        NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putInt("action", 2);
        bundle.putInt("source", 2);
        navController.navigate(R.id.action_navigation_dashboard_to_navigation_details, bundle);

    }
}