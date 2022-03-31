package com.example.testmlkit;

        import android.Manifest;
        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.content.ActivityNotFoundException;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Point;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.util.Log;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.Button;
        import android.widget.ImageView;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;

        import org.opencv.android.BaseLoaderCallback;
        import org.opencv.android.LoaderCallbackInterface;
        import org.opencv.android.OpenCVLoader;
        import org.opencv.android.Utils;
        import org.opencv.core.Core;
        import org.opencv.core.Mat;
        import org.opencv.core.Scalar;
        import org.opencv.imgproc.Imgproc;

public class GreenMasking extends Activity {
    private static final String TAG = "opencvtest";
    private static final int CameraIntent = 01;
    private ImageView cvimageview;
    private ImageView Croppedimageview;
    private Button ButtonTakeImage;
    private Bitmap Imagebitmap;
    private ImageView ResultImage;

    Mat unmaskedimageRGB;
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

        ButtonTakeImage = findViewById(R.id.takeimage);
        Croppedimageview = findViewById(R.id.croppedimage);
        cvimageview=findViewById(R.id.cvimage);
        ResultImage=findViewById(R.id.maskedImage);

        ButtonTakeImage.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, CameraIntent);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }
        });


        cvimageview = findViewById(R.id.cvimage);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraIntent && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Imagebitmap = (Bitmap) extras.get("data");
            cvimageview.setImageBitmap(Imagebitmap);
            greenMasking(Imagebitmap);


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

        // Matrix
        unmaskedimageRGB=new Mat();
        unmaskedimageHSV=new Mat();
        Mask=new Mat();
        Matresult =new Mat();

        //convert image to bitmap
        Utils.bitmapToMat(imagebitmap,unmaskedimageRGB);

        //convert matrix from rgb to hsv
        Imgproc.cvtColor(unmaskedimageRGB, unmaskedimageHSV, Imgproc.COLOR_RGB2HSV_FULL);


        Core.inRange(unmaskedimageHSV, new Scalar(40, 40, 40), new Scalar(70, 255, 255), Mask);

        Core.bitwise_and(unmaskedimageHSV, Matresult, Mask);

        Bitmap Bitimageresult=null;

        Utils.matToBitmap(Matresult,Bitimageresult);

        if(Bitimageresult!=null)
            ResultImage.setImageBitmap(Bitimageresult);




    }


}


