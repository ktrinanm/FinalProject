package edu.suu.ktrinanm.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadingOCR extends AppCompatActivity
{
    private String textFromImage;
    private TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingocr);

        Intent i = getIntent();
        String path = i.getStringExtra("pathname");
        try
        {
            ByteString[]  imgBytes = new ByteString[]{ByteString.readFrom(new FileInputStream(path))};
            AsyncTask bg = new Background().execute(imgBytes);

            t = (TextView) findViewById(R.id.ocrText);
            /*while(bg.getStatus() != AsyncTask.Status.FINISHED)
            {/*Do nothing}*/

        }
        catch(Exception e)
        {
            System.out.println("Katrina: " + e.getMessage());
        }
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

    private class Background extends AsyncTask<ByteString, Void, String>
    {
        protected String doInBackground(ByteString ... byteStringsArr)
        {
            try {

                List<AnnotateImageRequest> requests = new ArrayList<>();
                ByteString imgBytes = byteStringsArr[0];

                Image img = Image.newBuilder().setContent(imgBytes).build();
                Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
                AnnotateImageRequest request =
                        AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
                requests.add(request);

                BatchAnnotateImagesResponse response =
                        ImageAnnotatorClient.create().batchAnnotateImages(requests);
                List<AnnotateImageResponse> responses = response.getResponsesList();

                String text = "";

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        System.out.printf("Error: %s\n", res.getError().getMessage());
                        return null;
                    }

                    // For full list of available annotations, see http://g.co/cloud/vision/docs
                    for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
                        text += annotation.getDescription();
                        System.out.printf("Text: %s\n", text);
                        System.out.printf("Position : %s\n", annotation.getBoundingPoly());
                    }
                }
                return text;
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String text)
        {
            if(text.equals(null) || text.equals(""))
            {
                Log.d("ERROR", "SOMETHING BAD HAPPENED IN MAKING THE TEXT STRING!!!!!!!!!!!!!!");
            }
            textFromImage = text;
            t.setText(textFromImage);
        }
    }
}
