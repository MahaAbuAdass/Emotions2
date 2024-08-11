package com.example.emotions2

import android.graphics.Bitmap
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.common.InputImage

class FaceDetectionHelper {

    private val faceDetector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
    )

    suspend fun detectFaces(bitmap: Bitmap): List<Face>? {
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val faces = faceDetector.process(image).await()
            faces
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
