package com.example.inpaintingproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.inpaintingproject.Config.Config;
import com.example.inpaintingproject.Entity.InpaintCanvas;
import com.example.inpaintingproject.Inpainting.InpaintPresenter;
import com.example.inpaintingproject.Inpainting.InpaintView;
import com.example.inpaintingproject.Interface.Inpainting;
import com.example.inpaintingproject.Interface.Main;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.*;

public class MainActivity extends AppCompatActivity implements Main.View, View.OnClickListener {




    private static Context context;

    private Button cameraButton;
    private Button libraryButton;
    private MainPresenter presenter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this.getApplicationContext();
        requestPermission();
        initView();
        registerListener();
    }


    private void requestPermission(){
        //request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    private void initView(){

        cameraButton = findViewById(R.id.camera_button_main);
        libraryButton = findViewById(R.id.library_button_main);

    }


    private void registerListener(){
        cameraButton.setOnClickListener(this);
        libraryButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.camera_button_main:
                onCameraClick();
                break;
            case R.id.library_button_main:
                onLibraryClick();
                break;
            default:
                break;

        }
    }

    @Override
    public void onCameraClick() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, Main.View.CAMERA_CODE);
    }

    @Override
    public void onLibraryClick() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, Main.View.LIBRARY_CODE);
    }

    public static Context getContext() {
        return context;
    }

    private void toInpaintActivity(Bitmap bitmap){

        try {
            // write file
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //clean up
            stream.close();
            bitmap.recycle();

            //Pop intent
            Intent inpaintIntent = new Intent(this, InpaintView.class);
            inpaintIntent.putExtra("image", filename);
            startActivity(inpaintIntent);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!=RESULT_CANCELED){
            switch (requestCode){
                case CAMERA_CODE:
                    if (resultCode == RESULT_OK && data!=null) {
                        Log.e("Camera", "Ok");
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90.0f);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        toInpaintActivity(bitmap);


                    }
                    break;
                case LIBRARY_CODE:
                    if (resultCode == RESULT_OK && data!=null){
                        Log.e("Library", "Ok");
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null){
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null){
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                Matrix matrix = new Matrix();
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                cursor.close();
                                toInpaintActivity(bitmap);
                            }
                        }
                    }

                    break;

            }
        }
    }



}