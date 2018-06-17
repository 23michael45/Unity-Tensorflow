package com.bh.utfplugin;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bh.utflibrary.ArbitraryStyleTransfer;
import com.bh.utflibrary.UtfLibraryInterface;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    ImageView mResultImageView;
    Button mTransferBtn;
    ArbitraryStyleTransfer mTransfer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResultImageView = (ImageView) findViewById(R.id.ResultView);


        Button mTransferBtn = (Button) findViewById(R.id.TransferBtn);
        mTransferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    //String contentPath = "lance_s.jpg";//"file:///android_asset/lance.jpg";
                    //String stylePath = "lion.jpg";//""file:///android_asset/cat.jpg";

                    /*
                    String[] files = getAssets().list("/");

                    for (String file:files)
                    {
                        Log.i(TAG, "List Assets: " +  file);
                        
                    }*/


                    String path = Environment.getExternalStorageDirectory().toString();
                    String contentPath = path+"/lance_s.jpg";
                    String stylePath = path+"/cat.jpg";

                    String outPath = path + "/outimage.jpg";

                    Bitmap bitmap = UtfLibraryInterface.TransferFromFile(contentPath,stylePath,outPath);

                    mResultImageView.setImageBitmap((bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        try {
            UtfLibraryInterface.Init(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
