package com.yathams.loginsystem;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yathams.loginsystem.databinding.ActivitySignUpBinding;
import com.yathams.loginsystem.model.Response;
import com.yathams.loginsystem.utils.Utils;
import com.yathams.loginsystem.utils.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends BaseActivity {

    @Override
    public void onPreExecute() {
        showProgress(true);
        signUpResponse = null;
    }

    @Override
    public String doInBackground(String[] params) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", params[0]);
            jsonObject.put("password", params[1]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("Request json", jsonObject.toString());
        String response = Webservice.callPostService(Webservice.SIGN_UP, jsonObject.toString());
        try {
            signUpResponse = new Gson().fromJson(response, Response.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute() {
        showProgress(false);
        if(signUpResponse != null){
            if(signUpResponse.status == 0){ //Sign up success
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

    private ActivitySignUpBinding binding;
    private AsyncTask signUpTask;
    private Response signUpResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mBaseActivity = this;
        binding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptSignUp();
            }
        });
    }

    private void attemptSignUp() {
        if(!Utils.isNetworkAvailable(mBaseActivity)){
            Utils.showNetworkAlertDialog(mBaseActivity);
            return;
        }

        if(signUpTask != null){
            return;
        }
        // Reset errors.
        binding.email.setError(null);
        binding.password.setError(null);
        binding.confirmPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = binding.email.getText().toString();
        String password = binding.password.getText().toString();
        String confirmPassword = binding.confirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(email)){
            binding.email.setError(getString(R.string.error_field_required));
            focusView = binding.email;
            cancel = true;
        }else if(TextUtils.isEmpty(password)){
            binding.password.setError(getString(R.string.error_field_required));
            focusView = binding.password;
            cancel = true;
        }else if(TextUtils.isEmpty(confirmPassword)){
            binding.confirmPassword.setError(getString(R.string.error_field_required));
            focusView = binding.confirmPassword;
            cancel = true;
        }else if(!Utils.isEmailValid(email)){
            binding.email.setError(getString(R.string.error_invalid_email));
            focusView = binding.email;
            cancel = true;
        }else if(!Utils.isPasswordValid(password)){
            binding.password.setError(getString(R.string.error_invalid_password));
            focusView = binding.password;
            cancel = true;
        }else if(!TextUtils.equals(password, confirmPassword)){
            binding.confirmPassword.setError(getString(R.string.error_passwords_are_not_matched));
            focusView = binding.confirmPassword;
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
            signUpTask.execute(email, password);
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

}
