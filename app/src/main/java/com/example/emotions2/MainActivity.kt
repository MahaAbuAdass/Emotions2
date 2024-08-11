package com.example.emotions2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : ComponentActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var faceDetectionHelper: FaceDetectionHelper
    private lateinit var faceRecognitionHelper: FaceRecognitionHelper
    private lateinit var openCVHelper: OpenCVHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Initialize OpenCV
        if (!OpenCVLoader.initDebug()) {
            throw RuntimeException("Failed to initialize OpenCV")
        }

        previewView = findViewById(R.id.preview_view)
        faceDetectionHelper = FaceDetectionHelper()
        openCVHelper = OpenCVHelper(this)

        val model = Interpreter(loadModelFile())
        faceRecognitionHelper = FaceRecognitionHelper(model)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        val photoFile = createFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d("CameraX", "Photo saved successfully")
                    processImage(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraX", "Error capturing photo", exception)
                }
            }
        )
    }

    private fun processImage(file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            val faces = faceDetectionHelper.detectFaces(bitmap)

            faces?.forEach { face ->
                val rect = face.boundingBox
                val faceBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
                val recognitionResult = faceRecognitionHelper.recognizeFace(faceBitmap)
                Log.d("FaceRecognition", "Recognition result: $recognitionResult")
            }

            withContext(Dispatchers.Main) {
                // Update UI with recognition result
            }
        }
    }

    private fun createFile(): File {
        val timestamp = System.currentTimeMillis()
        val fileName = "IMG_$timestamp.jpg"
        val storageDir = getExternalFilesDir(null)
        return File(storageDir, fileName)
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = assets.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
