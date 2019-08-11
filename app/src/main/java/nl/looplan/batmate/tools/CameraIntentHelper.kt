package nl.looplan.batmate.tools

import android.app.Activity
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.looplan.batmate.ui.scanandrecord.fragments.CameraFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CameraIntentHelper {

    const val CAMERA_INTENT_REQUEST_CODE = 222

    fun createTempFile(activity: Activity): File {
        activity.run {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

            return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
        }
    }

    suspend fun createFileAndStartCamera(activity: Activity) : File {
        Log.i(CameraFragment.TAG, "startCameraApp: Starting camera app")

        // Create cache file.
        val file = withContext(Dispatchers.IO) {
            // Return the temp file.
            createTempFile(activity)
        }

        startIntent(activity, file)

        return file
    }

    fun startIntent(activity: Activity, file: File) {
        activity.run {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {

                    val uri = FileProvider.getUriForFile(applicationContext, "com.example.android.fileprovider", file)

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(takePictureIntent, CAMERA_INTENT_REQUEST_CODE)
                }
            }
        }
    }

}