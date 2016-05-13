package com.yathams.loginsystem;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yathams.loginsystem.databinding.ActivityProviderSignupBinding;
import com.yathams.loginsystem.model.LogInResponse;
import com.yathams.loginsystem.model.Response;
import com.yathams.loginsystem.utils.Utils;
import com.yathams.loginsystem.utils.Webservice;

import org.json.JSONException;
import org.json.JSONObject;


public class ProviderSignUpActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private ActivityProviderSignupBinding binding;
    private String androidUUID;
    private String deviceName;
    private String deviceManufacturer;
    private String serial;
    private String version;
    private String platform = "Android";
    private AsyncTask signUpTask;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LogInResponse signUpResponse;

    @Override
    public void onPreExecute() {
        showProgress(true);
        signUpResponse = null;
    }

    @Override
    public String doInBackground(String[] params) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("storeName", params[0]);
            jsonObject.put("storeAddress", params[1]);
            jsonObject.put("city", params[2]);
            jsonObject.put("state", params[3]);
            jsonObject.put("zipCode", params[4]);
            jsonObject.put("storePhone", params[5]);
            jsonObject.put("contactPersonName", params[6]);
            jsonObject.put("email", params[7]);
            jsonObject.put("password", params[8]);
            jsonObject.put("mobileNumber", params[9]);
            jsonObject.put("latitude", params[10]);
            jsonObject.put("longitude", params[11]);
            jsonObject.put("UUID", androidUUID);
            jsonObject.put("deviceName", deviceName);
            jsonObject.put("manufacturer", deviceManufacturer);
            jsonObject.put("platform", platform);
            jsonObject.put("serial", serial);
            jsonObject.put("version", version);
            jsonObject.put("userType", "Provider");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("Request json", jsonObject.toString());
        String response = Webservice.callPostService(Webservice.PROVIDER_SIGN_UP, jsonObject.toString());
        try {
            signUpResponse = new Gson().fromJson(response, LogInResponse.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute() {
        showProgress(false);
        if(signUpResponse != null){
            if(signUpResponse.status == 1){ //Sign up success
                finish();
            }else{ // Sign up failed and error message
                Utils.showToast(mBaseActivity, signUpResponse.message);
            }
        }else{
            Utils.showToast(mBaseActivity, "Something went wrong");
        }
        signUpTask = null;
    }

    @Override
    public void onCanceled() {
        signUpTask = null;
        showProgress(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_provider_signup);
        mBaseActivity = this;
        androidUUID = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        deviceName = Build.MODEL;
        deviceManufacturer = Build.MANUFACTURER;
        serial = Build.SERIAL;
        version = Build.VERSION.RELEASE;
//        signUpTask = new AsyncTask(mBaseActivity);
        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });

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
        } else {
            doGetCurrentLocation();
        }
    }

    private void doGetCurrentLocation() {
        createLocationRequest();
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

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void attemptSignUp() {
        if (!Utils.isNetworkAvailable(mBaseActivity)) {
            Utils.showNetworkAlertDialog(mBaseActivity);
            return;
        }

        if (signUpTask != null) {
            return;
        }

        // Reset errors.
        binding.email.setError(null);
        binding.password.setError(null);
        binding.confirmPassword.setError(null);
        binding.city.setError(null);
        binding.storeName.setError(null);
        binding.storeAddress.setError(null);
        binding.state.setError(null);
        binding.zipCode.setError(null);
        binding.storePhone.setError(null);
        binding.mobileNumber.setError(null);
        binding.contactPersonName.setError(null);
        binding.latitude.setError(null);
        binding.longitude.setError(null);

        // Store values at the time of the login attempt.
        String email = binding.email.getText().toString();
        String mobileNumber = binding.mobileNumber.getText().toString();
        String password = binding.password.getText().toString();
        String confirmPassword = binding.confirmPassword.getText().toString();
        String storeName = binding.storeName.getText().toString();
        String storeAddress = binding.storeAddress.getText().toString();
        String city = binding.city.getText().toString();
        String zipCode = binding.zipCode.getText().toString();
        String storePhone = binding.storePhone.getText().toString();
        String contactPersonName = binding.contactPersonName.getText().toString();
        String state = binding.state.getText().toString();
        String latitude = binding.latitude.getText().toString();
        String longitude = binding.longitude.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            binding.email.setError(getString(R.string.error_field_required));
            focusView = binding.email;
            cancel = true;
        } else if (TextUtils.isEmpty(mobileNumber)) {
            binding.mobileNumber.setError(getString(R.string.error_field_required));
            focusView = binding.mobileNumber;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            binding.password.setError(getString(R.string.error_field_required));
            focusView = binding.password;
            cancel = true;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPassword.setError(getString(R.string.error_field_required));
            focusView = binding.confirmPassword;
            cancel = true;
        } else if (TextUtils.isEmpty(storeName)) {
            binding.storeName.setError(getString(R.string.error_field_required));
            focusView = binding.storeName;
            cancel = true;
        } else if (TextUtils.isEmpty(storeAddress)) {
            binding.storeAddress.setError(getString(R.string.error_field_required));
            focusView = binding.storeAddress;
            cancel = true;
        } else if (TextUtils.isEmpty(city)) {
            binding.city.setError(getString(R.string.error_field_required));
            focusView = binding.city;
            cancel = true;
        } else if (TextUtils.isEmpty(state)) {
            binding.state.setError(getString(R.string.error_field_required));
            focusView = binding.state;
            cancel = true;
        } else if (TextUtils.isEmpty(zipCode)) {
            binding.zipCode.setError(getString(R.string.error_field_required));
            focusView = binding.zipCode;
            cancel = true;
        } else if (TextUtils.isEmpty(storePhone)) {
            binding.storePhone.setError(getString(R.string.error_field_required));
            focusView = binding.storePhone;
            cancel = true;
        } else if (TextUtils.isEmpty(contactPersonName)) {
            binding.contactPersonName.setError(getString(R.string.error_field_required));
            focusView = binding.contactPersonName;
            cancel = true;
        } else if (TextUtils.isEmpty(latitude)) {
            binding.latitude.setError(getString(R.string.error_field_required));
            focusView = binding.latitude;
            cancel = true;
        } else if (TextUtils.isEmpty(longitude)) {
            binding.longitude.setError(getString(R.string.error_field_required));
            focusView = binding.longitude;
            cancel = true;
        } else if (!Utils.isEmailValid(email)) {
            binding.email.setError(getString(R.string.error_invalid_email));
            focusView = binding.email;
            cancel = true;
        } else if (!Utils.isPasswordValid(password)) {
            binding.password.setError(getString(R.string.error_invalid_password));
            focusView = binding.password;
            cancel = true;
        } else if (!TextUtils.equals(password, confirmPassword)) {
            binding.confirmPassword.setError(getString(R.string.error_passwords_are_not_matched));
            focusView = binding.confirmPassword;
            cancel = true;
        } else if (!Utils.isMobileNumberValid(mobileNumber)) {
            binding.mobileNumber.setError(getString(R.string.error_invalid_mobile_number));
            focusView = binding.mobileNumber;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user signUp attempt.
            signUpTask = new AsyncTask(mBaseActivity);
            signUpTask.execute(storeName, storeAddress, city, state, zipCode, storePhone, contactPersonName, email, password, mobileNumber, latitude, longitude);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    private void showProgress(final boolean show) {
        //hide the relevant UI components.
        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.signUpLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        startLocationUpdates();
    }

    private void startLocationUpdates() {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
//        Utils.showToast(mBaseActivity, String.valueOf(location.getAccuracy()));
        if (location.getAccuracy() < 30) {
            binding.latitude.setText(String.valueOf(location.getLatitude()));
            binding.longitude.setText(String.valueOf(location.getLongitude()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
