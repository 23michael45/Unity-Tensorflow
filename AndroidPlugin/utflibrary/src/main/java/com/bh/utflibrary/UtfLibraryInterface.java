package com.bh.utflibrary;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import java.io.IOException;

public class UtfLibraryInterface {

    private static ArbitraryStyleTransfer mTransfer;
    public  static AssetManager mAssetManager;
    public static void Init(Activity currentActivity) throws IOException {
        mTransfer= ArbitraryStyleTransfer.create(currentActivity.getAssets());
    }
    public  static  void Deinit()
    {
        mTransfer.close();

    }

    public  static Bitmap Transfer(final String contentPath,final String stylePath,final String outPath) throws IOException {
        try {
            Bitmap bitmap = mTransfer.transfer(contentPath, stylePath, outPath);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
