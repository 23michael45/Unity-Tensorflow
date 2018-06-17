package com.bh.utflibrary;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;

public class UtfLibraryInterface {

    static  String TAG = "UtfLibraryInterface";
    private static ArbitraryStyleTransfer mTransfer;
    public  static AssetManager mAssetManager;
    public static void Init(Activity currentActivity) throws IOException {
        mTransfer= ArbitraryStyleTransfer.create(currentActivity.getAssets());
    }
    public  static  void Deinit()
    {
        mTransfer.close();

    }

    public  static Bitmap TransferFromFile(final String contentPath,final String stylePath,final String outPath) throws IOException {
        try {
            Bitmap bitmap = mTransfer.transferfromfile(contentPath, stylePath, outPath);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int[] Transfer(final  int contentWidth,final  int contentHeight,final float[] contentData,final int styleWidth,final int sytleHeight,final float[] styleData) throws IOException {
        try {
            Log.i(TAG, "contentWidth : " + contentWidth + " contentData Len" + contentData.length);
            return mTransfer.transfer(contentWidth, contentHeight, contentData,styleWidth,sytleHeight,styleData);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
