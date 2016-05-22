package com.yathams.loginsystem.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.yathams.loginsystem.BaseActivity;
import com.yathams.loginsystem.R;
import com.yathams.loginsystem.model.PhoneContact;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
     *
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

    public static void showTermsAndConditionsDialog(final Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.FullScreenDialogTheme);
        dialog.setContentView(R.layout.terms_and_conditions);
        dialog.setCancelable(false);
        dialog.findViewById(R.id.buttonAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                readPhoneContacts(activity);
            }
        });
        dialog.findViewById(R.id.buttonDecline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static List<PhoneContact> readPhoneContacts(Context context) {
        List<PhoneContact> phoneContacts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        String phone = null;
        String emailContact = null;
        String emailType = null;
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                PhoneContact phoneContact = new PhoneContact();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                image_uri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);
//                    sb.append("\n Contact Name:" + name);
                    phoneContact.fisrtName = name;
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    String phoneNumber = "";
                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        sb.append("\n Phone number:" + phone);
                        System.out.println("phone" + phone);
                        phoneNumber+=phone+",";
                    }
                    pCur.close();
                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
//                        sb.append("\nEmail:" + emailContact + "Email type:" + emailType);
                        System.out.println("Email " + emailContact + " Email Type : " + emailType);
                        phoneContact.email = emailContact;
                    }
                    emailCur.close();
                }
                /*if (image_uri != null) {
                    System.out.println(Uri.parse(image_uri));
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(image_uri));
                        sb.append("\n Image in Bitmap:" + bitmap);
                        System.out.println(bitmap);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) { // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/

                if(!phoneContact.pnoneNumber.isEmpty()){
                    phoneContacts.add(phoneContact);
                }
            }
        }
        return phoneContacts;
    }
}
