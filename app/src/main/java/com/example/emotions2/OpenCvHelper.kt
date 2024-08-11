package com.example.emotions2

import android.content.Context
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

class OpenCVHelper(context: Context) {

    init {
        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            throw RuntimeException("Failed to initialize OpenCV")
        }
    }

    fun loadImage(imagePath: String): Mat {
        val image = Imgcodecs.imread(imagePath)
        if (image.empty()) {
            throw RuntimeException("Failed to load image from path: $imagePath")
        }
        return image
    }

    fun saveImage(image: Mat, outputPath: String) {
        if (!Imgcodecs.imwrite(outputPath, image)) {
            throw RuntimeException("Failed to save image to path: $outputPath")
        }
    }

    fun processImage(image: Mat): Mat {
        val grayImage = Mat()
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY)
        return grayImage
    }

    fun resizeImage(image: Mat, width: Int, height: Int): Mat {
        val resizedImage = Mat()
        Imgproc.resize(image, resizedImage, org.opencv.core.Size(width.toDouble(), height.toDouble()))
        return resizedImage
    }

    fun rotateImage(image: Mat, angle: Double): Mat {
        val rotatedImage = Mat()
        val center = org.opencv.core.Point(image.cols() / 2.0, image.rows() / 2.0)
        val rotationMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.0)
        Imgproc.warpAffine(image, rotatedImage, rotationMatrix, image.size())
        return rotatedImage
    }
}
