package com.example.inpaintingproject.Inpainting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import com.example.inpaintingproject.Config.DrawTool;
import com.example.inpaintingproject.Interface.Inpainting;

import java.io.IOException;

public class InpaintPresenter implements Inpainting.Presenter{
    private InpaintModel model;
    private InpaintView view;
    private Button currentDrawButton = null;

    public InpaintPresenter(InpaintView view) {
        model = new InpaintModel();
        this.view = view;

    }

    private void test(){
        try{
            model.setImage(BitmapFactory.decodeStream(view.getAssets().open(Inpainting.View.emptyImage)));

        }catch (IOException e){
            e.printStackTrace();
            view.finish();
        }

        view.getInpaintCanvas().setImage(model.getImage());
    }

    private void unhighlightDraw(){
        if (currentDrawButton!=null){
            view.unHighLight(currentDrawButton);
            currentDrawButton = null;
        }
    }


    private void configDraw(int mode, boolean permission){
        view.getInpaintCanvas().setMode(mode);
        view.getInpaintCanvas().setAllow(permission);
    }



    @Override
    public void HandleCamera() {
        unhighlightDraw();
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        view.startActivityForResult(takePicture, Inpainting.View.CAMERA_CODE);
    }

    @Override
    public void HandleLibrary() {
        unhighlightDraw();
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        view.startActivityForResult(pickPhoto, Inpainting.View.LIBRARY_CODE);
    }

    @Override
    public void HandleRedo() {
        unhighlightDraw();


        if (!view.getInpaintCanvas().Redo()){
            view.showMessage("Can not redo");
        }
    }

    @Override
    public void HandleUndo() {
        unhighlightDraw();


        if (!view.getInpaintCanvas().Undo()){
            view.showMessage("Can not undo");
        }

    }

    @Override
    public void HandleSave() {
        unhighlightDraw();
        model.save();
        view.showMessage("Save Image");

    }

    @Override
    public void HandleDraw() {

        if (view.getInpaintCanvas().getMode() == DrawTool.DrawMode){
            unhighlightDraw();
            configDraw(DrawTool.NONEMODE, false);
        }else{
            unhighlightDraw();
            currentDrawButton = view.getDrawButton();
            view.highLight(currentDrawButton);
            configDraw(DrawTool.DrawMode, true);
        }
    }


    @Override
    public void HandleCircle() {
        if (view.getInpaintCanvas().getMode() == DrawTool.CircleMode){
            unhighlightDraw();
            configDraw(DrawTool.NONEMODE, false);
        }else{
            unhighlightDraw();
            currentDrawButton = view.getCircleButton();
            view.highLight(currentDrawButton);
            configDraw(DrawTool.CircleMode, true);
        }
    }

    @Override
    public void HandleErase() {
        if (view.getInpaintCanvas().getMode() == DrawTool.EraserMode){
            unhighlightDraw();
            configDraw(DrawTool.NONEMODE, false);
        }else{
            unhighlightDraw();
            currentDrawButton = view.getEraseButton();
            view.highLight(currentDrawButton);
            configDraw(DrawTool.EraserMode, true);
        }
    }

    @Override
    public void HandleSetting() {
        unhighlightDraw();
    }

    @Override
    public void HandleInpaint() {

        try {
            view.inpaint();
            unhighlightDraw();
            Thread.sleep(1);
            if (view.getInpaintCanvas().getmMask()!=null){

                Thread thread = new Thread(view);
                thread.start();

                Log.e("Finish","OK");

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Bitmap runModule(){
        if (view.getInpaintCanvas().getmMask()!=null){
            model.setMask(view.getInpaintCanvas().getmMask());

            return model.runModule();
        }
        return null;
    }

    @Override
    public void loadBitmap(Bitmap bitmap) {
        model.setImage(bitmap);
        view.getInpaintCanvas().setImage(model.getImage());
    }


}
