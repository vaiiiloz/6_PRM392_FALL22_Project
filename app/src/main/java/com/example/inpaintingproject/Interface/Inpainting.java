package com.example.inpaintingproject.Interface;

import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.inpaintingproject.Entity.InpaintCanvas;
import org.pytorch.Module;

public interface Inpainting {
    interface View {
        static final String emptyImage = "image1.jpg";
        static final String modelPath = "deepfill.torchscript.ptl";
        static final int CAMERA_CODE = 0;
        static final int LIBRARY_CODE = 1;


        void onCameraButton();
        void onLibraryButton();
        void onRedoButton();
        void onUndoButton();
        void onSaveButton();
        void onDrawButton();
        void onCircleButton();
        void onEraseButton();
        void onSettingButton();
        void onInpaintButton();

        InpaintCanvas getInpaintCanvas();
        Button getCameraButton();
        Button getLibraryButton();
        Button getRedoButton();
        Button getUndoButton();
        Button getSaveButton();
        Button getDrawButton();
        Button getCircleButton();
        Button getEraseButton();
        Button getSettingButton();
        Button getInpaintButton();


        void showMessage(String message);
    }

    interface Presenter{
        void HandleCamera();
        void HandleLibrary();
        void HandleRedo();
        void HandleUndo();
        void HandleSave();
        void HandleDraw();
        void HandleCircle();
        void HandleErase();
        void HandleSetting();
        void HandleInpaint();


        void loadBitmap(Bitmap bitmap);
    }

    interface Model{




    }
}
