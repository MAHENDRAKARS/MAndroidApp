package com.yathams.loginsystem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by vyatham on 14/03/16.
 */
public abstract class BaseActivity extends AppCompatActivity{

    protected BaseActivity mBaseActivity;

    public abstract void onPreExecute();

    public abstract String doInBackground(String[] params);

    public abstract void onPostExecute();

    public abstract void onCanceled();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
