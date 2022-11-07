package com.example.inpaintingproject.Tool;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.example.inpaintingproject.Config.DrawTool;

public class PaintTool {
    private Paint drawPaint;
    private Paint grayPaint;
    private Paint eraser;

    public PaintTool( int StrokeWidth) {
        setup(StrokeWidth);
    }

    private void setup( int StrokeWidth){
        //set up paint and stoke style
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLUE);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(StrokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);

        //set up gray paint
        grayPaint = new Paint();
        grayPaint.setColor(Color.WHITE);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        grayPaint.setColorFilter(f);

        eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setStrokeWidth(StrokeWidth);
        eraser.setStyle(Paint.Style.STROKE);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void changeStyle(int paint, Paint.Style style){
        switch (paint){
            case DrawTool.DrawPaint:
                drawPaint.setStyle(style);
                break;
            case DrawTool.GrayPaint:
                grayPaint.setStyle(style);
                break;
            case DrawTool.EraserPaint:
                eraser.setStyle(style);
                break;
        }
    }

    public int getColor(){
        return drawPaint.getColor();
    }

    public float getStrokeWidth(int paint){
        switch (paint){
            case DrawTool.DrawPaint:
                return drawPaint.getStrokeWidth();

            case DrawTool.GrayPaint:
                return grayPaint.getStrokeWidth();

            case DrawTool.EraserPaint:
                return eraser.getStrokeWidth();

            default:
                return 0.0f;

        }
    }

    public void changeStokeWidth(int paint, int stokeWidth){
        switch (paint){
            case DrawTool.DrawPaint:
                drawPaint.setStrokeWidth(stokeWidth);
                break;
            case DrawTool.GrayPaint:
                grayPaint.setStrokeWidth(stokeWidth);
                break;
            case DrawTool.EraserPaint:
                eraser.setStrokeWidth(stokeWidth);
                break;
        }
    }

    public void changeColor(int paint, int color){
        switch (paint){
            case DrawTool.DrawPaint:
                drawPaint.setColor(color);
                break;
            case DrawTool.GrayPaint:
                grayPaint.setColor(color);
                break;
            case DrawTool.EraserPaint:
                eraser.setColor(color);
                break;
        }
    }

    public Paint getEraser() {
        return eraser;
    }

    public void setEraserPaint(Paint eraserPaint) {
        this.eraser = eraserPaint;
    }

    public Paint getDrawPaint() {
        return drawPaint;
    }

    public void setDrawPaint(Paint drawPaint) {
        this.drawPaint = drawPaint;
    }

    public Paint getGrayPaint() {
        return grayPaint;
    }

    public void setGrayPaint(Paint grayPaint) {
        this.grayPaint = grayPaint;
    }
}
