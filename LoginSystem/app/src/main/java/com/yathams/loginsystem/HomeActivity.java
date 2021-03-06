package com.yathams.loginsystem;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.yathams.loginsystem.adapters.ImagesAdapter;
import com.yathams.loginsystem.adapters.ProductNamesAdapter;
import com.yathams.loginsystem.database.DBAdapter;
import com.yathams.loginsystem.databinding.ActivityFilesUploadBinding;
import com.yathams.loginsystem.databinding.ActivityHomeBinding;
import com.yathams.loginsystem.pojo.ImageItem;
import com.yathams.loginsystem.pojo.ProductItem;
import com.yathams.loginsystem.pojo.UserAccount;
import com.yathams.loginsystem.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private static final int REQUEST_CAMERA = 2;
    private static  final int USER_ACCOUNT = 3;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 4;
    private ActivityHomeBinding binding;
    private DBAdapter dbAdapter;
    private String email = "";
    private ImagesAdapter imagesAdapter;
    private ProductNamesAdapter namesAdapter;
    private List<String> names;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        mBaseActivity = this;
        dbAdapter = new DBAdapter(getApplicationContext());
        binding.appBarHomeInclude.contentHomeInclude.imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImagesAdapter(this, new ArrayList<ImageItem>());
        binding.appBarHomeInclude.contentHomeInclude.imagesRecyclerView.setAdapter(imagesAdapter);
        binding.appBarHomeInclude.contentHomeInclude.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
            }
        });

        Toolbar toolbar = binding.appBarHomeInclude.toolbar;
        setSupportActionBar(toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        new android.os.AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                dbAdapter.open();
                names = dbAdapter.getProductNameStrings();
                dbAdapter.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ArrayAdapter adapter = new ArrayAdapter(mBaseActivity, android.R.layout.simple_list_item_1, names);
                binding.appBarHomeInclude.contentHomeInclude.productName.setAdapter(adapter);
            }
        }.execute();
        binding.appBarHomeInclude.contentHomeInclude.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        binding.appBarHomeInclude.contentHomeInclude.productNamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        namesAdapter = new ProductNamesAdapter(this, new ArrayList<ProductItem>());
        binding.appBarHomeInclude.contentHomeInclude.productNamesRecyclerView.setAdapter(namesAdapter);

        binding.appBarHomeInclude.contentHomeInclude.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.appBarHomeInclude.contentHomeInclude.productName.getText().toString().trim();
                if (!name.isEmpty()) {
                    binding.appBarHomeInclude.contentHomeInclude.productName.setText("");
                    namesAdapter.addProduct(new ProductItem(name));
                }
            }
        });

        binding.appBarHomeInclude.contentHomeInclude.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mBaseActivity, SelectStoreActivity.class));
            }
        });

        SharedPreferences preferences = mBaseActivity.getSharedPreferences("com.yathams.loginsystem", MODE_PRIVATE);
        email = preferences.getString("email", "");
        getSupportActionBar().setTitle(email);
        if(!email.isEmpty()){
            navigationView.getMenu().removeItem(R.id.nav_provider_login);
        }

        Utils.showTermsAndConditionsDialog(mBaseActivity);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(mBaseActivity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mBaseActivity,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(mBaseActivity,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Utils.readPhoneContacts(mBaseActivity);
                }
            }).start();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Utils.readPhoneContacts(mBaseActivity);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user_account) {
            SharedPreferences preferences = mBaseActivity.getSharedPreferences("com.yathams.loginsystem", MODE_PRIVATE);
            if(preferences.getString("email", "").isEmpty()){
                startActivity(new Intent(mBaseActivity, LoginActivity.class));
                finish();
            }else {
                startActivityForResult(new Intent(mBaseActivity, UserAccountActivity.class), USER_ACCOUNT);
            }
        } else if (id == R.id.nav_open_requests) {
            startActivity(new Intent(mBaseActivity, ProviderSignUpActivity.class));
        } else if (id == R.id.nav_faq) {

        } else if (id == R.id.nav_terms) {

        } else if (id == R.id.nav_privacy) {

        } else if (id == R.id.nav_provider_login) {
            Intent loginIntent = new Intent(mBaseActivity, LoginActivity.class);
            loginIntent.putExtra("userType", "Provider");
            startActivity(loginIntent);
        } else if (id == R.id.nav_contact_us) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == USER_ACCOUNT && resultCode == Activity.RESULT_OK){
            startActivity(new Intent(mBaseActivity, LoginActivity.class));
            finish();
        }
        else if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ImageItem imageItem = new ImageItem(data.getData());
                imageItem.uri = data.getData();
                imagesAdapter.addImage(imageItem);
//                InputStream inputStream = mBaseActivity.getContentResolver().openInputStream(data.getData());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        } else if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".jpg");
            FileOutputStream fo;
            try {
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 4;
//            Bitmap bitmap = BitmapFactory.decodeFile(uri.toString(), options);
            ImageItem imageItem = new ImageItem(thumbnail);
//            imageItem.uri = uri;
            imagesAdapter.addImage(imageItem);
        }
    }
}
