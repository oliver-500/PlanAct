package org.unibl.etf.mr.planact.services;


import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import org.threeten.bp.LocalDateTime;
import org.unibl.etf.mr.planact.AppController;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.activitydb.ActivityDatabase;
import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.ui.DetailsFragment;
import org.unibl.etf.mr.planact.ui.notifications.Notification;
import org.unibl.etf.mr.planact.ui.notifications.NotificationsFragment;
import org.unibl.etf.mr.planact.util.CalendarUtil;
import org.unibl.etf.mr.planact.util.Constants;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationService extends Service {

    private final String notificationHistoryFileName = "notification_history.txt";
    private Handler handler;

    private boolean isUpdateNeeded = false;
    private final IBinder binder = new LocalBinder();

    private NotificationListener notificationListener;
    List<Notification> notifications;

    private ActivityDatabase database = AppController.getmInstance().getDatabase();

    public boolean isUpdateNeeded() {
        return isUpdateNeeded;
    }

    public void setUpdateNeeded(boolean updateNeeded) {
        isUpdateNeeded = updateNeeded;
    }

    private void writeHistoryToFile(int[] intArray) {
        String filename = "int_array_file.txt";

        try (FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(intArray);
            // Optionally, you can also flush and close the streams if not using try-with-resources

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] readHistoryFromFile() {

        try (FileInputStream fis = openFileInput(notificationHistoryFileName);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (int[]) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return new int[0]; // Return an empty array if there's an error
    }


    public NotificationListener getNotificationListener() {
        return notificationListener;
    }

    public void setNotificationListener(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }

    public interface NotificationListener {
        void onNotificationDataChange(String newData);
    }

    Context context;
    List<Notification> newNotifications = new ArrayList<>();

    private void fetchData() {


        List<Activity> activities = database.getActivityDao().getActivities();
        int[] shownNotificationsIds = readHistoryFromFile();

        int numberOfHours = -1;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedValue = sharedPreferences.getString("period", getString(R.string.default_period));

        switch(selectedValue){
            case "1 hour": {
                numberOfHours = Constants.notificationMaxNumberOfHours[0];
                break;
            }
            case "1 day":{
                numberOfHours = Constants.notificationMaxNumberOfHours[1];
                break;
            }
            case "7 days":{
                numberOfHours = Constants.notificationMaxNumberOfHours[2];
                break;
            }
            default: {
                numberOfHours = Constants.notificationMaxNumberOfHours[2];
            }
        }

        final int notificationNumberOfHours = numberOfHours;

        newNotifications = activities.stream().filter(a -> {
            boolean isFound = false;
            for (int i = 0; i < shownNotificationsIds.length; i++) {
                if (shownNotificationsIds[i] == a.getActivity_id()) {
                    isFound = true;
                    break;
                }
            }
            return (!isFound && !a.getDone());
        }).filter(i -> (LocalDateTime.now().plusHours(notificationNumberOfHours).compareTo(i.getTime()) > 0
                && LocalDateTime.now().compareTo(i.getTime()) < 0)).map(a -> new Notification(a.getName(), a.getTime(), (int) a.getActivity_id())).collect(Collectors.toList());


        if (newNotifications.size() > 0) {
            Log.d("notigeng", "veci od nula ");
            isUpdateNeeded = true;
        } else Log.d("notigeng", "nula ");
        //dovuci iz base trenutne podatke i provjeriti da li su drugaciji od onih u memoriji a isto provjeriti da li je notifikacija vec prikazana, pritom paziti koji period notifikacija je podesen u podesavanjima
    }


    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.myLooper());
    }

    public class LocalBinder extends Binder {
        NotificationService getService() {
            return NotificationService.this;
        }
    }

    private void showNotifications() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "your_channel_id",
                    "Your Channel Name",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        for (int i = 0; i < newNotifications.size(); i++) {


            Notification noti = newNotifications.get(i);

            String date = CalendarUtil.formatDateForView(noti.getDate().toString());

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "your_channel_id")
                    .setSmallIcon(R.drawable.splash_icon)
                    .setContentTitle(noti.getTitle())
                    .setContentText(date)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            Intent intent = new Intent(this, NotificationsFragment.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
            builder.setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);



            notificationManager.notify(noti.getActivity_id(), builder.build());
        }


    }

    @Override
    public IBinder onBind(Intent intent) {


        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        boolean isSwitchedOn = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("toggle", false);
        if(!isSwitchedOn) return START_NOT_STICKY;

        handler.postDelayed(new Runnable() {
            int count = 0;
            int max = 3;
            @Override
            public void run() {

                if (!isUpdateNeeded) fetchData();
                if (isUpdateNeeded) {

                    showNotifications();
                    isUpdateNeeded = false;
                }
                count++;
//                if(count != max)
//                    handler.postDelayed(this, 60 * 100);
//                else  Log.d("notigeng", "gasim ");
            }

            ;
        }, 1 * 1000);
        // Every 1 minute
        return START_NOT_STICKY;
    }
}
