package com.lendingapp.neighborly;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.preference.PreferenceManager;

import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    Button filterPageButton;
    GoogleApiClient mGoogleApiClient;
    final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    Location mLastLocation;
    private boolean permissionGiven = true;
    Marker meMarker;
    TextView filterText;
    private HashMap<LatLng, UserInfoWrapper> listingPointers;
    private AlertDialog.Builder builder;
    private boolean test;
    private SupportMapFragment smf;
    String userClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        listingPointers = new HashMap<>();
        filterPageButton = (Button) findViewById(R.id.filter);
        filterPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, FilterActivity.class);
                MapActivity.this.startActivity(intent);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        smf = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        smf.getMapAsync(this);
        if (checkGooglePlayServices()) {
            buildGoogleApiClient();
        }
        filterText = (TextView) findViewById(R.id.filter_text);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String filter;
        try {
            filter = getIntent().getExtras().getString("filter");
        } catch (Exception e) {
            filter = null;
        }
        if (filter == null) {
            String address = "https://lending-legend.herokuapp.com/users";
            String token = preferences.getString("token", "");
            Response.Listener<JSONArray> listener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            UserInfoWrapper toAdd = new UserInfoWrapper();
                            toAdd.id = response.getJSONObject(i).getString("_id");
                            toAdd.firstName = response.getJSONObject(i).getString("firstName");
                            toAdd.lastName = response.getJSONObject(i).getString("lastName");
                            toAdd.email = response.getJSONObject(i).getString("email");
                            double lon = response.getJSONObject(i).getJSONObject("location").getDouble("lon");
                            double lat = response.getJSONObject(i).getJSONObject("location").getDouble("lat");
                            LatLng newLatLng = new LatLng(lat, lon);
                            listingPointers.put(newLatLng, toAdd);
                        }
                        populateMap();
                        for (int i = 0; i < response.length(); i++) {
                            String id = listingPointers.get(listingPointers.keySet().toArray()[i]).id;
                            String rAddress = "https://lending-legend.herokuapp.com/listings/by/" + id;
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String token = preferences.getString("token", "");
                            Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response1) {
                                    try {
                                        for (int j = 0; j < response1.length(); j++) {
                                            String name = response1.getJSONObject(j).getString("name");
                                            String user = response1.getJSONObject(j).getString("user");
                                            for (int k = 0; k < listingPointers.keySet().size(); k++) {
                                                String uid = listingPointers.get(listingPointers.keySet().toArray()[k]).id;
                                                if (uid.equals(user)) {
                                                    listingPointers.get(listingPointers.keySet().toArray()[k]).listings.add(name);
                                                }
                                            }

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            JSONArray requestArray = new JSONArray();
                            Response.ErrorListener errorListener1 = new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    filterText.setText("No filter");
                                    String toPrint = "";
                                    for (int i = 0; i < error.networkResponse.data.length; i++) {
                                        toPrint += (char) error.networkResponse.data[i];
                                    }
                                    System.out.println(toPrint);
                                    Toast.makeText(MapActivity.this, toPrint, Toast.LENGTH_LONG).show();
                                }
                            };
                            GetRequest listingsRequest = new GetRequest(rAddress, token, responseListener, requestArray, errorListener1);
                            RequestQueue queue1 = Volley.newRequestQueue(MapActivity.this);
                            queue1.add(listingsRequest);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            JSONArray request_data = new JSONArray();
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    filterText.setText("No filter");
                    String toPrint = "";
                    for (int i = 0; i < error.networkResponse.data.length; i++) {
                        toPrint += (char) error.networkResponse.data[i];
                    }
                    System.out.println(toPrint);
                    Toast.makeText(MapActivity.this, toPrint, Toast.LENGTH_LONG).show();
                }
            };
            GetRequest request = new GetRequest(address, token, listener, request_data, errorListener);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        } else {
            filterText.setText("filter: " + filter);
            String address = "https://lending-legend.herokuapp.com/users/" + filter;
            String token = preferences.getString("token", "");
            Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            String id = response.getJSONObject(i).getString("user");
                            String address = "https://lending-legend.herokuapp.com/user/" + id;
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String token = preferences.getString("token", "");
                            Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        double lat = response.getJSONObject("location").getDouble("lat");
                                        double lon = response.getJSONObject("location").getDouble("lon");
                                        LatLng latlon = new LatLng(lat, lon);
                                        UserInfoWrapper userInfo = new UserInfoWrapper();
                                        userInfo.firstName = response.getString("firstName");
                                        userInfo.lastName = response.getString("lastName");
                                        userInfo.email = response.getString("email");
                                        userInfo.id = response.getString("_id");
                                        listingPointers.put(latlon, userInfo);
                                        populateMap();
                                        String rAddress = "https://lending-legend.herokuapp.com/listings/by/" + userInfo.id;
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        String token = preferences.getString("token", "");
                                        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
                                            @Override
                                            public void onResponse(JSONArray response1) {
                                                try {
                                                    for (int j = 0; j < response1.length(); j++) {
                                                        String name = response1.getJSONObject(j).getString("name");
                                                        String user = response1.getJSONObject(j).getString("user");
                                                        for (int k = 0; k < listingPointers.keySet().size(); k++) {
                                                            String uid = listingPointers.get(listingPointers.keySet().toArray()[k]).id;
                                                            if (uid.equals(user)) {
                                                                listingPointers.get(listingPointers.keySet().toArray()[k]).listings.add(name);
                                                            }
                                                        }

                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        JSONArray requestArray = new JSONArray();
                                        Response.ErrorListener errorListener1 = new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                filterText.setText("No filter");
                                                String toPrint = "";
                                                for (int i = 0; i < error.networkResponse.data.length; i++) {
                                                    toPrint += (char) error.networkResponse.data[i];
                                                }
                                                System.out.println(toPrint);
                                                Toast.makeText(MapActivity.this, toPrint, Toast.LENGTH_LONG).show();
                                            }
                                        };
                                        GetRequest listingsRequest = new GetRequest(rAddress, token, responseListener, requestArray, errorListener1);
                                        RequestQueue queue1 = Volley.newRequestQueue(MapActivity.this);
                                        queue1.add(listingsRequest);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            JSONObject jsonObject = new JSONObject();
                            Response.ErrorListener errorListener = new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    filterText.setText("No filter");
                                    String toPrint = "";
                                    for (int i = 0; i < error.networkResponse.data.length; i++) {
                                        toPrint += (char) error.networkResponse.data[i];
                                    }
                                    System.out.println(toPrint);
                                    Toast.makeText(MapActivity.this, toPrint, Toast.LENGTH_LONG).show();
                                }
                            };
                            GetSingleRequest request = new GetSingleRequest(address, token, responseListener, jsonObject, errorListener);
                            RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
                            queue.add(request);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            JSONArray jsonArray = new JSONArray();
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    filterText.setText("No filter");
                    String toPrint = "";
                    for (int i = 0; i < error.networkResponse.data.length; i++) {
                        toPrint += (char) error.networkResponse.data[i];
                    }
                    System.out.println(toPrint);
                    Toast.makeText(MapActivity.this, toPrint, Toast.LENGTH_LONG).show();
                }
            };
            GetRequest request = new GetRequest(address, token, responseListener, jsonArray, errorListener);
            RequestQueue queue = Volley.newRequestQueue(MapActivity.this);
            queue.add(request);
        }
    }

    protected void populateMap() {
        for (int i = 0; i < listingPointers.keySet().size(); i++) {
            LatLng loc = (LatLng) listingPointers.keySet().toArray()[i];
            Marker newMarker = mMap.addMarker(new MarkerOptions().position(loc));
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        if (listingPointers != null) {
            listingPointers.clear();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (permissionGiven == true) {
            LocationRequest mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000)
                    .setFastestInterval(20000);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            updateUI();
        } else {
            LatLng me = new LatLng(0, 0);
            mMap.addMarker(new MarkerOptions().position(me).title("Current location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean checkGooglePlayServices() {

        int checkGooglePlayServices = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
			/*
			* google play services is missing or update is required
			*  return code could be
			* SUCCESS,
			* SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED,
			* SERVICE_DISABLED, SERVICE_INVALID.
			*/
            int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(15);
        setMapPointerClickers();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    permissionGiven = false;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateUI();
    }

    private void updateUI() {
        if (meMarker != null) {
            meMarker.remove();
        }
        LatLng me = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
        MarkerOptions options = new MarkerOptions().position(me).title("Current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.me));
        meMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(me));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapActivity.this, MainActivity.class);
        MapActivity.this.startActivity(intent);
    }

    private void setMapPointerClickers() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                double lat = marker.getPosition().latitude;
                double lon = marker.getPosition().longitude;
                UserInfoWrapper userInfo = listingPointers.get(new LatLng(lat, lon));
                if (userInfo != null) {
                    userClicked = userInfo.id;
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                    builder.setTitle(userInfo.firstName + " " + userInfo.lastName);
                    builder.setMessage("listings" + userInfo.listings.toString());
                    builder.setPositiveButton("View User", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MapActivity.this, ViewUserActivity.class);
                            intent.putExtra("id", userClicked);
                            MapActivity.this.startActivity(intent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }

                return true;
            }
        });
    }
}
