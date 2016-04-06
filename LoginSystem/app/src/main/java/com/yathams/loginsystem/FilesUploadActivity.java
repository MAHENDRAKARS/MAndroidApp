package com.yathams.loginsystem;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ArrayAdapter;

import com.yathams.loginsystem.adapters.ImagesAdapter;
import com.yathams.loginsystem.adapters.ProductNamesAdapter;
import com.yathams.loginsystem.database.DBAdapter;
import com.yathams.loginsystem.databinding.ActivityFilesUploadBinding;
import com.yathams.loginsystem.pojo.ImageItem;
import com.yathams.loginsystem.pojo.ProductItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FilesUploadActivity extends BaseActivity {

    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    private static final int REQUEST_CAMERA = 2;
    private ActivityFilesUploadBinding binding;
    private DBAdapter dbAdapter;
    private String email = "";

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

    private ImagesAdapter imagesAdapter;
    private ProductNamesAdapter namesAdapter;
    private List<String> names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_files_upload);
        mBaseActivity = this;
        dbAdapter = new DBAdapter(getApplicationContext());
        binding.imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImagesAdapter(this, new ArrayList<ImageItem>());
        binding.imagesRecyclerView.setAdapter(imagesAdapter);
        binding.gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
            }
        });

        ArrayAdapter adapter;

        new android.os.AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                dbAdapter.open();
                names = dbAdapter.getProductNameStrings();
                dbAdapter.close();
                return  null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ArrayAdapter adapter = new ArrayAdapter(mBaseActivity, android.R.layout.simple_list_item_1, names);
                binding.productName.setAdapter(adapter);
            }
        }.execute();
        binding.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        binding.productNamesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        namesAdapter = new ProductNamesAdapter(this, new ArrayList<ProductItem>());
        binding.productNamesRecyclerView.setAdapter(namesAdapter);

        binding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.productName.getText().toString().trim();
                if(!name.isEmpty()){
                    binding.productName.setText("");
                    namesAdapter.addProduct(new ProductItem(name));
                }
            }
        });

        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mBaseActivity, SelectStoreActivity.class));
            }
        });

        SharedPreferences preferences = mBaseActivity.getSharedPreferences("com.yathams.loginsystem", MODE_PRIVATE);
        email = preferences.getString("email", "");
        getSupportActionBar().setTitle(email);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
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
