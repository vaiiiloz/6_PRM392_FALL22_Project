package com.example.inpaintingproject.Entity;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.inpaintingproject.Config.Config;
import com.example.inpaintingproject.Config.DrawTool;
import com.example.inpaintingproject.Tool.PaintTool;
import com.example.inpaintingproject.Utils.utils;

import java.util.ArrayList;

public class InpaintCanvas extends View  {

    private PaintTool paintTool;
    private Canvas mask = null;
    private ArrayList<cPath> paths;
    private ArrayList<cPath> undonePaths;
    private Path mPath;
    private int mode=-1;
    private Bitmap mMask = null;
    private float mX, mY;
    private Bitmap backgroundBitmap = null;
    private Canvas backgroundCanvas = null;
    private Bitmap backgroundImage = null;

    private static final float TOUCH_TOLERANCE = 4;

    public InpaintCanvas(Context context) {

        super(context);
        setAllow(true);
        mode = DrawTool.CreateMode;

//        paths = new ArrayList<>();
//        undonePaths = new ArrayList<>();

//
//        mPath = new Path();
//
//        setupMask(Config.SceenWidth, Config.SceenHeight);
//        setupBackground(Config.SceenWidth, Config.SceenHeight);
    }


    public InpaintCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAllow(true);
        mode = DrawTool.CreateMode;
//        paths = new ArrayList<>();
//        undonePaths = new ArrayList<>();

//
//        mPath = new Path();
//
//        setupMask(Config.SceenWidth, Config.SceenHeight);
//        setupBackground(Config.SceenWidth, Config.SceenHeight);

    }

    public void setPaintTool(PaintTool paintTool) {
        this.paintTool = paintTool;
    }

    public void setMask(Canvas mask) {
        this.mask = mask;
    }

    public void setPaths(ArrayList<cPath> paths) {
        this.paths = paths;
    }

    public void setUndonePaths(ArrayList<cPath> undonePaths) {
        this.undonePaths = undonePaths;
    }

    public void setmPath(Path mPath) {
        this.mPath = mPath;
    }

    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        this.backgroundBitmap = backgroundBitmap;
    }

    public void setBackgroundCanvas(Canvas backgroundCanvas) {
        this.backgroundCanvas = backgroundCanvas;
    }

    public void setBackgroundImage(Bitmap backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    private void setupMask(int width, int height){


        mMask = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mask = new Canvas(mMask);
    }

    private void setupBackground(int width, int height){


        backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        backgroundCanvas = new Canvas(backgroundBitmap);
    }



    public void setAllow(boolean permission){
        setFocusable(permission);
        setFocusableInTouchMode(permission);
        setDrawingCacheEnabled(permission);
    }

    public void setImage(Bitmap bitmap){
//        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//
//        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN){
//            this.setBackground(drawable);
//        }else{
//            this.setBackgroundDrawable(drawable);
//        }
        backgroundImage = Bitmap.createScaledBitmap(bitmap, Config.SceenWidth, Config.SceenHeight, true);
        postInvalidate();
    }

    public Bitmap getmMask() {
        return mMask;
    }

    public void setmMask(Bitmap mMask) {
        this.mMask = mMask;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.getVisibility() == View.INVISIBLE){
            return true;
        }

        if (mode==-1){
            return true;
        }
        //get current Coordination
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //set start point end point


                touch_down(x, y);

                //redraw
                postInvalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                //set new end point
                touch_move(x, y);
                //redraw
                postInvalidate();
                break;

            case MotionEvent.ACTION_UP:

                touch_up();
                postInvalidate();
                break;
        }
        return true;
    }

    private void touch_down(float x, float y){
        undonePaths.clear();
        mPath.reset();
        mPath.moveTo(x, y);
        paths.add(new cPath(mPath, mode));
//        paths.get(paths.size()-1).path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y){
//        Log.e("X", Float.toString(x));
//        Log.e("Y", Float.toString(y));
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx>=TOUCH_TOLERANCE || dy>=TOUCH_TOLERANCE){
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;



        }


    }

    private void touch_up(){
        mPath.lineTo(mX, mY);

        // commit to mask
        addMask(mPath, paintTool.getGrayPaint());


        mPath = new Path();

    }

    private void addMask(Path mPath, Paint grayPaint) {

        drawPath(mPath, mask, grayPaint, mode);
    }

    private void createMask(){
        for (cPath p:paths){
            drawPath(p.path, mask, paintTool.getGrayPaint(), p.mode);
        }
    }

    private void drawPath(Path mPath, Canvas canvas, Paint paint, int mode) {

        switch (mode){
            case DrawTool.DrawMode:
                drawLine(mPath, canvas, paint);
                break;
            case DrawTool.CircleMode:
                drawPolygon(mPath, canvas, paint);
                break;
            case DrawTool.EraserMode:
                drawLine(mPath, canvas, paintTool.getEraser());


                break;
            default:
                break;
        }
    }

    private void drawPolygon(Path mPath, Canvas canvas, Paint paint){
        paint.setStyle(Paint.Style.FILL);


        canvas.drawPath(mPath, paint);

    }

    private void drawLine(Path mPath, Canvas canvas, Paint paint){

        paint.setStyle(Paint.Style.STROKE);

        canvas.drawPath(mPath, paint);

    }

    private void reset(){
        setupMask(Config.SceenWidth, Config.SceenHeight);
        setupBackground(Config.SceenWidth, Config.SceenHeight);

        createMask();
    }


    public boolean Undo(){
        if (paths.size()>0){
            undonePaths.add(paths.remove(paths.size()-1));
            reset();
            postInvalidate();
            return true;
        }
        return false;
    }

    public boolean Redo(){
        if (undonePaths.size() >0){
            paths.add(undonePaths.remove(undonePaths.size()-1));
            reset();
            postInvalidate();
            return true;
        }
        return false;
    }

    public int getPathsLength(){
        return paths.size();
    }

    public int getUndonePathsLength(){
        return undonePaths.size();
    }

    public void clear(){
        setupMask(Config.SceenWidth, Config.SceenHeight);
        setupBackground(Config.SceenWidth, Config.SceenHeight);
        paths.clear();
        undonePaths.clear();
        postInvalidate();
    }

    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    @Override
    public void onDraw(Canvas canvas){
        Log.e("paint", Integer.toString(mode));
        canvas.drawBitmap(backgroundImage, 0, 0, null);

        if (mode!=DrawTool.NONEMODE){
            if (mode==DrawTool.CreateMode){
                mode = DrawTool.NONEMODE;

            }

            for (cPath p:paths){
                    drawPath(p.path, backgroundCanvas, paintTool.getDrawPaint(), p.mode);
//                }
            }
            canvas.drawBitmap(utils.drawMask(backgroundImage, backgroundBitmap), 0, 0, null);

        }

    }



    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
