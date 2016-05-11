package com.yathams.loginsystem.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.yathams.loginsystem.BaseActivity;
import com.yathams.loginsystem.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by vyatham on 14/03/16.
 */
public class Utils {

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    public static boolean isMobileNumberValid(String mobileNumber) {
        return mobileNumber.length() == 10;
    }

    public static void showToast(BaseActivity mBaseActivity, String message) {
        Toast.makeText(mBaseActivity, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Function to get Internet connectivity status
     * @return true, if internet conneced.false not connected.
     */
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showNetworkAlertDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage(activity.getString(R.string.no_internet))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public static String readTextFileFromAssets(Context context, String fileName) {
        String text = "";
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(fileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = is.read();
            }
            is.close();
            text = byteArrayOutputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;

    }
}
