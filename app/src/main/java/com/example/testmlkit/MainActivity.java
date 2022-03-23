package com.example.testmlkit;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class MainActivity extends AppCompatActivity {


    Button captureid;
    TextView Referenceid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureid = findViewById(R.id.captureid);
        Referenceid = findViewById(R.id.Referenceid);
        ActivityResultLauncher<Intent> TextRecognizer = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data1 = result.getData();
                        try {
                            assert data1 != null;
                            Bitmap bitmap = (Bitmap) data1.getExtras().get("data");
                            com.google.android.gms.vision.text.TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
                            Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();
                            SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                            String text = "";

                            for (int i = 0; i < textBlocks.size(); i++) {
                                TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                                // return string
                                text = textBlock.getValue();
                            }
                            Referenceid.setText(text);

                        } catch (Exception e) {

                        }
                    }
                });



        captureid.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            TextRecognizer.launch(intent);
        });

    }


}