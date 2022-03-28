package com.example.testmlkit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ReadingImages {

        public static void main(String args[]) {
            //Loading the OpenCV core library
            System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

            //Instantiating the Imagecodecs class
            Imgcodecs imageCodecs = new Imgcodecs();

            //Reading the Image from the file
            String file ="C:/EXAMPLES/OpenCV/sample.jpg";
            Mat matrix = imageCodecs.imread(file);

            System.out.println("Image Loaded");
        }
    }





