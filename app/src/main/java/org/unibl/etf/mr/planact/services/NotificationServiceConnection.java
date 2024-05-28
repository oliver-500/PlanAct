package org.unibl.etf.mr.planact.services;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class NotificationServiceConnection implements ServiceConnection {
    private NotificationService myBoundService;

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        // Cast the IBinder to the LocalBinder and get the service instance
        NotificationService.LocalBinder binder = (NotificationService.LocalBinder) iBinder;
        myBoundService = binder.getService();

        // Now you can call methods on myBoundService
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        myBoundService = null;
    }

    public NotificationService getMyBoundService() {
        return myBoundService;
    }
}

