package com.example.inpaintingproject.Entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.inpaintingproject.Config.Config;
import com.example.inpaintingproject.R;
import com.example.inpaintingproject.Tool.PaintTool;

import java.util.ArrayList;

public class CanvasFragment extends Fragment {
    private InpaintCanvas inpaintCanvas;
    private PaintTool paintTool;
    private Canvas mask = null;
    private ArrayList<cPath> paths;
    private ArrayList<cPath> undonePaths;
    private Path mPath;
    private Bitmap mMask = null;
    private Bitmap backgroundBitmap = null;
    private Canvas backgroundCanvas = null;
    private Bitmap backgroundImage = null;

    public CanvasFragment(PaintTool paintTool){
        this.paintTool = paintTool;
        paths = new ArrayList<>();
        undonePaths = new ArrayList<>();

        mPath = new Path();

        setupMask(Config.SceenWidth, Config.SceenHeight);
        setupBackground(Config.SceenWidth, Config.SceenHeight);
    }

    public void clear(){
        paths.clear();
        undonePaths.clear();
        setupMask(Config.SceenWidth, Config.SceenHeight);
        setupBackground(Config.SceenWidth, Config.SceenHeight);
        if (inpaintCanvas!=null){
            inpaintCanvas.clear();
//            inpaintCanvas.postInvalidate();
        }

    }

    public void setImage(Bitmap bitmap){
        backgroundImage = Bitmap.createScaledBitmap(bitmap, Config.SceenWidth, Config.SceenHeight, true);
        if (inpaintCanvas != null){
            inpaintCanvas.setBackgroundImage(backgroundImage);
        }
    }

    private void setupMask(int width, int height){
        mMask = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mask = new Canvas(mMask);
    }

    private void setupBackground(int width, int height){
        backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        backgroundCanvas = new Canvas(backgroundBitmap);
    }

    public InpaintCanvas getInpaintCanvas() {
        return inpaintCanvas;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.canvas_fragment, container, false);
        inpaintCanvas = layout.findViewById(R.id.inpaintCanvas);

        setupCanvas();
        return layout;
    }

    public void setupCanvas(){

        inpaintCanvas.getLayoutParams().width = Config.SceenWidth;
        inpaintCanvas.getLayoutParams().height = Config.SceenHeight;

        inpaintCanvas.setPaintTool(paintTool);
        inpaintCanvas.setMask(mask);
        inpaintCanvas.setPaths(paths);
        inpaintCanvas.setUndonePaths(undonePaths);
        inpaintCanvas.setmPath(mPath);
        inpaintCanvas.setmMask(mMask);
        inpaintCanvas.setBackgroundBitmap(backgroundBitmap);
        inpaintCanvas.setBackgroundCanvas(backgroundCanvas);
        inpaintCanvas.setBackgroundImage(backgroundImage);
        inpaintCanvas.postInvalidate();

    }


}
