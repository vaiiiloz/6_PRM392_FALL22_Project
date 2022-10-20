package com.example.inpaintingproject.Utils;

import android.content.Context;
import android.graphics.*;
import com.example.inpaintingproject.Config.Config;
import org.pytorch.Tensor;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class utils {
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);

        if (file.exists() && file.length()>0){
            return file.getAbsolutePath();
        }

        try (InputStream is=context.getAssets().open(assetName)){
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
        }
        return file.getAbsolutePath();

    }

    public static Tensor reduceColor(Tensor originTensor) {

        long[] shape = originTensor.shape();
        float[] flatten = originTensor.getDataAsFloatArray();
        long batch = shape[0];
        long d = shape[1];
        long height = shape[2];
        long width = shape[3];

        float[] newFlatten = new float[(int) (batch * 1 * height * width)];

        for (int x = 0; x < height * width; x++) {
            newFlatten[x] = flatten[x];
        }
        long[] newShape = new long[]{1, 1, height, width};
        Tensor newTensor = Tensor.fromBlob(newFlatten, newShape);
        return newTensor;
    }

    public static Bitmap arrayFloatToBitMap(List<Float> floatList) {
        int width = Config.InputWidth;
        int height = Config.InputHeight;
        byte alpha = (byte) 255;
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int length = width * height;

        float maximum = Collections.max(floatList);
        float minimum = Collections.min(floatList);
        float delta = maximum - minimum;


        for (int i=0;i<length;i++){
            int r = (int) ((((floatList.get(i) - minimum) / delta) * 255));
            int g = (int) ((((floatList.get(i+length) - minimum) / delta) * 255));
            int b = (int) ((((floatList.get(i+length*2) - minimum) / delta) * 255));

            int x = i / width;
            int y = i % width;
            bmp.setPixel(y, x, Color.rgb(r,g,b));
        }
        return bmp;
    }

    public static Bitmap postProcess(Bitmap originalImage, Bitmap outputImage, Bitmap mask) {
        float[] originalFlatten = flattenBitMap(originalImage);
        float[] outputFlatten = flattenBitMap(outputImage);
        float[] maskFlatten = flattenBitMap(mask);
        float[] reverseMaskFlatten = reverse(maskFlatten);

        List<Float> newBitmapFlatten = new ArrayList<>();

        for (int i=0;i<originalFlatten.length;i++){
            newBitmapFlatten.add(originalFlatten[i]*reverseMaskFlatten[i] + outputFlatten[i]*maskFlatten[i]);
        }

        return arrayFloatToBitMap(newBitmapFlatten);
    }

    public static Bitmap drawMask(Bitmap originalImage, Bitmap maskImage) {
        Bitmap original = originalImage.copy(originalImage.getConfig(), true);
        Bitmap mask = maskImage.copy(maskImage.getConfig(), true);

        //You can change original image here and draw anything you want to be masked on it.

        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),  Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setColor(Color.BLACK);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        tempCanvas.drawBitmap(original, 0, 0, null);
        tempCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);
        return result;
    }

    private static float[] flattenBitMap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int length = bitmap.getHeight() * bitmap.getWidth();
        float[] array = new float[length*3];
        for (int y=0;y<height;y++){
            for (int x=0;x<width;x++){
                int index = y*width+x;
                Color color = bitmap.getColor(x,y);
                array[index] = color.red();
                array[index+length] = color.green();
                array[index+length*2] = color.blue();
            }
        }
        return array;
    }


    public static float[] reverse(float[] origin) {
        float[] newArray = new float[origin.length];
        for (int i = 0; i < origin.length; i++) {
            newArray[i] = 1 - origin[i];
        }
        return newArray;
    }

    public static Bitmap convertMask(Bitmap mask){
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(),  Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(result);
        tempCanvas.drawBitmap(mask, 0, 0, null);
        return result;
    }
}
