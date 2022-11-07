package com.example.inpaintingproject.Inpainting;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import com.example.inpaintingproject.Config.Config;
import com.example.inpaintingproject.Interface.Inpainting;
import com.example.inpaintingproject.Utils.utils;
import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InpaintModel implements Inpainting.Model {
    private Module module = null;
    private Bitmap mask = null;
    private Bitmap image = null;

    public InpaintModel(){
        try{
            module = LiteModuleLoader.load(utils.assetFilePath(InpaintView.getContext(), Inpainting.View.modelPath));
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Bitmap getMask() {
        return mask;
    }

    public void setMask(Bitmap mask) {
        this.mask = mask;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void save(){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        File myDir = new File(root + "/inpainted_image");
        myDir.mkdirs();
        Random generator = new Random();

        File file;
        do{
            int n = generator.nextInt(10000);
            String fname = "Image-"+n+".jpg";
            file = new File(myDir, fname);
        }while (file.exists());
        try{
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("Save ok",file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Bitmap runModule() {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, Config.InputWidth, Config.InputHeight, true);
        Bitmap resizedMask = Bitmap.createScaledBitmap(mask, Config.InputWidth, Config.InputHeight, true);
        Tensor imageTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedBitmap, Config.NO_MEAN_RGB, Config.NO_STD_RGB);
        Tensor maskTensor = TensorImageUtils.bitmapToFloat32Tensor(resizedMask, Config.NO_MEAN_RGB, Config.NO_STD_RGB);

        //reduce from shape 1 3 w h to 1 1 w g
        maskTensor = utils.reduceColor(maskTensor);

        IValue[] outputTuple = module.forward(IValue.from(imageTensor), IValue.from(maskTensor)).toTuple();
        Tensor output = outputTuple[1].toTensor();

        float[] outputFloat = output.getDataAsFloatArray();

        List<Float> floatList = new ArrayList<>();

        for (int i=0;i<outputFloat.length;i++){
            floatList.add(outputFloat[i]);
        }
        Bitmap outputBitmap = utils.arrayFloatToBitMap(floatList);
        image= utils.postProcess(resizedBitmap, outputBitmap, resizedMask);
        return image;
    }
}
