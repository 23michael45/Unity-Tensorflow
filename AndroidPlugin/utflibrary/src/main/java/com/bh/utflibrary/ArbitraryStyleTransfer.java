package com.bh.utflibrary;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.os.TraceCompat;
import android.util.Log;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by amitshekhar on 06/03/17.
 */

/**
 * A classifier specialized to label images using TensorFlow.
 */
public class ArbitraryStyleTransfer{

    private static final String TAG = "ImageClassifier";

    private static final String MODEL_FILE = "file:///android_asset/arbitrary-style-transfer-graph.pb";
    // Config values.
    private static String inputContentName = "content";
    private static String inputStyleName = "style";
    private static String outputName = "output_image";

    private float[] outputs;
    private String[] outputNames;

    private static int outWidth;
    private static int outHeight;
    private TensorFlowInferenceInterface inferenceInterface;

    private boolean runStats = false;
    private  static AssetManager mAssetManager;
    private ArbitraryStyleTransfer() {
    }

    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * @param assetManager  The asset manager to be used to load assets.
     * @throws IOException
     */
    public static ArbitraryStyleTransfer create(
            AssetManager assetManager)
            throws IOException {
        mAssetManager = assetManager;
        ArbitraryStyleTransfer c = new ArbitraryStyleTransfer();
        c.inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
        // The shape of the output is [N, NUM_CLASSES], where N is the batch size.
        Operation outOp = c.inferenceInterface.graph().operation(outputName);
        outWidth =
                (int) outOp.output(0).shape().size(1);
        outHeight =
                (int) outOp.output(0).shape().size(2);

        // Ideally, inputSize could have been retrieved from the shape of the input operation.  Alas,
        // the placeholder node for input in the graphdef typically used does not specify a shape, so it
        // must be passed in as a parameter.

        // Pre-allocate buffers.
        c.outputNames = new String[]{outputName};
        //c.intValues = new int[c.inputSize * c.inputSize];
        //c.floatValues = new float[c.inputSize * c.inputSize * 3];

        return c;
    }
    private  float[] ConvertBitmap2FloatArray(Bitmap bitmap)
    {
        int[] intArr = new int[bitmap.getWidth()* bitmap.getHeight()];
        float[] floatArr = new float[bitmap.getWidth()* bitmap.getHeight() * 3];
        bitmap.getPixels(intArr, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < intArr.length; ++i) {
            final int val = intArr[i];
            floatArr[i * 3 + 0] = ((val >> 16) & 0xFF);
            floatArr[i * 3 + 1] = ((val >> 8) & 0xFF);
            floatArr[i * 3 + 2] = (val & 0xFF) ;
        }
        return floatArr;
    }
    private  Bitmap LoadBitmap(String filefullpath) throws IOException {


        Log.i(TAG, "load bitmap: " + filefullpath);
        InputStream fIn = new FileInputStream(new File(filefullpath));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeStream(fIn);

        //InputStream is = mAssetManager.open(path);
        //Bitmap bitmap= BitmapFactory.decodeStream(is);
        return bitmap;
    }


    public Bitmap transfer(final String contentPath,final String stylePath,final String outPath) throws IOException {

        Bitmap contentBitmap = LoadBitmap(contentPath);
        Bitmap styleBitmap = LoadBitmap(stylePath);

        //SaveBitmap(contentBitmap,"content");
        //SaveBitmap(styleBitmap,"style");

        TraceCompat.beginSection("preprocessBitmap");

        float[] contentFloat = ConvertBitmap2FloatArray(contentBitmap);
        float[] styleFloat = ConvertBitmap2FloatArray(styleBitmap);
        inferenceInterface.feed(inputContentName,contentFloat , new long[]{1, contentBitmap.getHeight(), contentBitmap.getWidth(), 3});
        inferenceInterface.feed(inputStyleName, styleFloat, new long[]{1, styleBitmap.getHeight(), styleBitmap.getWidth(), 3});


        TraceCompat.endSection();


        outputs = new float[contentBitmap.getWidth() * contentBitmap.getHeight()*3];
        inferenceInterface.run(outputNames, runStats);
        inferenceInterface.fetch(outputName, outputs);



        int[] outInt = new int[outputs.length/3];
        for (int i = 0; i < outputs.length; i += 3) {
            final int val1 = (int)outputs[i];
            final int val2 = (int)outputs[i + 1];
            final int val3 = (int)outputs[i + 2];
            int rgb =(val1<< 16 )+ (val2 << 8) + val3;

            rgb =(255<< 24) + (val1<< 16 )+ (val2 << 8) + val3;
            outInt[i/3] = rgb;
        }
        Bitmap outBitmap = Bitmap.createBitmap(contentBitmap.getWidth(), contentBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        outBitmap.setPixels(outInt,0,contentBitmap.getWidth(),0,0,contentBitmap.getWidth(), contentBitmap.getHeight());
        SaveBitmap(outBitmap,outPath);

        return outBitmap;
    }

    void SaveBitmap(Bitmap bitmap,String filefullpath) throws IOException {
        Log.i(TAG, "save bitmap: " + filefullpath);
        File file = new File(filefullpath); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        FileOutputStream fOut = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        fOut.flush(); // Not really required
        fOut.close(); // do not forget to close the stream
    }
    public void close() {
        inferenceInterface.close();
    }
}
