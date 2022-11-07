package com.example.inpaintingproject.Entity;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.inpaintingproject.Config.Config;
import com.example.inpaintingproject.R;
import com.example.inpaintingproject.Tool.PaintTool;

import top.defaults.colorpicker.ColorPickerPopup;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private SeekBar penSizeBar;
    private SeekBar eraseSizeBar;
    private TextView penSizeText;
    private TextView eraseSizeText;
    private Button pickColorButton;
    private Button setColorButton;
    private View colorView;
    private PaintTool paintTool;
    private int currentColor;
    private static final int maxStroke = 50;

    public SettingFragment(PaintTool paintTool){
        this.paintTool = paintTool;

    }
    public void setupPaintTool() {
        currentColor = paintTool.getColor();

        int penSize = (int) paintTool.getStrokeWidth(1);
        penSizeBar.setProgress(penSize);
        penSizeText.setText(penSize+"dp");

        int eraseSize = (int) paintTool.getStrokeWidth(3);
        eraseSizeBar.setProgress(eraseSize);
        eraseSizeText.setText(eraseSize+"dp");

        colorView.setBackgroundColor(paintTool.getColor());
    }

    public SettingFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    public SettingFragment() {
        super(R.layout.setting_dialog);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        super.onCreateView(inflater, container, savedInstanceState);
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.setting_dialog, container, false);
        penSizeBar = view.findViewById(R.id.penSizeBar);
        eraseSizeBar = view.findViewById(R.id.eraserSizeBar);
        pickColorButton = view.findViewById(R.id.pickColor);
        setColorButton = view.findViewById(R.id.setColor);
        colorView = view.findViewById(R.id.preview_selected_color);
        penSizeText = view.findViewById(R.id.pensizeText);
        eraseSizeText = view.findViewById(R.id.eraserSizeText);
        view.getLayoutParams().width = Config.SceenWidth;
        view.getLayoutParams().height = Config.SceenHeight;
        registerListener();
        setupPaintTool();
        return view;


    }


    private void registerListener(){
        pickColorButton.setOnClickListener(this);
        setColorButton.setOnClickListener(this);
        penSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                penSizeText.setText(progress+"dp");
                paintTool.changeStokeWidth(1, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        eraseSizeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                eraseSizeText.setText(progress+"dp");
                paintTool.changeStokeWidth(3, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pickColor:
                // create color picker popup
                new ColorPickerPopup.Builder(getContext()).initialColor(paintTool.getColor())
                        .enableBrightness(true)
                        .enableAlpha(true)
                        .okTitle("Choose")
                        .cancelTitle("Cancel")
                        .showIndicator(true)
                        .showValue(true).build().show(v,
                                new ColorPickerPopup.ColorPickerObserver() {
                                    @Override
                                    public void onColorPicked(int color) {
                                        colorView.setBackgroundColor(color);
                                        currentColor = color;
                                    }
                                });
                break;
            case R.id.setColor:
                paintTool.changeColor(1, currentColor);
                break;
            default:
                break;
        }

    }
}
