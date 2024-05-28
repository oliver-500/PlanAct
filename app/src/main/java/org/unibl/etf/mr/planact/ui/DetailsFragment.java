package org.unibl.etf.mr.planact.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.unibl.etf.mr.planact.AppController;
import org.unibl.etf.mr.planact.R;
import org.unibl.etf.mr.planact.activitydb.ActivityDatabase;
import org.unibl.etf.mr.planact.activitydb.enums.ActivityType;
import org.unibl.etf.mr.planact.activitydb.model.Activity;
import org.unibl.etf.mr.planact.activitydb.model.Image;
import org.unibl.etf.mr.planact.activitydb.model.Location;
import org.unibl.etf.mr.planact.databinding.FragmentDetailsBinding;
import org.unibl.etf.mr.planact.services.NotificationService;
import org.unibl.etf.mr.planact.ui.home.HomeViewModel;
import org.unibl.etf.mr.planact.util.ImageLoadingTarget;
import org.unibl.etf.mr.planact.util.PlacesUtil;

import java.io.File;
import java.lang.ref.WeakReference;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.unibl.etf.mr.planact.util.ImageUtil;


public class DetailsFragment extends Fragment implements MenuProvider, OnMapReadyCallback {

    private HomeViewModel sharedViewModel;

    private ActivityResultLauncher<String> requestStoragePermissionLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestPhotosLauncher;

    private ActivityResultLauncher<Uri> takePictureLauncher;
    private final ActivityDatabase database = AppController.getmInstance().getDatabase();

    private FragmentDetailsBinding binding;
    private View root;

    private FrameLayout loadingLayout;
    private CustomScrollView mainLayout;

    private TextInputEditText tietDescription;
    private TextInputEditText tietTitle;
    private TextInputEditText tietDate;
    private TextInputEditText tietTime;
    private MaterialAutoCompleteTextView tietLocation;

    private static final int CAMERA_REQUEST = 1888;
    private final List<Image> tempImages = new ArrayList<>();

    private int oldNumberOfImages = 0;
    private ArrayAdapter<String> adapterLocations;

    private Uri recentUri;

    private Spinner spinner;
    private LinearLayout mainLayout2;

    PlacesUtil.GeocodeService geocodeService;
    private LinearLayout photosLayout;

    List<Location> locations = new ArrayList<>();

    Location previousLocation;

    public DetailsFragment() {
    }

    private ImageView addImageToContainer() {
        LinearLayout linearLayout = root.findViewById(R.id.photosListLayout);
        ImageView imageView = new ImageView(requireContext());

        int widthInDp = 130;
        int heightInDp = 130;
        float scale = getResources().getDisplayMetrics().density;
        int widthInPixels = (int) (widthInDp * scale + 0.5f);
        int heightInPixels = (int) (heightInDp * scale + 0.5f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthInPixels, heightInPixels);

        imageView.setBackgroundResource(R.drawable.black_rounded_allsides_border);
        int marginInDp = 12; // Your desired margin in dp
        int marginInPixels = (int) (marginInDp * scale + 0.5f);
        layoutParams.setMargins(0, 0, marginInPixels, 0);

        imageView.setLayoutParams(layoutParams);

        linearLayout.addView(imageView, 0);
        return imageView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == android.app.Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView imageView = addImageToContainer();
            imageView.setImageBitmap(photo);
            tempImages.add(new Image(ImageUtil.convertBitmapToByteBuffer(photo), -1, photo.getWidth(), photo.getHeight()));
        }
    }


    public void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
//        try {
//            File file = new File(getActivity().getCacheDir(), "photo" + tempImages.size());
//            Toast.makeText(requireContext(), getActivity().getApplicationContext().getPackageName() + ".provider", Toast.LENGTH_SHORT).show();
//            recentUri = FileProvider.getUriForFile(requireContext(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
//            takePictureLauncher.launch(recentUri);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Toast.makeText(getActivity(), "oj", Toast.LENGTH_LONG).show();
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null)
            getActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        Button btnDatePicker = root.findViewById(R.id.btnDatePicker);
        Button btnTimePicker = root.findViewById(R.id.btnTimePicker);
        mapsLayout = root.findViewById(R.id.mapsLayout);
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });


        loadingLayout = root.findViewById(R.id.loadingLayout);
        mainLayout = root.findViewById(R.id.svAddActivity);
        mainLayout2 = root.findViewById(R.id.llAddActivity);

        tietDescription = root.findViewById(R.id.tietDescription);
        tietTitle = root.findViewById(R.id.tietTitle);
        tietDate = root.findViewById(R.id.tietDate);
        tietTime = root.findViewById(R.id.tietTime);
        tietLocation = root.findViewById(R.id.tietLocation);
        Button btnAddPhotos = root.findViewById(R.id.btnAddPhotos);
        Button btnDownloadPhoto = root.findViewById(R.id.btnDownloadPhoto);
        Button btnTakePhoto = root.findViewById(R.id.btnTakePhoto);


        spinner = root.findViewById(R.id.spinnerActivityType);

        geocodeService = new PlacesUtil.GeocodeService(requireContext());

        photosLayout = root.findViewById(R.id.photosLayout);


        int mode = getMode();

        ActionBar customActionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (customActionBar != null) {
            if (mode == 2) {

                customActionBar.setTitle(getString(R.string.title_activity_details));


            } else if (mode == 1) {
                customActionBar.setTitle(getString(R.string.title_new_activity));
            }
        }


        requestStoragePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                requestPhotosLauncher.launch("image/*");
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_basic_message) , Toast.LENGTH_LONG).show();
            }
        });

        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {

                openCamera();
            } else {
                Toast.makeText(getActivity(), getString(R.string.error_basic_message), Toast.LENGTH_LONG).show();
            }
        });

        requestPhotosLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), isGranted -> {

            if (isGranted.size() > 0) {
                for (int i = 0; i < isGranted.size(); i++) {
                    if (tempImages.size() >= 10) {
                        Toast.makeText(getActivity(), getString(R.string.error_number_of_images_message), Toast.LENGTH_LONG).show();
                        break;
                    }
                    Bitmap bitmapImage = ImageUtil.loadBitmapFromUri(requireContext(), isGranted.get(i));

                    if (bitmapImage != null) {
                        ImageView iv = addImageToContainer();
                        iv.setImageBitmap(bitmapImage);
                        tempImages.add(new Image(ImageUtil.convertBitmapToByteBuffer(bitmapImage), -1, bitmapImage.getWidth(), bitmapImage.getHeight()));
                    }
                }
            }
        });


        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result.booleanValue() && recentUri != null) {
                        // Image capture was successful

                        Bitmap photo = ImageUtil.loadBitmapFromUri(requireContext(), recentUri);
                        ImageView imageView = addImageToContainer();
                        imageView.setImageBitmap(photo);
                        tempImages.add(new Image(ImageUtil.convertBitmapToByteBuffer(photo), -1, photo.getWidth(), photo.getHeight()));
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.error_basic_message), Toast.LENGTH_SHORT);
                    }
                });

        btnAddPhotos.setOnClickListener(v ->
        {

            //needs bug fix
            if (tempImages.size() >= 10) {
                Toast.makeText(getActivity(), getString(R.string.error_number_of_images_message), Toast.LENGTH_LONG).show();

            } else {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {
                    requestPhotosLauncher.launch("image/*");
                }
            }


        });

        btnTakePhoto.setOnClickListener(v ->
        {

            if (tempImages.size() >= 10) {
                Toast.makeText(getActivity(), getString(R.string.error_number_of_images_message), Toast.LENGTH_LONG).show();

            } else {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Request camera permission if not granted
                    requestCameraPermission();

                } else {
                    // Permission already granted, open the camera
                    openCamera();
                }
            }


        });

        btnDownloadPhoto.setOnClickListener(v ->
        {

            if (tempImages.size() >= 10) {
                Toast.makeText(getActivity(), getString(R.string.error_number_of_images_message), Toast.LENGTH_LONG).show();

            } else {
                View dialogView = inflater.inflate(R.layout.download_image_layout, null);


                EditText imageURLeditText = dialogView.findViewById(R.id.etImageURL);


                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(dialogView)
                        .setTitle(getString(R.string.enter_image_message))
                        .setPositiveButton(getString(R.string.download_button), (dialog, which) -> {
                            // Handle the text input
                            String userInput = imageURLeditText.getText().toString();
                            // Do something with userInput
                            if (userInput.length() == 0) {

                            } else {
                                Target t = new ImageLoadingTarget(
                                        requireContext(),
                                        (imageId, bitmap) -> {
                                            hideLoadingIndicator(imageId);
                                            // Add the loaded bitmap to an ImageView within the FrameLayout
                                            ImageView imageView = addImageToContainer();
                                            imageView.setImageBitmap(bitmap);
                                            tempImages.add(new Image(ImageUtil.convertBitmapToByteBuffer(bitmap), -1, bitmap.getWidth(), bitmap.getHeight()));
                                        },
                                        (imageId) -> {
                                            hideLoadingIndicator(imageId);
                                            Toast.makeText(getActivity(), getString(R.string.error_downloading_image_message), Toast.LENGTH_LONG).show();
                                        },
                                        (imageId) -> {

                                            LinearLayout imageListLayout = root.findViewById(R.id.photosListLayout);
                                            Handler handler2 = new Handler(Looper.getMainLooper());

                                            //na ui threadu-u
                                            handler2.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    hideLoadingIndicator(imageId);
                                                }
                                            }, 5000);

                                            View loadingLayout = LayoutInflater.from(requireContext()).inflate(R.layout.loading_layout, null);
                                            loadingLayout.setBackgroundResource(R.drawable.black_rounded_border);
                                            loadingLayout.setId(imageId);
                                            imageListLayout.addView(loadingLayout);
                                        });

                                Picasso.get().load(userInput).resize(500, 500).centerInside().// Set a placeholder image while loading
                                        into(t

                                );
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel_button), (dialog, which) -> {
                            // Handle cancel action
                        });


                builder.show();


            }


        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {


                if (position != 2) {
                    photosLayout.setVisibility(View.GONE);
                }

                if (position != 3) {
                    mapsLayout.setVisibility(View.GONE);
                }
                if (position == 3) {

                    if (mapsLayout.getVisibility() == View.GONE) {
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                .findFragmentById(R.id.map);
                        mainLayout.setDisableScrollChild(mapFragment.getView());

                        mapFragment.getMapAsync(new WeakReference<DetailsFragment>(DetailsFragment.this).get());

                    }


                    mapsLayout.setVisibility(View.VISIBLE);

                }
                if (position == 2) {
                    photosLayout.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });

        tietLocation.setOnItemClickListener((parent, view, position, id) ->
        {
            String selectedDisplayName = adapterLocations.getItem(position);

            Location selectedLocation = null;
            for (Location location : locations) {
                if (location.getDisplayName().equals(selectedDisplayName)) {
                    selectedLocation = location;
                    break;
                }
            }

            if (selectedLocation != null) {


                sharedViewModel.getLocation().setLon(new BigDecimal(selectedLocation.getLon()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                sharedViewModel.getLocation().setLat(new BigDecimal(selectedLocation.getLat()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                sharedViewModel.getLocation().setDisplayName(selectedLocation.getDisplayName());

                if (spinner.getSelectedItemPosition() == 3 || (sharedViewModel.getEditingItem() != null && sharedViewModel.getEditingItem().getType() == ActivityType.TRAVEL_ACTIVITY)) {
                    LatLng place = new LatLng(selectedLocation.getLat(), selectedLocation.getLon());
                    //  Toast.makeText(getActivity(), selectedLocation.getDisplayName(), Toast.LENGTH_LONG).show();
                    if (mMap != null) {
                        MarkerOptions m = new MarkerOptions().position(place).title(selectedLocation.getDisplayName());
                        mMap.clear();
                        mMap.addMarker(m);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                    }
                }
            }

        });

        tietLocation.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                tietLocation.setSelection(0);
                if (sharedViewModel.getLocation() != null && sharedViewModel.getLocation().getLon() == 0.0) {
                    if (mMap != null) {
                        mMap.clear();
                    }
                }
            } else {
                if (tietLocation.getText().length() > 0)
                    tietLocation.selectAll();

            }
        });


        return root;
    }

    private boolean locationFound = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        HandlerThread handlerThread = new HandlerThread("FetchingDataThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());

        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
        sharedViewModel.getNotificationItems().observe(getViewLifecycleOwner(), items -> {


            if (items.size() > 0) {
                BadgeDrawable badge = navView.getOrCreateBadge(R.id.navigation_notifications);
                badge.setNumber(items.size());
                badge.setVisible(true);
            } else {
                navView.removeBadge(R.id.navigation_notifications);

            }

        });

        adapterLocations = new ArrayAdapter<String>(requireContext(),
                R.layout.location_item_layout, sharedViewModel.getSuggestions().getValue());

        sharedViewModel.getSuggestions().observe(getViewLifecycleOwner(), items -> {
                    adapterLocations.clear();
                    adapterLocations.addAll(items);
                    adapterLocations.notifyDataSetChanged();

//                    tietLocation.postDelayed(() -> {
//                        tietLocation.showDropDown();
//                    }, 200);

                }
        );
        tietLocation.setAdapter(adapterLocations);


        tietLocation.addTextChangedListener(new

                                                    TextWatcher() {


                                                        @Override
                                                        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                                                            // This method is called to notify you that the characters within `charSequence` are about to be replaced
                                                            // You can use it if you need to perform some actions before the text changes
                                                        }

                                                        @Override
                                                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                                                            // This method is called to notify you that the characters within `charSequence` have changed
                                                            // You can use it to dynamically update the list of suggestions

                                                            Location loc = new Location();
                                                            loc.setDisplayName(charSequence.toString());
                                                            sharedViewModel.setLocation(loc);
                                                            locationFound = false;

                                                            if (charSequence.length() > 2) {


                                                                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, geocodeService.createURL(charSequence.toString()), null,
                                                                        new Response.Listener<JSONArray>() {


                                                                            public void scheduleNewTask() {
                                                                                synchronized (geocodeService.scheduledTaskLock) {
                                                                                    if (geocodeService.scheduledTask != null) {

                                                                                        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");

                                                                                        handlerThread.start();
                                                                                        Handler handler = new Handler(handlerThread.getLooper());
                                                                                        handler.postDelayed(new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                geocodeService.queue.add(geocodeService.scheduledTask);

                                                                                                geocodeService.scheduledTask = null;

                                                                                            }
                                                                                        }, geocodeService.intervalBetweenRequestsInSeconds);


                                                                                    } else {


                                                                                        sharedViewModel.addSuggestions(locations.stream().map(loc -> loc.getDisplayName()).collect(Collectors.toList()));


                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onResponse(JSONArray response) {

                                                                                geocodeService.lastRequestTime = LocalDateTime.now();


                                                                                try {
                                                                                    // Iterate through the JSON array
                                                                                    for (int i = 0; i < response.length(); i++) {
                                                                                        JSONObject jsonObject = response.getJSONObject(i);

                                                                                        // Parse JSON object and create MyObject instance
                                                                                        double lat = jsonObject.getDouble("lat");
                                                                                        double lon = jsonObject.getDouble("lon");
                                                                                        String name = jsonObject.getString("display_name");

                                                                                        Location location = new Location(name, lat, lon);

                                                                                        // Add the object to the list
                                                                                        locations.add(location);

                                                                                    }

                                                                                    // Now myObjectList contains your list of custom objects
                                                                                } catch (
                                                                                        JSONException e) {

                                                                                }
                                                                                //sharedViewModel.addSuggestions(locations.stream().map(loc -> loc.getDisplayName()).collect(Collectors.toList()));

                                                                                scheduleNewTask();


                                                                            }

                                                                        },
                                                                        new Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {

                                                                                // Toast.makeText(getActivity(), "errorr volley", Toast.LENGTH_LONG).show();

                                                                                error.printStackTrace();

                                                                                //scheduleNewTask();

                                                                            }
                                                                        });

                                                                if (LocalDateTime.now().compareTo(geocodeService.lastRequestTime.plusSeconds(geocodeService.intervalBetweenRequestsInSeconds)) > 0) {


                                                                    HandlerThread handlerThread = new HandlerThread("MyHandlerThread");

                                                                    handlerThread.start();
                                                                    Handler handler = new Handler(handlerThread.getLooper());
                                                                    handler.postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            geocodeService.queue.add(jsonArrayRequest);


                                                                        }
                                                                    }, geocodeService.intervalBetweenRequestsInSeconds);


                                                                } else {
                                                                    synchronized (geocodeService.scheduledTaskLock) {
                                                                        geocodeService.scheduledTask = jsonArrayRequest;
                                                                    }
                                                                }


                                                            }


                                                        }

                                                        @Override
                                                        public void afterTextChanged(Editable editable) {
                                                            // This method is called to notify you that the characters within `editable` have been changed
                                                            // You can use it if you need to perform some actions after the text changes
                                                        }
                                                    });


        List<String> itemList = Arrays.asList(getResources().getStringArray(R.array.category_options));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner.setAdapter(adapter);

        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        Activity activity = sharedViewModel.getEditingItem();
                        if (activity != null) {
                            previousLocation = database.getLocationDao().getLocationForActivity(activity.getActivity_id());

                        }


//                        sharedViewModel.load();


                        ExecutorService executor = Executors.newSingleThreadExecutor();

                        Handler handler = new Handler(Looper.getMainLooper());





                        if (activity != null) {






                            ActivityType type = activity.getType();

                            executor.execute(() -> {
                                handler.post(() -> {

                                    Log.d("notigeng", previousLocation.getLon() + "");
                                    if (previousLocation != null && previousLocation.getLon() != 0.0) {
                                        Log.d("notigeng", previousLocation.getDisplayName() + previousLocation.getLon());

                                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                                .findFragmentById(R.id.map);
                                        mainLayout.setDisableScrollChild(mapFragment.getView());

                                        mapFragment.getMapAsync(new WeakReference<DetailsFragment>(DetailsFragment.this).get());
                                        sharedViewModel.setLocation(previousLocation);

                                    }
                                    int position = type.ordinal() + 1;

                                    spinner.setSelection(position);

                                });


                            });


                            if (activity.getType().equals(ActivityType.FREE_ACTIVITY)) {

                                try{
                                    List<Image> activityImages = database.getImageDao().getImagesForActivity(activity.getActivity_id());
                                    for (Image image : activityImages) {


                                        Bitmap bitmap = ImageUtil.createBitmapFromByteBuffer(image.getData(), (int) image.getWidth(), (int) image.getHeight());
                                        if (bitmap != null) {

                                            executor.execute(() -> {
                                                handler.post(() -> {

                                                    ImageView imageView = addImageToContainer();
                                                    imageView.setImageBitmap(bitmap);
                                                    tempImages.add(image);
                                                    oldNumberOfImages++;
                                                });

                                            });


                                        }


                                    }
                                }
                                catch(Exception e){

                                }


                                executor.execute(() -> {
                                    handler.post(() -> {
                                        photosLayout.setVisibility(View.VISIBLE);

                                    });

                                });


                            } else if (activity.getType().equals(ActivityType.TRAVEL_ACTIVITY)) {
                                //prikazati mapu sa gradom
                            }

                            executor.execute(() -> {
                                handler.post(() -> {
                                    try {
                                        tietDescription.setText(activity.getDescription());
                                        tietLocation.setText(activity.getLocation());
                                        //koordinate locirat na mapi
                                        tietTitle.setText(activity.getName());
                                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


                                        LocalDate date = null;
                                        LocalTime time = null;

                                        String dateTime = activity.getTime().toString();
                                        String dateString = dateTime.split("T")[0];
                                        String timeString = dateTime.split("T")[1];

                                        date = LocalDate.parse(dateString, dateFormatter);

                                        time = LocalTime.parse(timeString, timeFormatter);

                                        tietTime.setText(time.format(timeFormatter));
                                        tietDate.setText(date.format(dateFormatter2));

                                    } catch (DateTimeParseException e) {


                                    }

                                });

                            });

                        } else {





                        }

                    }
                });


            }
        });


    }

    private void hideLoadingIndicator(int imageId) {
        LinearLayout imageListLayout = root.findViewById(R.id.photosListLayout);
        View retrievedLoadingLayout = imageListLayout.findViewById(imageId);
        if (retrievedLoadingLayout != null) {
            imageListLayout.removeView(retrievedLoadingLayout);
        }
    }


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            String permission = Manifest.permission.READ_EXTERNAL_STORAGE; // Replace with the desired permission

            requestStoragePermissionLauncher.launch(permission);
        } else {
            String permission = Manifest.permission.READ_EXTERNAL_STORAGE; // Replace with the desired permission

            requestStoragePermissionLauncher.launch(permission);
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            String permission = Manifest.permission.CAMERA; // Replace with the desired permission

            requestCameraPermissionLauncher.launch(permission);
        } else {
            String permission = Manifest.permission.CAMERA; // Replace with the desired permission
            requestCameraPermissionLauncher.launch(permission);
        }
    }


    private Activity createObjectFromInput() {
        String title = tietTitle.getText().toString();
        String dateString = tietDate.getText().toString();
        String description = tietDescription.getText().toString();
        String timeString = tietTime.getText().toString();
        String location = tietLocation.getText().toString();

        if (title.length() == 0 || timeString.length() == 0 || description.length() == 0 || location.length() == 0 || dateString.length() == 0) {
            Toast.makeText(getActivity(), "Some fields are empty", Toast.LENGTH_LONG).show();
            return null;
        }

        Spinner spinner = root.findViewById(R.id.spinnerActivityType);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:m");
        LocalDate date = null;
        LocalTime time = null;
        try {
            date = LocalDate.parse(dateString, dateFormatter);
            time = LocalTime.parse(timeString, timeFormatter);
        } catch (DateTimeParseException e) {
            Toast.makeText(getActivity(), getString(R.string.datetime_format_error_message) , Toast.LENGTH_LONG).show();
            return null;
        }

        ActivityType type = null;

        String[] itemList = getResources().getStringArray(R.array.category_options);
        String selectedItem = spinner.getSelectedItem().toString();
        for (int i = 0; i < itemList.length; i++) {
            if (selectedItem.equals(itemList[i])) {

                if (i == 0) {
                    type = null;
                } else if (i == 1) {
                    type = ActivityType.JOB_ACTIVITY;
                } else if (i == 2) {
                    type = ActivityType.FREE_ACTIVITY;
                } else if (i == 3) {
                    type = ActivityType.TRAVEL_ACTIVITY;
                } else if (i == 4) {
                    type = ActivityType.OTHER_ACTIVITY;
                }
                break;
            }
        }


        if (type == null) {
            Toast.makeText(getActivity(),  getString(R.string.category_selection_error_message), Toast.LENGTH_LONG).show();
            return null;
        }

        LocalDateTime localDateTime = date.atTime(time);
        Activity newActivity = new Activity(title, localDateTime, description, location, type, false);

        return newActivity;

    }

    private void showDatePicker() {
        LocalDate date = LocalDate.now();
        TextInputEditText tietDate = root.findViewById(R.id.tietDate);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.US);
        if (tietDate.getText().length() > 0) {
            try {
                date = LocalDate.parse(tietDate.getText(), formatter);
            } catch (DateTimeParseException e) {

            }

        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                TextInputEditText tv = root.findViewById(R.id.tietDate);
                TextInputEditText tietTime = root.findViewById(R.id.tietTime);

                LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);

                String formattedDate = date.format(formatter);
                tv.setText(formattedDate);
            }
        }, date.getYear(), date.getMonth().getValue() - 1, date.getDayOfMonth());
        datePickerDialog.show();
    }


    private void showTimePicker() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:m");
        TextInputEditText tietTime = root.findViewById(R.id.tietTime);

        if (tietTime.getText().length() > 0) {
            try {
                time = LocalTime.parse(tietTime.getText(), timeFormatter);


            } catch (DateTimeParseException e) {

            }
        }

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                TextInputEditText tv = root.findViewById(R.id.tietTime);
                tv.setText(hourOfDay + ":" + minute);

            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                timeSetListener,
                time.getHour(),
                time.getMinute(),
                true
        );
        timePickerDialog.show();
    }


    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.details_of_activity, menu);
        int mode = getMode();
        if (mode == 1) {
            MenuItem item = menu.findItem(R.id.delete_activity_option);
            if (item != null) {
                item.setVisible(false);
            }
        } else if (mode == 2) {

        }
    }

    private int getMode() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            int action = bundle.getInt("action");
            return action;
        } else return -1;
    }


    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.clear_fields) {

            clearAllFields();
            return true;
        } else if (menuItem.getItemId() == R.id.delete_activity_option) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.confirm_deletion)
                    .setPositiveButton(R.string.positive_confirmation, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            database.getActivityDao().deleteActivity(sharedViewModel.getEditingItem());

                            if (sharedViewModel.getEditingItem().getType().equals(ActivityType.FREE_ACTIVITY))
                                database.getImageDao().deleteImagesForActivityId(sharedViewModel.getEditingItem().getActivity_id());
                            Toast.makeText(requireContext(), getString(R.string.succesfull_deletion_message), Toast.LENGTH_LONG).show();
                            sharedViewModel.getItems().getValue().remove(sharedViewModel.getEditingItem());
                            if (sharedViewModel.getEditingItem() != null)
                                sharedViewModel.setEditingItem(null);
                            getActivity().onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancels the dialog.
                        }
                    });
            // Create the AlertDialog object and return it.
            builder.create().show();
            return false;
        } else if (menuItem.getItemId() == R.id.action_add_activity) {
            menuItem.setEnabled(false);


            Activity activity = sharedViewModel.getEditingItem();
            Activity newActivity = createObjectFromInput();


            if (newActivity == null) {
                menuItem.setEnabled(true);
                return true;
            }
            Location location = null;
            if (sharedViewModel.getLocation().getLon() != 0.0) {

                location = sharedViewModel.getLocation();
            } else {
                location = new Location();
                location.setDisplayName(newActivity.getLocation());
            }
            loadingLayout.setVisibility(View.VISIBLE);
            if (activity == null) {

                try {
                    newActivity.setActivity_id(database.getActivityDao().insertActivity(newActivity));

                    location.setId_activity((int) newActivity.getActivity_id());
                    database.getLocationDao().insertLocation(location);

                    sharedViewModel.getItems().getValue().add(newActivity);
                    sharedViewModel.getNotificationItems().getValue().add(newActivity);
                    Toast.makeText(requireContext(), getString(R.string.succesfull_adding_message), Toast.LENGTH_LONG).show();
                    if (newActivity.getType().equals(ActivityType.FREE_ACTIVITY) && tempImages.size() > 0) {
                        for (Image image : tempImages) {
                            try {

                                image.setId_activity(newActivity.getActivity_id());
                                image.setId_image(database.getImageDao().insertImage(image));
                            } catch (SQLiteException e) {
                                Toast.makeText(getActivity(), getString(R.string.error_adding_message), Toast.LENGTH_LONG).show();
                            }

                        }


                    }

                    getActivity().onBackPressed();
                } catch (SQLiteException e) {
                    Toast.makeText(getActivity(), getString(R.string.error_basic_message), Toast.LENGTH_LONG).show();
                    menuItem.setEnabled(true);
                    loadingLayout.setVisibility(View.INVISIBLE);
                    return true;
                }


            } else {

                try {

                    newActivity.setActivity_id(activity.getActivity_id());
                    if (newActivity.equals(activity) && tempImages.size() == oldNumberOfImages) {
                        Toast.makeText(requireContext(), getString(R.string.error_edit_message), Toast.LENGTH_LONG).show();
                        menuItem.setEnabled(true);
                        loadingLayout.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    database.getActivityDao().updateActivity(newActivity);
                    if (location.getId_location() == 0) {
                        location.setId_location(previousLocation.getId_location());
                    }
                    location.setId_activity((int) newActivity.getActivity_id());

                    database.getLocationDao().updateLocation(location);


                    if (activity.getType() == ActivityType.FREE_ACTIVITY && newActivity.getType() != ActivityType.FREE_ACTIVITY) {
                        database.getImageDao().deleteImagesForActivityId(activity.getActivity_id());

                    }
                    if (oldNumberOfImages < tempImages.size()) {
                        for (int i = oldNumberOfImages; i < tempImages.size(); i++) {
                            Image image = tempImages.get(i);
                            try {

                                image.setId_activity(newActivity.getActivity_id());
                                image.setId_image(database.getImageDao().insertImage(image));
                            } catch (SQLiteException e) {
                                Toast.makeText(getActivity(), getString(R.string.error_adding_image), Toast.LENGTH_LONG).show();
                            }
                        }
                    }


                    sharedViewModel.getItems().getValue().remove(activity);
                    sharedViewModel.getItems().getValue().add(newActivity);
                    sharedViewModel.getNotificationItems().getValue().remove(activity);
                    sharedViewModel.getNotificationItems().getValue().add(newActivity);
                    Toast.makeText(requireContext(), getString(R.string.succesfull_saving_message), Toast.LENGTH_LONG).show();

                    getActivity().onBackPressed();
                } catch (SQLiteException e) {
                    Toast.makeText(getActivity(), getString(R.string.error_basic_message), Toast.LENGTH_LONG).show();
                    menuItem.setEnabled(true);
                    loadingLayout.setVisibility(View.INVISIBLE);
                    return true;
                }
            }
            loadingLayout.setVisibility(View.INVISIBLE);
            menuItem.setEnabled(true);
            return true;
        } else if (menuItem.getItemId() == android.R.id.home) {
            sharedViewModel.clearEditingItem();
            sharedViewModel.setLocation(null);
            getActivity().onBackPressed();

            return true;
        } else {

            return false;
        }

    }

    private void clearAllFields() {
        tietTitle.setText("");
        tietDate.setText("");
        tietTime.setText("");
        tietDescription.setText("");
        tietLocation.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        int mode = getMode();
        if (mode == 2 && sharedViewModel.getEditingItem() == null) {
            clearAllFields();
            spinner.setSelection(0);
            tempImages.clear();

            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_navigation_details_to_navigation_home);


        }
        loadingLayout.setVisibility(View.INVISIBLE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sharedViewModel.getEditingItem() != null) sharedViewModel.setEditingItem(null);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        binding = null;
    }

    public GoogleMap mMap;
    public RelativeLayout mapsLayout;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {


        mMap = googleMap;

        mapsLayout.setVisibility(View.VISIBLE);

        LatLng place = null;

        if (sharedViewModel.getLocation() != null && sharedViewModel.getLocation().getLon() != 0.0) {

            place = new LatLng(sharedViewModel.getLocation().getLat(), sharedViewModel.getLocation().getLon());


        } else if (tietLocation.getText().length() > 2) {

        }
        if (mMap != null && place != null) {
            mMap.addMarker(new MarkerOptions().position(place).title(sharedViewModel.getLocation().getDisplayName()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        }


    }


}