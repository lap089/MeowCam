package com.example.user.test_camera.MainSource;

import android.content.res.TypedArray;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by user on 8/20/2015.
 */
public class ListManager {
    public static String[] NameFeature = {"Blur","Filter","Brightness Contrast","OCR","Mirror"};
    public static ArrayList<Class> ClassFeature = new ArrayList<Class>()
    {{add(BlurActivity.class);
            add(FilterActivity.class);
            add(BrightnessContrastActivity.class);
        add(OcrActivity.class);
        add(MirrorActivity.class);}};

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


    public static String[] NameType = {"QRScaner","Image Editor","Setting"};

    public static String[] OcrLinks = {
            "",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIOG9kQW4wSWJSSjA",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIS056bHJlVFZIUHc",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIQV82TlpyUTJWVjQ",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIX0NKOTJXc0RYckk",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosITllYZ0cxQVhlLVU",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIQVJmbkg1UDFleHM",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIaEczNFBDOThHems",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIeTd5UXBZV21nXzA",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIMXJfWjlpZndxb3M",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIUlp0NDV2V19sdE0",
            "https://drive.google.com/uc?export=download&id=0B0SiGb1MZosIM0d4U3JwNmVsWGs"
    };

    public static String[] OcrLang = {"eng","vie","spa","spa_old","msa","kor","jpn","ita","ita_old","chi_tra","chi_sim","ind"};
    public static String[] OcrName = {"English","Vietnam","Spanish","Spanish (old)","Malaysia","Korea","Japan","Italy","Italy (old)","China (traditional)","China (simplified)","Indonesia"};
    public static String[] OcrNameFile = {"eng.traineddata", "vie.traineddata","spa.traineddata","spa_old.traineddata","msa.traineddata",
            "kor.traineddata","jpn.zip","ita.traineddata","ita_old.traineddata",
            "chi_tra.zip","chi_sim.zip","ind.traineddata"};
    public static long[] OcrSize = {21876572 ,5473919,2217822, 13274548 , 4120713,13309831, 13662324  , 2513459 , 8342637 , 24967182  , 17890797, 4621831};


}
