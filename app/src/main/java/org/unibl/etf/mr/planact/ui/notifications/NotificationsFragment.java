package org.unibl.etf.mr.planact.ui.notifications;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.threeten.bp.LocalDateTime;
import org.unibl.etf.mr.planact.AppController;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.databinding.FragmentNotificationsBinding;
import org.unibl.etf.mr.planact.list.MainListAdapter;
import org.unibl.etf.mr.planact.list.NotificationListAdapter;
import org.unibl.etf.mr.planact.ui.home.HomeViewModel;
import org.unibl.etf.mr.planact.util.Constants;

import java.util.TreeSet;
import java.util.stream.Collectors;

public class NotificationsFragment extends Fragment implements MainListAdapter.OnItemClickListener{

    private FragmentNotificationsBinding binding;
    private HomeViewModel sharedViewModel;

    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    private NotificationListAdapter nAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);



        // Get or create a BadgeDrawable for a specific menu item


        // Set badge properties





        recyclerView = root.findViewById(R.id.rvNotifications);

        sharedViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        sharedViewModel.load();



        nAdapter = new NotificationListAdapter(requireContext(), sharedViewModel.getNotificationItems().getValue(), this::onItemClick);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(nAdapter);



        sharedViewModel.getNotificationItems().observe(getViewLifecycleOwner(), items -> {
            nAdapter.setItems(items);

            if(nAdapter.items.size() > 0) {
                BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_notifications);
                badge.setNumber(nAdapter.items.size());
                badge.setVisible(true);
            }
            else {
                navView.removeBadge(R.id.navigation_notifications);
            }
            nAdapter.notifyDataSetChanged();
        });


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Activity item) {
        sharedViewModel.setEditingItem(item);

        NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putInt("action", 2);
        bundle.putInt("source", 3);
        navController.navigate(R.id.action_navigation_notifications_to_navigation_details, bundle);

    }

}