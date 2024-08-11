package com.example.emotions2


import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result!!)
        } else {
            continuation.resumeWithException(task.exception ?: Exception("Unknown error"))
        }
    }
}
