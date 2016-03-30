package com.yathams.loginsystem;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yathams.loginsystem.databinding.ActivityResetPasswordBinding;
import com.yathams.loginsystem.model.Response;
import com.yathams.loginsystem.utils.Utils;
import com.yathams.loginsystem.utils.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends BaseActivity {

    @Override
    public void onPreExecute() {
        showProgress(true);
        resetPasswordResponse = null;
    }

    @Override
    public String doInBackground(String[] params) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("oldPassword", params[0]);
            jsonObject.put("newPassword", params[1]);
            jsonObject.put("userId", "");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("Request json", jsonObject.toString());
        String response = Webservice.callPostService(Webservice.RESET_PASSWORD, jsonObject.toString());
        try {
            resetPasswordResponse = new Gson().fromJson(response, Response.class);
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
        if(resetPasswordResponse != null){
            if(resetPasswordResponse.status == 0){ //success
                binding.currentPassword.setText("");
                binding.password.setText("");
                binding.confirmPassword.setText("");
            }
                Utils.showToast(mBaseActivity, resetPasswordResponse.message);
        }else{
            Utils.showToast(mBaseActivity, "Something went wrong");
        }

    }

    @Override
    public void onCanceled() {
        resetPasswordTask = null;
        showProgress(false);
    }

    private AsyncTask resetPasswordTask;
    private ActivityResetPasswordBinding binding;
    private Response resetPasswordResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
        mBaseActivity = this;

        binding.buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Utils.isNetworkAvailable(mBaseActivity)){
                    Utils.showNetworkAlertDialog(mBaseActivity);
                    return;
                }

                if(resetPasswordTask != null){
                    return;
                }
                // Reset errors.
                binding.currentPassword.setError(null);
                binding.password.setError(null);
                binding.confirmPassword.setError(null);

                // Store values at the time of the login attempt.
                String currentPassword = binding.currentPassword.getText().toString();
                String password = binding.password.getText().toString();
                String confirmPassword = binding.confirmPassword.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if(TextUtils.isEmpty(currentPassword)){
                    binding.currentPassword.setError(getString(R.string.error_field_required));
                    focusView = binding.currentPassword;
                    cancel = true;
                }else if(TextUtils.isEmpty(password)){
                    binding.password.setError(getString(R.string.error_field_required));
                    focusView = binding.password;
                    cancel = true;
                }else if(TextUtils.isEmpty(confirmPassword)){
                    binding.confirmPassword.setError(getString(R.string.error_field_required));
                    focusView = binding.confirmPassword;
                    cancel = true;
                }else if(!Utils.isPasswordValid(currentPassword)){
                    binding.currentPassword.setError(getString(R.string.error_invalid_password));
                    focusView = binding.currentPassword;
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
                    resetPasswordTask = new AsyncTask(mBaseActivity);
                    resetPasswordTask.execute(currentPassword, password);
                }

            }
        });

    }

    /**
     * Shows the progress UI and hides the login form.
     */

    private void showProgress(final boolean show) {
        //hide the relevant UI components.
        binding.loginProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.resetPasswordLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
