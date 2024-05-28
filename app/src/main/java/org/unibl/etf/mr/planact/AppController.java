package org.unibl.etf.mr.planact;

import android.app.Application;



import org.unibl.etf.mr.planact.activitydb.ActivityDatabase;
import org.threeten.bp.zone.TzdbZoneRulesProvider;
public class AppController extends Application {

    private ActivityDatabase database;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();



        mInstance = this;
        database = ActivityDatabase.getInstance(getApplicationContext());
    }

    public static synchronized AppController getmInstance() {
        return mInstance;
    }

    public ActivityDatabase getDatabase() { return database; }
}
