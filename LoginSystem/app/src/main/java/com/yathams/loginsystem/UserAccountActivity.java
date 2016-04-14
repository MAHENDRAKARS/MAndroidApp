package com.yathams.loginsystem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yathams.loginsystem.databinding.ActivityUserAccountBinding;

public class UserAccountActivity extends BaseActivity {

    private ActivityUserAccountBinding binding;

    @Override
    public void onPreExecute() {

    }

    @Override
    public String doInBackground(String[] params) {
        return null;
    }

    @Override
    public void onPostExecute() {

    }

    @Override
    public void onCanceled() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_account);
        mBaseActivity = this;
        final SharedPreferences preferences = mBaseActivity.getSharedPreferences("com.yathams.loginsystem", MODE_PRIVATE);
        binding.editTextEmail.setText(preferences.getString("email", ""));
        binding.textViewUserName.setText("Hello "+preferences.getString("email", ""));
        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString("email", "").putString("userName", "").putString("userId", "").commit();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

    }
}
