package com.example.inpaintingproject;

import android.content.Intent;
import android.provider.MediaStore;
import com.example.inpaintingproject.Interface.Inpainting;
import com.example.inpaintingproject.Interface.Main;

public class MainPresenter implements Main.Presenter {
    Main.View view;

    @Override
    public void cameraHandle() {


    }

    @Override
    public void libraryHandle() {

    }

    @Override
    public void setView(Main.View view) {
        this.view = view;
    }
}
