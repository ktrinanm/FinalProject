package edu.suu.ktrinanm.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

public class LoadingOCR extends AppCompatActivity
{
    private Bitmap bmap;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingocr);

        Intent i = getIntent();
        String path = i.getStringExtra("imagepath");
        bmap = getThumbnail(path);
    }

    private Bitmap getThumbnail(String path)
    {
        Bitmap thumbnail = null;

        // If no file on external storage, look in internal storage
        if (thumbnail == null) {
            try {
                File filePath = getFileStreamPath(path);
                FileInputStream fi = new FileInputStream(filePath);
                thumbnail = BitmapFactory.decodeStream(fi);
            } catch (Exception ex) {
                Log.e("getThumb()", ex.getMessage());
            }
        }
        return thumbnail;
    }
}
