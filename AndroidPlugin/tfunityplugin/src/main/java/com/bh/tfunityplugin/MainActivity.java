package com.bh.tfunityplugin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.bh.utflibrary.UtfLibraryInterface;
import com.unity3d.player.UnityPlayerActivity;

import java.io.IOException;

public class MainActivity extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            UtfLibraryInterface.Init(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 显示Toast消息
    public void ShowToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // 返回一个字符串（静态方法）
    public static String GetInformation()
    {
        return "This is a Plugin's content!";
    }
}
