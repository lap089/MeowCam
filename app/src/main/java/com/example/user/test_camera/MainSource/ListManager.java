package com.example.user.test_camera.MainSource;

import android.content.res.TypedArray;
import android.os.Environment;

import com.example.user.test_camera.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 8/20/2015.
 */
public class ListManager {
    public static String[] NameFeature = {"Blur","Filter","Contrast Bright","OCR","Mirror"};
    public static ArrayList<Class> ClassFeature = new ArrayList<Class>()
    {{add(BlurActivity.class);
            add(FilterActivity.class);
            add(BrightnessContrastActivity.class);
        add(OcrActivity.class);
        add(MinorActivity.class);}};

    public static String[] ImageFeature = {"","","","",""};
    public static TypedArray  imageFeatureID = null;

    static File Camera = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
    static File Picture = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    static File Download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    public static ArrayList<File> Dirs = new ArrayList<File>() {{
        add(Camera);
        add(Picture);
        add(Download);
    }};
    public static String[] NameDirs = {"Camera","Picture","Download"};


    public static String[] NameType = {"QRScaner","Editor Image","SettingActivity"};

    public static String[] OcrLinks = {"https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIOG9kQW4wSWJSSjA"
                                        };
            public static String[] OcrLang = {"vie"};
    public static String[] OcrName = {"Vietnam"};
    public static String[] OcrNameFile = {"vie.traineddata"};

}
