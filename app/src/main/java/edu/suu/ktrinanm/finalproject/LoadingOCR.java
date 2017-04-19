package edu.suu.ktrinanm.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class LoadingOCR extends AppCompatActivity
{
    private Bitmap bmap;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingocr);


        Intent i = getIntent();
        byte [] data = i.getByteArrayExtra("imgBytes");
        ByteString imgBytes = ByteString.copyFrom(data);
        try {
            runVision(imgBytes);
        }
        catch(Exception e) {
            System.out.println("Something went wrong.");
        }
    }

    private void runVision(ByteString imgBytes) throws Exception
    {

        ImageAnnotatorClient vision = ImageAnnotatorClient.create();

        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();
        requests.add(request);

        BatchAnnotateImagesResponse response = ImageAnnotatorClient.create().batchAnnotateImages(requests);//vision.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.printf("Error: %s\n", res.getError().getMessage());
                return;
            }

            for(EntityAnnotation annotation : res.getTextAnnotationsList())
            {
                System.out.printf("Text: %s\n", annotation.getDescription());
                System.out.printf("Position: %s\n", annotation.getBoundingPoly());
            }
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
}
