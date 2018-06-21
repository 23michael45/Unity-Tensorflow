package com.bh.utflibrary;

import android.app.Activity;
import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;

public class UtfLibraryInterface {

    static String TAG = "UtfLibraryInterface";
    private static ArbitraryStyleTransfer mTransfer;
    public static AssetManager mAssetManager;


    private static int[] outputs;
    public static void Init(Activity currentActivity) throws IOException {
        mTransfer = ArbitraryStyleTransfer.create(currentActivity.getAssets());
    }

    public static void Deinit() {
        mTransfer.close();

    }

    public static Bitmap TransferFromFile(final String contentPath, final String stylePath, final String outPath) throws IOException {
        try {
            Bitmap bitmap = mTransfer.transferfromfile(contentPath, stylePath, outPath);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void Transfer(final int contentWidth, final int contentHeight, final float[] contentData, final int styleWidth, final int sytleHeight, final float[] styleData) throws IOException {
        try {
            Log.i(TAG, "contentWidth : " + contentWidth + ":"+contentHeight + " contentData Len" + contentData.length + ": styleData Len" + styleData.length);
            outputs = mTransfer.transfer(contentWidth, contentHeight, contentData, styleWidth, sytleHeight, styleData);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
    public static int[] GetOutputs()
    {
        if(outputs != null)
        {
            Log.i(TAG, "GetOutputs : " + outputs.length);
            return outputs;

        }
        return null;
    }
    public static void TryArrayInput(final float[] input) {
        Log.i(TAG, "TryArrayInput : " + input.length);

    }

    public static int[] TryArrayOutput() {

        int[] o = new int[]{1,2,4,8};
        Log.i(TAG, "TryArrayOutput : " + o.length);

        return o;
    }
}
