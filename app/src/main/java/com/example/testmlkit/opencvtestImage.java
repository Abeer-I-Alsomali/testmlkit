package com.example.testmlkit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class opencvtestImage extends Activity {
    private static final String  TAG              = "opencvtest";
    private static final int  CameraIntent              = 01;

    private boolean              mIsColorSelected = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;
    private ImageView cvimageview;
    private Button ButtonTakeImage;
    private Bitmap Imagebitmap;
    private ActivityResultLauncher<Intent> ImageIntentResult;
    Boolean Flag=false;

    private CameraBridgeViewBase mOpenCvCameraView;


    public opencvtestImage() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_opencvtest_image);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 01);

        ButtonTakeImage = findViewById(R.id.takeimage);

        ButtonTakeImage.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePictureIntent, CameraIntent);
            } catch (ActivityNotFoundException e) {
                // display error state to the user
            }
        });
        cvimageview = findViewById(R.id.cvimage);

     ///   cvimageview.setOnTouchListener(this);

    }

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //cvimageview.setOnTouchListener(opencvtestImage.this::onTouch);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraIntent && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Imagebitmap = (Bitmap) extras.get("data");
            Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.g1,null);
           // Imagebitmap  = ((BitmapDrawable) drawable).getBitmap();
            cvimageview.setImageBitmap(Imagebitmap);

            Flag=true;
      //      onImageViewStarted();

        }
    }



//
//
//    public void onImageViewStarted() {
//       // mRgba = new Mat();
//        mDetector = new ColorBlobDetector();
//        mSpectrum = new Mat();
//        mBlobColorRgba = new Scalar(255);
//        mBlobColorHsv = new Scalar(255);
//        SPECTRUM_SIZE = new Size(200, 64);
//        CONTOUR_COLOR = new Scalar(255,0,0,255);
//    }
//
//
//
//
//    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
//        Mat pointMatRgba = new Mat();
//        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
//        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);
//        return new Scalar(pointMatRgba.get(0, 0));
//    }
//
//
//
//    public String classify(@NotNull Scalar c)
//    {
//
//        double hue = c.val[0];
//        double sat = c.val[1];
//        double lgt = c.val[2];
//
//        Log.i("color0",hue+"");
//        Log.i("color1",sat+"");
//        Log.i("color2",lgt+"");
//
//        //   if (lgt < 0.2)  return "Blacks";
//        //     if (lgt > 0.8)  return "Whites";
/////
//        ///if (sat < 0.25) return "Grays";
//
//        ///     if (hue < 30)   return "Reds";
//
//        if (sat> 25 && lgt > 25){
//        if (hue >49 && hue < 125)  return "Greens";
//        }
//        if (hue < 210)  return "Cyans";
//        if (hue < 270)  return "Blues";
//        if (hue < 330)  return "Magentas";
//        return "Reds";
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        mRgba=new Mat();
//        Imagebitmap=((BitmapDrawable)cvimageview.getDrawable()).getBitmap();
//        Utils.bitmapToMat(Imagebitmap,mRgba);
//        // mRgba = inputFrame.rgba();
//
//        Log.i("cameraframe","here");
//
//        if(Flag){
//            if (mIsColorSelected) {
//                mDetector.process(mRgba);
//                List<MatOfPoint> contours = mDetector.getContours();
//                Log.e(TAG, "Contours count: " + contours.size());
//                Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);
//
//                Mat colorLabel = mRgba.submat(4, 68, 4, 68);
//                colorLabel.setTo(mBlobColorRgba);
//
//                Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
//                mSpectrum.copyTo(spectrumLabel);
//            }
//
//            int cols = mRgba.cols();
//            int rows = mRgba.rows();
//
//
//
//            int xOffset = (cvimageview.getWidth() - cols) / 2;
//            int yOffset = (cvimageview.getHeight() - rows) / 2;
//
//            int x = (int)event.getX() - xOffset;
//            int y = (int)event.getY() - yOffset;
//            Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");
//
//            if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;
//
//            Rect touchedRect = new Rect();
//
//            touchedRect.x = (x>4) ? x-4 : 0;
//            touchedRect.y = (y>4) ? y-4 : 0;
//
//            touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;
//            touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;
//
//            Mat touchedRegionRgba = mRgba.submat(touchedRect);
//
//            Mat touchedRegionHsv = new Mat();
//            Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);
//
//            // Calculate average color of touched region
//            mBlobColorHsv = Core.sumElems(touchedRegionHsv);
//            int pointCount = touchedRect.width*touchedRect.height;
//            for (int i = 0; i < mBlobColorHsv.val.length; i++)
//                mBlobColorHsv.val[i] /= pointCount;
//
//            mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
//
//            Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
//                    ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");
//
//            mDetector.setHsvColor(mBlobColorHsv);
//
//            Log.i("color",classify(mBlobColorHsv));
//            Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE, 0, 0, Imgproc.INTER_LINEAR_EXACT);
//            mIsColorSelected = true;
//
//            touchedRegionRgba.release();
//            touchedRegionHsv.release();
//
//        }
//        return false; // don't need subsequent touch events
//    }
}