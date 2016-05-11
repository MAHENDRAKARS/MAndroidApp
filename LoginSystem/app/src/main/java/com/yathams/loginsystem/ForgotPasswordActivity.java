package com.yathams.loginsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yathams.loginsystem.model.FogotPasswordResponse;
import com.yathams.loginsystem.model.Response;
import com.yathams.loginsystem.utils.Utils;
import com.yathams.loginsystem.utils.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends BaseActivity {

    @Override
    public void onPreExecute() {
        showProgress(true);
        forgotPasswordResponse = null;
    }

    @Override
    public String doInBackground(String[] params) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", params[0]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("Request json", jsonObject.toString());
        String response = Webservice.callPostService(Webservice.FORGOT_PASSWORD, jsonObject.toString());

        try {
            forgotPasswordResponse = new Gson().fromJson(response, FogotPasswordResponse.class);
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

        if(forgotPasswordResponse != null){
            if(forgotPasswordResponse.status == 1){ //success
                Intent intent = new Intent(mBaseActivity, ResetPasswordActivity.class);
                intent.putExtra("userId", forgotPasswordResponse.userID);
                startActivity(intent);
                finish();
            }
            Utils.showToast(mBaseActivity, forgotPasswordResponse.message);

        }else{
            Utils.showToast(mBaseActivity, "Something went wrong");
        }
        forgotTask = null;
    }

    @Override
    public void onCanceled() {
        forgotTask = null;
        showProgress(false);
    }

    private AsyncTask forgotTask;
    private EditText emailEditText;
    private View forgotLayout;
    private ProgressBar progressBar;
    private FogotPasswordResponse forgotPasswordResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mBaseActivity = this;

        emailEditText = (EditText) findViewById(R.id.email);
        forgotLayout = findViewById(R.id.forgotLayout);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

    }

    public void submitButtonClicked(View view) {

        if(!Utils.isNetworkAvailable(mBaseActivity)){
            Utils.showNetworkAlertDialog(mBaseActivity);
            return;
        }

        if(forgotTask != null){
            return;
        }

        // Reset errors.
        emailEditText.setError(null);
        String email = emailEditText.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.error_field_required));
            focusView = emailEditText;
            cancel = true;
        }else if(!Utils.isEmailValid(email)){
            emailEditText.setError(getString(R.string.error_invalid_email));
            focusView = emailEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user signUp attempt.
            forgotTask = new AsyncTask(mBaseActivity);
            forgotTask.execute(email);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    private void showProgress(final boolean show) {
        //hide the relevant UI components.
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        forgotLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
