package org.unibl.etf.mr.planact.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.activitydb.model.Location;
import org.unibl.etf.mr.planact.exceptions.ThrottleException;

import java.net.URL;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlacesUtil {
    //cekaj po sekundu

    public static class GeocodeService {


        public JsonArrayRequest scheduledTask = null;

        public Object scheduledTaskLock = new Object();
        private Context context;

        public int intervalBetweenRequestsInSeconds = 1;

        public LocalDateTime lastRequestTime = LocalDateTime.now().minusSeconds(intervalBetweenRequestsInSeconds);

        private String baseURL = "https://geocode.maps.co/search";
        private String firstParameterName = "q";
        private String secondParameterName = "api_key";
        private String apiKey;
        public RequestQueue queue;

        public String createURL(String query) {
            Uri.Builder builder = Uri.parse(baseURL).buildUpon();
            builder.appendQueryParameter(firstParameterName, query);
            builder.appendQueryParameter(secondParameterName, apiKey);

            return builder.build().toString();
        }

        public GeocodeService(Context context) {
            this.context = context;
            this.queue = Volley.newRequestQueue(context);
            this.apiKey = context.getResources().getString(R.string.geocode_maps_api_key);
        }


    }


}
