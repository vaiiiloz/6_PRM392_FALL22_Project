package com.example.inpaintingproject.Interface;

public interface Main {
    interface View{
        static final int CAMERA_CODE = 0;
        static final int LIBRARY_CODE = 1;
        void onCameraClick();
        void onLibraryClick();

    }

    interface Presenter{
        void cameraHandle();
        void libraryHandle();
        void setView(View view);
    }

    interface Model{

    }

}
