package com.era.androidselfiecamera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotoapparat)

        imageView = findViewById(R.id.imageView)
        buttonPhoto.isEnabled = false

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),111)
        }else{
            buttonPhoto.isEnabled = true
        }

        buttonPhoto.setOnClickListener {
            buttonPhoto.visibility = View.GONE
            imageView.visibility = View.GONE
            cameraView.visibility = View.VISIBLE
            captureBtn.visibility = View.VISIBLE
            fotoapparat.start()
        }
        fotoapparat = Fotoapparat(
            context = this,
            view = cameraView,
            //focusView = focusView,
            logger = logcat(),
            lensPosition = activeCamera.lensPosition,
            cameraConfiguration = activeCamera.configuration
        )

        captureBtn.setOnClickListener {
           var photoResult = fotoapparat.takePicture()
//            Bitmap result = photoResult.toBitmap().await()
//            imageView.setImageBitmap(result)


               .toBitmap(scaled(scaleFactor = 0.10f))

//            Toast.makeText(this,photoResult,Toast.LENGTH_SHORT).show()
//            val imageBytes = Base64.decode(photoResult, 0)
//            val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//            imageView.setImageBitmap(photoResult)

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
            cameraView.visibility = View.GONE
            captureBtn.visibility = View.GONE
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
}
private const val LOGGING_TAG = "Fotoapparat Example"

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