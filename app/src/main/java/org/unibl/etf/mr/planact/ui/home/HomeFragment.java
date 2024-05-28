package org.unibl.etf.mr.planact.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.unibl.etf.mr.planact.AppController;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.databinding.FragmentHomeBinding;
import org.unibl.etf.mr.planact.list.MainListAdapter;

public class HomeFragment extends Fragment implements MainListAdapter.OnItemClickListener, MenuProvider {



    private FragmentHomeBinding binding;

    private RecyclerView recyclerView;
    private MainListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private HomeViewModel sharedViewModel;

    FrameLayout loadingLayout;
    ConstraintLayout mainLayout;

    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);




        loadingLayout = root.findViewById(R.id.loadingLayout);
        mainLayout = root.findViewById(R.id.clActivities);




        recyclerView = root.findViewById(R.id.rvActivities);

        FloatingActionButton fab = root.findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {


            NavController navController = NavHostFragment.findNavController(this);
            //workaround
            mainLayout.setVisibility(View.INVISIBLE);
            loadingLayout.setVisibility(View.VISIBLE);

            Bundle bundle = new Bundle();
            bundle.putInt("action", 1);
            bundle.putInt("source", 1);
            navController.navigate(R.id.action_navigation_home_to_navigation_details, bundle);

        });
        //dodati u listu itema


        layoutManager = new LinearLayoutManager(getActivity());


        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MainListAdapter(requireContext(), AppController.getmInstance().getDatabase().getActivityDao().getActivities(), this::onItemClick);
        recyclerView.setAdapter(mAdapter);

        sharedViewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        });

        sharedViewModel.load();
        this.root = root;
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        loadingLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private SearchView searchView;
    @Override
    public void onItemClick(Activity item) {
        sharedViewModel.setEditingItem(item);
        NavController navController = NavHostFragment.findNavController(this);
        Bundle bundle = new Bundle();
        bundle.putInt("action", 2);
        bundle.putInt("source", 1);
        navController.navigate(R.id.action_navigation_home_to_navigation_details, bundle);

    }


    @Override
    public void onPrepareMenu(@NonNull Menu menu) {
        MenuProvider.super.onPrepareMenu(menu);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.items_list_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_field);
        View actionView = searchItem.getActionView();
        searchView = actionView.findViewById(R.id.search_view);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                sharedViewModel.searchItemsByTitle(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >= 2)
                    sharedViewModel.searchItemsByTitle(newText);
                if(newText.length() == 0) sharedViewModel.load();
                return false;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                sharedViewModel.load();

                return true;
            }
        });


    }



    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.search_field) {


            return true;
        }

        else
            return false;
    }

}