package com.example.testmlkit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GreenMasking extends Activity {
    private static final String TAG = "opencvtest";
    private static final int CameraIntent = 01;
    private ImageView cvimageview;
    private ImageView Croppedimageview;
    private Button ButtonTakeImage;
    private Bitmap Imagebitmap;
    private ImageView ResultImage;

    Mat unmaskedimageRGB;
    Mat unmaskedimageBGR;
    Mat unmaskedimageHSV;
    Mat Mask;
    Mat Matresult ;
    public GreenMasking() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_green_masking);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
              ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 01);

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 02);


        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 02);


        ButtonTakeImage = findViewById(R.id.takeimage);
        Croppedimageview = findViewById(R.id.maskedImage);
        cvimageview=findViewById(R.id.cvimage);
        ResultImage=findViewById(R.id.maskedImage);


        ButtonTakeImage.setOnClickListener(v -> {

            Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.g2,null);
            Bitmap imagebitmap  = ((BitmapDrawable) drawable).getBitmap();

            greenMasking(imagebitmap);

//            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            try {
//                startActivityForResult(takePictureIntent, CameraIntent);
//            } catch (ActivityNotFoundException e) {
//                // display error state to the user
//            }
        });


        cvimageview = findViewById(R.id.cvimage);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraIntent && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Imagebitmap = (Bitmap) extras.get("data");
            cvimageview.setImageBitmap(Imagebitmap);
            Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.g1,null);
            Bitmap imagebitmap  = ((BitmapDrawable) drawable).getBitmap();
         greenMasking(imagebitmap);


        //    greenMasking(Imagebitmap);


        }
    }


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                     unmaskedimageRGB=new Mat();
                      unmaskedimageBGR=new Mat();
                     unmaskedimageHSV=new Mat();
                     Mask=new Mat();
                     Matresult =new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void greenMasking(Bitmap imagebitmap) {


       Bitmap resultimage=imagebitmap;


        // Matrix
        unmaskedimageRGB=new Mat();
        unmaskedimageBGR=new Mat();
        unmaskedimageHSV=new Mat();
        Mask=new Mat();
        Matresult =new Mat();



        unmaskedimageBGR = Imgcodecs.imread(getResources().getDrawable(R.drawable.g2).toString());

        imagesave(imagebitmap);

        //convert image to bitmap
        Utils.bitmapToMat(imagebitmap,unmaskedimageBGR);

        //convert matrix from bgr to rgb
        Imgproc.cvtColor(unmaskedimageBGR,unmaskedimageRGB,Imgproc.COLOR_BGR2RGB);


        //convert matrix from rgb to hsv
        Imgproc.cvtColor(unmaskedimageRGB, unmaskedimageHSV, Imgproc.COLOR_RGB2HSV_FULL);

        Core.inRange(unmaskedimageHSV,new Scalar(40, 40, 40),new Scalar(70, 255, 255), Mask);

        Core.bitwise_and(unmaskedimageRGB,unmaskedimageRGB,Matresult,Mask);


        Utils.matToBitmap(Matresult,resultimage);

        if(resultimage!=null)
            ResultImage.setImageBitmap(resultimage);


        Imgproc.cvtColor(Matresult, Matresult,Imgproc.COLOR_BGR2GRAY);

        int size= (int) Matresult.total();

        int precentage =(Core.countNonZero(Matresult)*100)/size;

        Log.i("Matsize", String.valueOf(Core.countNonZero(Matresult)));
        Log.i("Matsize", String.valueOf(size));







    }


    public void imagesave(Bitmap bitmap){

        FileOutputStream fos= null;
        File file = getDisc();
        if(!file.exists() && !file.mkdirs()) {
            //Toast.makeText(this, "Can't create directory to store image", Toast.LENGTH_LONG).show();
            //return;
            Log.i("file","file not created");
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "FileName"+date+".jpg";
        String file_name = file.getAbsolutePath()+"/"+name;
        File new_file = new File(file_name);
        Log.i("file","new_file created");
        try {
            fos= new FileOutputStream(new_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Toast.makeText(this, "Save success", Toast.LENGTH_LONG).show();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.i("file","FNF");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void refreshGallery(File file){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);
    }

    private File getDisc(){
        String t= getCurrentDateAndTime();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(file, "ImageDemo");
    }

    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;

    }







}


