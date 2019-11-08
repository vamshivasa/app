package com.example.findroommate.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findroommate.Filters;
import com.example.findroommate.MapsActivity;
import com.example.findroommate.MyAdapter;
import com.example.findroommate.Post;
import com.example.findroommate.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener  {
    private HashMap<Long, Double> sort;
    private ArrayList<Integer> mUserFiletrs = new ArrayList<>();
    private ImageButton imageButtonLocation, imageButtonSearch;
    private EditText editTextSearch;
    private SharedPreferences preferences;
    private DatabaseReference databaseReference, databaseReferenceImages, databaseReference2;
    private RecyclerView recyclerView;
    private ArrayList<Post> list, duplicateList;
    private ArrayList<Filters> list1;
    private MyAdapter myAdapter;
    private FusedLocationProviderClient client;
    private ArrayList<Uri> images_url;
    private LinearLayoutManager layoutManager;
    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;
    private List<Double> lati;
    private List<Double> longi;
    private List<Long> postIdDis;
    private long postNum;
    private AlertDialog progressDialog;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;

    private LocationManager locationManager;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        checkLocationPermission();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Post");
        databaseReferenceImages = FirebaseDatabase.getInstance().getReference().child("postImages");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Filters");
        imageButtonLocation = root.findViewById(R.id.location_button);
        imageButtonSearch = root.findViewById(R.id.button_search);
        editTextSearch = root.findViewById(R.id.edit_text_search);
        recyclerView = root.findViewById(R.id.home_recyclerView);
        client = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        list = new ArrayList<Post>();
        duplicateList = new ArrayList<>();
        list1 = new ArrayList<Filters>();
        images_url = new ArrayList<Uri>();
        sort = new HashMap<>();
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        lati = new ArrayList<>();
        longi = new ArrayList<>();
        postIdDis = new ArrayList<>();
        progressDialog = new SpotsDialog(getContext(), R.style.Custom);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Toast.makeText(getContext(), preferences.getString("UserLocation", null), Toast.LENGTH_LONG).show();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        //getting data from database
        getWholeData();
        //getting images from database
        getImages();


        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myAdapter.getFilter().filter(editTextSearch.getText());
            }
        });
        editTextSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    myAdapter.getFilter().filter(editTextSearch.getText());
                    return true;
                }
                return false;
            }
        });

        imageButtonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor = preferences.edit();
                final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Enter Location");
                alertDialog.setButton("Current Location", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //getLocation();
                      startLocationUpdates();
                    }
                });
                alertDialog.setButton2("Enter Manually", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getContext(), MapsActivity.class);
                        startActivityForResult(intent, 11);
                    }
                });
                // Set the Icon for the Dialog
                alertDialog.setIcon(R.drawable.ic_map_black_24dp);
                alertDialog.show();
            }
        });
        Button buttonFilter = root.findViewById(R.id.button_filters);
        Spinner spinner = root.findViewById(R.id.spinner_sort);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort, android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                if (selectedItem.equals("High Price")) {
                    progressDialog.show();

                    Collections.sort(list, new Comparator<Post>() {
                        @Override
                        public int compare(Post p1, Post p2) {
                            return p1.getPrice() - p2.getPrice();
                        }


                    });
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(layoutManager);
                    myAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                } else if (selectedItem.equals("Low Price")) {
                    progressDialog.show();
                    Collections.sort(list, new Comparator<Post>() {
                        @Override
                        public int compare(Post p1, Post p2) {
                            return p2.getPrice() - p1.getPrice();
                        }

                    });
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(layoutManager);
                    myAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                } else if (selectedItem.equals("Nearest")) {
                    SortLocations();
                    HashMap<Long, Double> hashMapnew = new HashMap<>();
                    hashMapnew = sortHashMapByValuesD(sort);
                    list.clear();
                    progressDialog.show();
                    for (long key : hashMapnew.keySet()) {


                        databaseReference.orderByChild("postId").equalTo(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Post post = snapshot.getValue(Post.class);
                                    list.add(post);
                                }
                                myAdapter = new MyAdapter(getContext(), list);
                                layoutManager.setReverseLayout(false);
                                layoutManager.setStackFromEnd(false);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(myAdapter);
                                myAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                } else if (selectedItem.equals("Newest")) {
                    list.clear();
                    getWholeData();
                    progressDialog.dismiss();

                }


            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
        final String[] mListItems = getResources().getStringArray(R.array.filters);
        final boolean[] mCheckedItems = new boolean[mListItems.length];
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Click", Toast.LENGTH_LONG).show();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose Filters");
                builder.setMultiChoiceItems(mListItems, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if (!isChecked) {
                            mUserFiletrs.add(position);
                        }
                        if (isChecked) {
                            if (!mUserFiletrs.contains(position)) {
                                mUserFiletrs.add(position);


                            }
                        }
                    }
                });
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, final int position) {
                        final HashMap<Integer, String> nameMap = new HashMap();
                        nameMap.put(0, "furnished");
                        nameMap.put(1, "noSmoking");
                        nameMap.put(2, "noDrinking");
                        nameMap.put(3, "noPets");
                        nameMap.put(4, "male");
                        nameMap.put(5, "female");
                        if (mUserFiletrs.size() == 1) {
                            list1.clear();
                            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Filters");
                            db1.orderByChild(Objects.requireNonNull(nameMap.get(mUserFiletrs.get(0)))).equalTo(true).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Filters filters = snapshot.getValue(Filters.class);
                                        list1.add(filters);
                                    }
                                    getDataByFilters();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else if (mUserFiletrs.size() == 2) {
                            list1.clear();

                            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Filters");
                            db1.orderByChild(Objects.requireNonNull(nameMap.get(mUserFiletrs.get(0)))).equalTo(true).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Filters filters = snapshot.getValue(Filters.class);
                                        if (snapshot.child(nameMap.get(mUserFiletrs.get(1))).getValue().equals(true)) {
                                            list1.add(filters);
                                        }


                                    }
                                    getDataByFilters();
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else if (mUserFiletrs.size() == 3) {
                            list1.clear();
                            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Filters");
                            db1.orderByChild(Objects.requireNonNull(nameMap.get(mUserFiletrs.get(0)))).equalTo(true)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Filters filters = snapshot.getValue(Filters.class);
                                                if (snapshot.child(nameMap.get(mUserFiletrs.get(1))).getValue().equals(true) &&
                                                        snapshot.child(nameMap.get(mUserFiletrs.get(2))).getValue().equals(true)) {
                                                    list1.add(filters);
                                                }
                                            }
                                            getDataByFilters();
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                        } else if (mUserFiletrs.size() == 4) {
                            list1.clear();
                            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Filters");
                            db1.orderByChild(Objects.requireNonNull(nameMap.get(mUserFiletrs.get(0)))).equalTo(true)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                Filters filters = snapshot.getValue(Filters.class);
                                                if (snapshot.child(nameMap.get(mUserFiletrs.get(1))).getValue().equals(true) &&
                                                        snapshot.child(nameMap.get(mUserFiletrs.get(2))).getValue().equals(true) &&
                                                        snapshot.child(nameMap.get(mUserFiletrs.get(3))).getValue().equals(true)) {
                                                    list1.add(filters);
                                                }

                                            }
                                            getDataByFilters();
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                });
                builder.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        for (int i = 0; i < mCheckedItems.length; i++) {
                            mCheckedItems[i] = false;
                            mUserFiletrs.clear();
                            list.clear();
                            getWholeData();
                        }
                    }
                });
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });
        return root;
    }

    private void getWholeData() {
        list.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1.child("shown").exists()) {
                        if (Objects.equals(dataSnapshot1.child("shown").getValue(), true)) {
                            Post p = dataSnapshot1.getValue(Post.class);
                            list.add(p);

                        }

                    }


                }

                myAdapter = new MyAdapter(getContext(), list);
                recyclerView.setLayoutManager(layoutManager);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                recyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
                //SortLocations();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getImages() {

    }

    private void getDataByFilters() {
        list.clear();
        if (list1.size() > 0) {
            for (int i = 0; i < list1.size(); i++) {
                databaseReference.orderByChild("filterId").equalTo(list1.get(i).getFilterId())
                        .addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    if (dataSnapshot1.child("shown").exists()) {
                                        if (Objects.equals(dataSnapshot1.child("shown").getValue(), true)) {
                                            Post p = dataSnapshot1.getValue(Post.class);
                                            list.add(p);
                                        }
                                    }
                                }

                                myAdapter = new MyAdapter(getContext(), list);
                                recyclerView.setAdapter(myAdapter);
                                recyclerView.stopScroll();
                                recyclerView.getRecycledViewPool().clear();
                                myAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }
        } else {
            list1.size();

            myAdapter = new MyAdapter(getContext(), list);
            recyclerView.setAdapter(myAdapter);
            myAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 11) {
            if (resultCode == RESULT_OK) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("UserLocation", data.getStringExtra("key"));
                String userLocation = data.getStringExtra("key");
                getLongAndLat(userLocation);
                editor.apply();

            }
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mListState = layoutManager.onSaveInstanceState();
        outState.putParcelable("stateKey", mListState);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
            mListState = savedInstanceState.getParcelable("stateKey");
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void SortLocations() {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String loc = list.get(i).getLocation();
                long postId = list.get(i).getPostId();
                getLongAndLat(loc, postId);
            }
            if (preferences.getString("currentLat", null) != null && preferences.getString("currentLong", null) != null) {
                double latCurrent = Double.parseDouble(Objects.requireNonNull(preferences.getString("currentLat", null)));
                double longCurrent = Double.parseDouble(Objects.requireNonNull(preferences.getString("currentLong", null)));
                for (int i = 0; i < lati.size(); i++) {

                    double dis = distance(lati.get(i), longi.get(i), latCurrent, longCurrent, postNum);
                    sort.put(postIdDis.get(i), dis);
                    //Toast.makeText(getContext(), String.valueOf(dis), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Please Enter your address First", Toast.LENGTH_LONG).show();
                getWholeData();


            }
        }


    }

    private void getLongAndLat(String address, long postId) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            if (address != null) {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                Address add = addresses.get(0);
                double latitude = add.getLatitude();
                double longitude = add.getLongitude();
                lati.add(latitude);
                longi.add(longitude);
                postIdDis.add(postId);

                // Toast.makeText(getContext(), String.valueOf(latitude), Toast.LENGTH_LONG).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getLongAndLat(String address) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            if (address != null) {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                Address add = addresses.get(0);
                double latitude = add.getLatitude();
                double longitude = add.getLongitude();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("currentLat", String.valueOf(latitude));
                editor.putString("currentLong", String.valueOf(longitude));
                editor.apply();

                // Toast.makeText(getContext(), String.valueOf(latitude), Toast.LENGTH_LONG).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
   private  void getLocation(){


    }



    private double distance(double lat1, double lon1, double lat2, double lon2, long postNum1) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private LinkedHashMap sortHashMapByValuesD(HashMap distance) {
        List mapKeys = new ArrayList(distance.keySet());
        List mapValues = new ArrayList(distance.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        for (Object val : mapValues) {

            for (Object key : mapKeys) {
                String comp1 = distance.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    distance.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put(key, val);
                    break;
                }


            }

        }
        //Toast.makeText(getContext(),sortedMap.toString(),Toast.LENGTH_LONG).show();
        return sortedMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
          savingLocation(mLocation);
            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(getContext(), "Location not Detected", Toast.LENGTH_SHORT).show();
        }

    }
    private void savingLocation(Location location){
        SharedPreferences.Editor editor=preferences.edit();
        if (mLocation != null) {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            String address = null;
            try {
                addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                address = addresses.get(0).getAddressLine(0);
               // Toast.makeText(getContext(),address,Toast.LENGTH_LONG).show();
                editor.putString("currentLat", String.valueOf(mLocation.getLatitude()));
                editor.putString("currentLong", String.valueOf(mLocation.getLongitude()));

            } catch (IOException e) {
                e.printStackTrace();
            }


            editor.putString("UserLocation", address);
            editor.apply();
        }

    }

    private void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat
                .checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }

    @Override
    public void onConnectionSuspended(int i) {
      //  Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        savingLocation(location);


    }
    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}