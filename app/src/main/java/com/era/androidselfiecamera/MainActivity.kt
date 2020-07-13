package com.era.androidselfiecamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private var output: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.isEnabled = false

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),111)
        }else{
            button.isEnabled = true
        }

        button.setOnClickListener {
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            i.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            i.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            i.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
            val dir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

            

            output = File(dir, "CameraContentDemo.jpeg")
            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output))
            startActivityForResult(i,111)

//            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//                intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
//                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//                intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//            } else {
//                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//            }
//
//            //Uri fileUri = // your fileUri
//
//
//            startActivityForResult(intent, 111);


        }

    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            button.isEnabled = true;
        }
    }

    private fun openFrontFacingCameraGingerbread(): Camera? {
        var cameraCount = 0
        var cam: Camera? = null
        val cameraInfo = Camera.CameraInfo()
        cameraCount = Camera.getNumberOfCameras()
        for (camIdx in 0 until cameraCount) {
            Camera.getCameraInfo(camIdx, cameraInfo)
            if (cameraInfo.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx)
                } catch (e: RuntimeException) {
                    //Log.e(MainActivity.TAG, "Camera failed to open: " + e.localizedMessage)
                }
            }
        }
        return cam
    }
}