package org.unibl.etf.mr.planact;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.unibl.etf.mr.planact.databinding.ActivityMainBinding;
import org.unibl.etf.mr.planact.services.NotificationService;
import org.unibl.etf.mr.planact.services.NotificationServiceConnection;
import org.unibl.etf.mr.planact.ui.home.HomeViewModel;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    MenuItem customItem;
    private HomeViewModel sharedViewModel;
    public static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    NavController navController;
    private NotificationService notificationService;
    private NotificationServiceConnection serviceConnection;

    @Override
    protected void attachBaseContext(Context base) {
        // uzimamo trenutni jezik iz SharedPreferences-a
        SharedPreferences shPreferences = PreferenceManager.getDefaultSharedPreferences(base);
        String lang = shPreferences.getString(SELECTED_LANGUAGE, Locale.getDefault().getLanguage());

        // sacuvamo promjene u konfiguraciji aplikacije
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        base.getResources().updateConfiguration(config,
                base.getResources().getDisplayMetrics());
        super.attachBaseContext(base);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle navigation home icon click
        if (item.getItemId() == R.id.navigation_home) {
            // Perform your action here
            // For example, navigate to a specific destination

            binding.navView.getMenu().findItem(R.id.navigation_home).setChecked(true);
            return true;
        } else if (item.getItemId() == R.id.navigation_dashboard) {

            binding.navView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
        } else if (item.getItemId() == R.id.navigation_notifications) {
            binding.navView.getMenu().findItem(R.id.navigation_notifications).setChecked(true);
        }

        return super.onOptionsItemSelected(item);
    }

    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        Intent intent2 = new Intent(this, NotificationService.class);
        stopService(intent2);

    }


    private void requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            String permission = Manifest.permission.POST_NOTIFICATIONS; // Replace with the desired permission

            requestNotificationPermissionLauncher.launch(permission);
        } else {
            String permission = Manifest.permission.POST_NOTIFICATIONS; // Replace with the desired permission

            requestNotificationPermissionLauncher.launch(permission);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);






        requestNotificationPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Intent intent = new Intent(this, NotificationService.class);

                // Create a ServiceConnection
                serviceConnection = new NotificationServiceConnection();
               // serviceConnection.getMyBoundService().isUpdateNeeded();
                // Bind to the service
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);

                Intent intent2 = new Intent(this, NotificationService.class);
                startService(intent2);

            } else {
                Intent serviceIntent = new Intent(this, MainActivity.class);
                if(serviceConnection != null && serviceConnection.getMyBoundService() != null) serviceConnection.getMyBoundService().stopService(serviceIntent);
            }
        });



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            requestNotificationPermission();

        }
        else{
            if(isServiceRunning(NotificationService.class)){


            }
            else  {
                Intent intent = new Intent(this, NotificationService.class);

                // Create a ServiceConnection
                serviceConnection = new NotificationServiceConnection();
//            serviceConnection.getMyBoundService().isUpdateNeeded();
                // Bind to the service
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);


                Intent intent2 = new Intent(this, NotificationService.class);

                startService(intent2);

            }
        }

        sharedViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);


        setSupportActionBar(findViewById(R.id.toolbar));


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_settings).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
//
        binding.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_details) {

            } else {

            }
            return false;
        });

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.navigation_details) {
                NavBackStackEntry entry = navController1.getBackStackEntry(R.id.navigation_home);

                if (bundle != null) {
                    int source = bundle.getInt("source");
                    if (source == 1) {
                        binding.navView.getMenu().findItem(R.id.navigation_home).setChecked(true);
                    } else if (source == 2) {
                        binding.navView.getMenu().findItem(R.id.navigation_dashboard).setChecked(true);
                    }
                    else if(source == 3){
                        binding.navView.getMenu().findItem(R.id.navigation_notifications).setChecked(true);
                    }
                }

            }

        });

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        Menu menu = navView.getMenu();


        sharedViewModel.getNotificationItems().observe(this, items -> {


            if(items.size() > 0) {
                BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_notifications);
                badge.setNumber(items.size());
                badge.setVisible(true);
            }
            else {
                navView.removeBadge(R.id.navigation_notifications);

            }

        });




    }


}