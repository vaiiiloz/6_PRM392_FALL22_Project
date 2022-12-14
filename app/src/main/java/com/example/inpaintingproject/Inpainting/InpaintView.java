package com.example.inpaintingproject.Inpainting;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.inpaintingproject.Config.Config;
import com.example.inpaintingproject.Entity.CanvasFragment;
import com.example.inpaintingproject.Entity.InpaintCanvas;
import com.example.inpaintingproject.Entity.SettingFragment;
import com.example.inpaintingproject.Inpainting.InpaintPresenter;
import com.example.inpaintingproject.Interface.Inpainting;
import com.example.inpaintingproject.R;
import com.example.inpaintingproject.Tool.PaintTool;

import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.*;

public class InpaintView extends AppCompatActivity implements Inpainting.View, View.OnClickListener, Runnable {

    private Bitmap mBitmap = null;


    private static Context context;
    private ProgressBar mProgressBar;
    private Button inpaintButton;
    private Button cameraButton;
    private Button libraryButton;
    private Button undoButton;
    private Button redoButton;
    private Button saveButton;
    private Button drawButton;
    private Button circleButton;
    private Button eraseButton;
    private Button settingButton;
    private CanvasFragment canvasFragment;
    private SettingFragment settingFragment;
    private InpaintPresenter presenter;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private PaintTool paintTool;
    private boolean create;
    private Bitmap currentBitmap;


    public CanvasFragment getCanvasFragment() {
        return canvasFragment;
    }

    public SettingFragment getSettingFragment() {
        return settingFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inpainting_view);
        context = this.getApplicationContext();
        paintTool = new PaintTool(20);
        create = true;
        initView();
        registerListener();
        initPresenter();

        loadImage();


    }

    @Override
    protected void onStart() {

        super.onStart();
        initFragment();


    }

    private void initFragment(){
//        fragmentManager.beginTransaction()
//                .setReorderingAllowed(true)
//                .replace(R.id.fragment, canvasFragment, "canvas")
//                .commitNow();

//        settingFragment.setPaintTool(paintTool);
        presenter.loadBitmap(currentBitmap);
        canvasFragment.getInpaintCanvas().postInvalidate();
        Log.e("Hey", Integer.toString(canvasFragment.getInpaintCanvas().getMode()));


    }

    private void loadImage(){
        Bitmap bitmap = null;
        String filename = getIntent().getStringExtra("image");
        try{
            FileInputStream is = this.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentBitmap = bitmap;
//        presenter.loadBitmap(currentBitmap);
    }



    private void initView(){
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        inpaintButton = findViewById(R.id.inpaint_button);
        cameraButton = findViewById(R.id.camera_button);
        libraryButton = findViewById(R.id.library_button);
        undoButton = findViewById(R.id.UnRedo_button);
        redoButton = findViewById(R.id.Redo_button);
        saveButton = findViewById(R.id.save_button);
        drawButton = findViewById(R.id.paint);
        circleButton = findViewById(R.id.fast_paint);
        eraseButton = findViewById(R.id.eraser);
        settingButton = findViewById(R.id.setting);
        canvasFragment = new CanvasFragment(paintTool);
        settingFragment = new SettingFragment(paintTool);

        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment, settingFragment, "setting")
                .commitNow();

        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment, canvasFragment, "canvas")
                .commitNow();







    }



    private void initPresenter(){
        presenter = new InpaintPresenter(this, paintTool);
    }

    private void registerListener(){
        inpaintButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        libraryButton.setOnClickListener(this);
        undoButton.setOnClickListener(this);
        redoButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        circleButton.setOnClickListener(this);
        eraseButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        drawButton.setOnClickListener(this);

    }

    public void highLight(Button button){
        button.setEnabled(false);
    }

    public void unHighLight(Button button){
        button.setEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.camera_button:
                onCameraButton();
                break;
            case R.id.library_button:
                onLibraryButton();
                break;
            case R.id.UnRedo_button:
                onUndoButton();
                break;
            case R.id.Redo_button:
                onRedoButton();
                break;
            case R.id.save_button:
                onSaveButton();
                break;
            case R.id.paint:
                onDrawButton();
                break;
            case R.id.fast_paint:
                onCircleButton();
                break;
            case R.id.eraser:
                onEraseButton();
                break;
            case R.id.setting:
                onSettingButton();
                break;
            case R.id.inpaint_button:
                onInpaintButton();
                break;
            default:
                break;

        }
    }

    public static Context getContext() {
        return context;
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
                        currentBitmap = bitmap;
                        presenter.loadBitmap(currentBitmap);
                        canvasFragment.clear();

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
                                Log.e("find image", selectedImage.toString());
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                                Matrix matrix = new Matrix();
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                                cursor.close();
                                currentBitmap = bitmap;
                                presenter.loadBitmap(currentBitmap);
                                canvasFragment.clear();


                            }
                        }
                    }

                    break;

            }
        }
    }

    public void inpaint(){
        runOnUiThread(()->{
            mProgressBar.setVisibility(View.VISIBLE);
            enableButton(false);

        });
        Toast.makeText(this, "Begin inpaint", Toast.LENGTH_SHORT);
        Log.e("Begin", "OK");

    }

    public void enableButton(boolean permission){
        canvasFragment.getInpaintCanvas().setAllow(permission);

        inpaintButton.setEnabled(permission);
        cameraButton.setEnabled(permission);
        libraryButton.setEnabled(permission);
        undoButton.setEnabled(permission);
        redoButton.setEnabled(permission);
        saveButton.setEnabled(permission);
        drawButton.setEnabled(permission);
        circleButton.setEnabled(permission);
        eraseButton.setEnabled(permission);
        settingButton.setEnabled(permission);

    }





    @Override
    public void onCameraButton() {
        presenter.HandleCamera();

    }

    @Override
    public void onLibraryButton() {
        presenter.HandleLibrary();
    }

    @Override
    public void onRedoButton() {
        presenter.HandleRedo();
    }

    @Override
    public void onUndoButton() {
        presenter.HandleUndo();
    }

    @Override
    public void onSaveButton() {
        presenter.HandleSave();
    }

    @Override
    public void onDrawButton() {
        presenter.HandleDraw();
    }

    @Override
    public void onCircleButton() {
        presenter.HandleCircle();

    }

    @Override
    public void onEraseButton() {
        presenter.HandleErase();
    }

    @Override
    public void onSettingButton() {
        presenter.HandleSetting();

    }



    @Override
    public void onInpaintButton() {
        presenter.HandleInpaint();
    }


    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG);
    }

    @Override
    public InpaintCanvas getInpaintCanvas() {
        return canvasFragment.getInpaintCanvas();

    }

    @Override
    public Button getCameraButton() {
        return cameraButton;
    }

    @Override
    public Button getLibraryButton() {
        return libraryButton;
    }

    @Override
    public Button getRedoButton() {
        return redoButton;
    }

    @Override
    public Button getUndoButton() {
        return undoButton;
    }

    @Override
    public Button getSaveButton() {
        return saveButton;
    }

    @Override
    public Button getDrawButton() {
        return drawButton;
    }

    @Override
    public Button getCircleButton() {
        return circleButton;
    }

    @Override
    public Button getEraseButton() {
        return eraseButton;
    }

    @Override
    public Button getSettingButton() {
        return settingButton;
    }

    @Override
    public Button getInpaintButton() {
        return inpaintButton;
    }


    @Override
    public void run() {
        Bitmap output = presenter.runModule();
        runOnUiThread(() ->{

            mProgressBar.setVisibility(View.INVISIBLE);
            enableButton(true);
            canvasFragment.setImage(output);
            canvasFragment.clear();



        });
    }


}