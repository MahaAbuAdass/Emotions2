package com.example.emotions2


import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class FaceRecognitionHelper(private val interpreter: Interpreter) {

    fun recognizeFace(bitmap: Bitmap): FloatArray {
        // Convert Bitmap to TensorImage or TensorBuffer
        val tensorImage = TensorImage.fromBitmap(bitmap)
        val tensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 128), DataType.FLOAT32)

        interpreter.run(tensorImage.buffer, tensorBuffer.buffer.rewind())

        return tensorBuffer.floatArray
    }
}
