package com.era.androidselfiecamera

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.logcat
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