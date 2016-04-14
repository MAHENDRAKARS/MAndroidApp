package com.yathams.loginsystem;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yathams.loginsystem.databinding.ActivityLoginBinding;
import com.yathams.loginsystem.model.LogInResponse;
import com.yathams.loginsystem.utils.Utils;
import com.yathams.loginsystem.utils.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_GET_ACCOUNTS = 1;
    private AsyncTask signInTask;
    private ActivityLoginBinding binding;
    private LogInResponse loginResponse;

    private String email = "";

    @Override
    public void onPreExecute() {
        showProgress(true);
        loginResponse = null;
    }

    @Override
    public String doInBackground(String[] params) {
        JSONObject jsonObject = new JSONObject();
        String response;
        if(params.length > 2){
            try {
                jsonObject.put("id", params[0]);
                jsonObject.put("email", params[1]);
                jsonObject.put("name", params[2]);
                email = params[1];
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("Request json", jsonObject.toString());
            response = Webservice.callPostService(Webservice.STORE_GPLUS_INFO, jsonObject.toString());
        }else {
            try {
                jsonObject.put("email", params[0]);
                jsonObject.put("password", params[1]);
                email = params[0];
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v("Request json", jsonObject.toString());
            response = Webservice.callPostService(Webservice.LOGIN, jsonObject.toString());
        }
        try {
            loginResponse = new Gson().fromJson(response, LogInResponse.class);
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
        if (loginResponse != null) {
            if (loginResponse.status == 0) { // success
                SharedPreferences preferences = mBaseActivity.getSharedPreferences("com.yathams.loginsystem", MODE_PRIVATE);
                preferences.edit().putString("email", email).putString("userId", loginResponse.userId).commit();
                startActivity(new Intent(mBaseActivity, HomeActivity.class));
                finish();
            } else { // Failed login shoe error message
                Utils.showToast(mBaseActivity, loginResponse.message);
            }
        } else {
            Utils.showToast(mBaseActivity, "Something went wrong");
        }
        signInTask = null;
        startActivity(new Intent(mBaseActivity, HomeActivity.class));
        finish();
    }

    @Override
    public void onCanceled() {
        signInTask = null;
        showProgress(false);
    }

    private static final String TAG = "LogInActivity";
    private static final int RC_SIGN_IN = 9001;

    private ConnectionResult mConnectionResult;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mBaseActivity = this;
        binding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        binding.emailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        binding.buttonForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
            }
        });

        binding.buttonSignUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });


        binding.signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleApiClient == null)
                mGoogleApiClient = new GoogleApiClient.Builder(mBaseActivity)
                        .addApi(Plus.API)
                        .addOnConnectionFailedListener(LoginActivity.this)
                        .addConnectionCallbacks(LoginActivity.this)
                        .addScope(new Scope(Scopes.PLUS_LOGIN))
                        .addScope(new Scope(Scopes.PLUS_ME))
                        .build();
                mGoogleApiClient.connect();
            }
        });


        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.GET_ACCOUNTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_ACCESS_GET_ACCOUNTS);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_ACCESS_GET_ACCOUNTS);

                // MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
//            doGetCurrentLocation();
        }

        SharedPreferences preferences = mBaseActivity.getSharedPreferences("com.yathams.loginsystem", MODE_PRIVATE);
        email = preferences.getString("email", "");
        if(!email.isEmpty()){
            startActivity(new Intent(mBaseActivity, HomeActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
//                    doGetCurrentLocation();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        if (!Utils.isNetworkAvailable(mBaseActivity)) {
            Utils.showNetworkAlertDialog(mBaseActivity);
            return;
        }
        if (signInTask != null) {
            return;
        }

        // Reset errors.
        binding.email.setError(null);
        binding.password.setError(null);

        // Store values at the time of the login attempt.
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            binding.email.setError(getString(R.string.error_field_required));
            focusView = binding.email;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            binding.password.setError(getString(R.string.error_field_required));
            focusView = binding.password;
            cancel = true;
        } else if (!isEmailValid(email)) {
            binding.email.setError(getString(R.string.error_invalid_email));
            focusView = binding.email;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            binding.password.setError(getString(R.string.error_invalid_password));
            focusView = binding.password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the user login attempt.
            signInTask = new AsyncTask(mBaseActivity);
            signInTask.execute(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    private void showProgress(final boolean show) {
        //hide the relevant UI components.
        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.emailLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }
        mConnectionResult = connectionResult;

        resolveSignInError();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
        // Get user's information
        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String id = currentPerson.getId();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

                Log.e(TAG, "Id: "+id+", Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);
                signInTask = new AsyncTask(mBaseActivity);
                signInTask.execute(id, email, personName);
                SharedPreferences preferences = mBaseActivity.getSharedPreferences("com.yathams.loginsystem", MODE_PRIVATE);
                preferences.edit().putString("email", email).putString("userName", personName).commit();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if(mConnectionResult != null)
        if (mConnectionResult.hasResolution()) {
            try {
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mGoogleApiClient.connect();
            }
        }
    }


}

