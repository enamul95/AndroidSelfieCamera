package com.era.androidselfiecamera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
import io.fotoapparat.result.transformer.scaled
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_fotoapparat.*


class FotoapparatSelfi : AppCompatActivity() {

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var imageView: ImageView
    private var activeCamera: Camera = Camera.Front
    var cameraStatus : CameraState? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotoapparat)

        imageView = findViewById(R.id.imageView)
        buttonPhoto.isEnabled = false
        toolbar.setTitle("Selfi Camera")

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),111)
        }else{
            buttonPhoto.isEnabled = true
        }

        buttonPhoto.setOnClickListener {
            buttonPhoto.visibility = View.GONE
            imageView.visibility = View.GONE
            toolbar.visibility = View.GONE
            cameraView.visibility = View.VISIBLE
            captureBtn.visibility = View.VISIBLE
            cameraLayout.visibility = View.VISIBLE
            fotoapparat.start()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            // Remember that you should never show the action bar if the
            // status bar is hidden, so hide that too if necessary.
            actionBar?.hide()
        }
        fotoapparat = Fotoapparat(
            context = this,
            view = cameraView,
            //focusView = focusView,
            logger = logcat(),
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration
        )

        switchCamera.setOnClickListener {
            switchCamera()
        }

        captureBtn.setOnClickListener {
           var photoResult = fotoapparat.takePicture()


               .toBitmap(scaled(scaleFactor = 0.10f))


               .whenAvailable { photo ->
                   photo
                       ?.let {
                           Log.i(LOGGING_TAG, "New photo captured. Bitmap length: ${it.bitmap.byteCount}")

                           val imageView = findViewById<ImageView>(R.id.imageView)

                           imageView.setImageBitmap(it.bitmap)
                           imageView.rotation = (-it.rotationDegrees).toFloat()
                       }
                       ?: Log.e(LOGGING_TAG, "Couldn't capture photo.")
               }
            buttonPhoto.visibility = View.VISIBLE
            imageView.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
            cameraView.visibility = View.GONE
            captureBtn.visibility = View.GONE
            cameraLayout.visibility = View.GONE
            //fotoapparat.stop()
        }



    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            buttonPhoto.isEnabled = true;
        }
    }

    private fun switchCamera() {
        fotoapparat?.switchTo(
            lensPosition =  if (cameraStatus == CameraState.BACK) front() else back(),
            cameraConfiguration = CameraConfiguration()
        )

        if(cameraStatus == CameraState.BACK) cameraStatus = CameraState.FRONT
        else cameraStatus = CameraState.BACK
    }
}
private const val LOGGING_TAG = "Fotoapparat Example"

enum class CameraState{
    FRONT, BACK
}

private sealed class Camera(
    val lensPosition: LensPositionSelector,
    val configuration: CameraConfiguration
) {

    object Back : Camera(
        lensPosition = back(),
        configuration = CameraConfiguration(
            previewResolution = firstAvailable(
                wideRatio(highestResolution()),
                standardRatio(highestResolution())
            ),
            previewFpsRange = highestFps(),
            flashMode = off(),
            focusMode = firstAvailable(
                continuousFocusPicture(),
                autoFocus()
            ),
            frameProcessor = {
                // Do something with the preview frame
            }
        )
    )

    object Front : Camera(
        lensPosition = front(),
        configuration = CameraConfiguration(
            previewResolution = firstAvailable(
                wideRatio(highestResolution()),
                standardRatio(highestResolution())
            ),
            previewFpsRange = highestFps(),
            flashMode = off(),
            focusMode = firstAvailable(
                fixed(),
                autoFocus()
            )
        )
    )
}