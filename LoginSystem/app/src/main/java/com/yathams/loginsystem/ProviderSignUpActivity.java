package com.yathams.loginsystem;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.yathams.loginsystem.databinding.ActivityProviderSignupBinding;

public class ProviderSignUpActivity extends BaseActivity {

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

    private ActivityProviderSignupBinding binding;
    private String androidUUID;
    private String deviceName;
    private String deviceManufacturer;
    private String serial;
    private String version;
    private String platform = "Android";

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
    }
}
