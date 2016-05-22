package com.yathams.loginsystem;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yathams.loginsystem.adapters.StoreLocationsAdapter;
import com.yathams.loginsystem.model.StoreLocation;
import com.yathams.loginsystem.model.NearByStoresResponse;
import com.yathams.loginsystem.utils.Utils;
import com.yathams.loginsystem.utils.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SelectStoreActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final String TAG = "SelectStoreActivity";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private RecyclerView recyclerView;
    private StoreLocationsAdapter adapter;
    private AsyncTask getNearByStoresAsync;
    private NearByStoresResponse nearByStoresResponse;
    private View progressBar, mainContainer;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    @Override
    public void onPreExecute() {
        showProgress(true);
        if(getNearByStoresAsync != null) {
            nearByStoresResponse = null;
        }
    }

    @Override
    public String doInBackground(String[] params) {
        if(getNearByStoresAsync != null) {
            JSONObject jsonObject = new JSONObject();
            String response;
            try {
                jsonObject.put("latitude", params[0]);
                jsonObject.put("longitude", params[1]);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("Request json", jsonObject.toString());
            response = Webservice.callPostService(Webservice.GET_STORE_LOCATIONS, jsonObject.toString());
            try {
                nearByStoresResponse = new Gson().fromJson(response, NearByStoresResponse.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    @Override
    public void onPostExecute() {
        showProgress(false);
        if(getNearByStoresAsync != null) {
            if(nearByStoresResponse != null){
                if(nearByStoresResponse.status == 1){
                    storeItems = nearByStoresResponse.stores;
                    adapter = new StoreLocationsAdapter(mBaseActivity, storeItems);
                    recyclerView.setAdapter(adapter);
                    addMarkers();
                }else{
                    Utils.showToast(mBaseActivity, nearByStoresResponse.message);
                }
            }else{
                Utils.showToast(mBaseActivity, "Something went wrong");
            }



            getNearByStoresAsync = null;
        }
    }

    @Override
    public void onCanceled() {
        if(getNearByStoresAsync != null) {
            getNearByStoresAsync = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mBaseActivity = this;
        recyclerView = (RecyclerView) findViewById(R.id.storeLocationsRecyclerView);
        progressBar = findViewById(R.id.login_progress);
        mainContainer = findViewById(R.id.mainContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            doGetCurrentLocation();
        }


    }

    /**
     * Shows the progress UI and hides the login form.
     */

    private void showProgress(final boolean show) {
        //hide the relevant UI components.
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mainContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    protected void onStart() {

        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, "onConnected: "+mLastLocation.toString());
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            LatLng myloc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myloc).title("Marker in myloc"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));

            getNearByStoresAsync = new AsyncTask(mBaseActivity);
            getNearByStoresAsync.execute(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));

        }
    }

    private void addMarkers() {
        if(storeItems != null)
        for (StoreLocation storeItem : storeItems) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(storeItem.latitude, storeItem.longitude)).title(storeItem.storeName));
        }
    }

    private List<StoreLocation> storeItems;

    @Override
    public void onConnectionSuspended(int i) {
        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    doGetCurrentLocation();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void doGetCurrentLocation() {
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    public void sendButtonClicked(View view) {
        if(!Utils.isNetworkAvailable(mBaseActivity)){
            Utils.showNetworkAlertDialog(mBaseActivity);
        }else{
            boolean isAtLeastOneStoreSelected = false;
            for (StoreLocation storeItem : storeItems) {
                if(storeItem.isSelected){
                    isAtLeastOneStoreSelected = true;
                    break;
                }
            }

            if(!isAtLeastOneStoreSelected){
                Utils.showToast(mBaseActivity, "Please select a store to send");
            }else{

            }
        }
    }
}
