package com.dynamsoft.qrcodescanner

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.dynamsoft.dce.CameraEnhancer
import com.dynamsoft.dce.DCECameraView
import com.dynamsoft.qrcodescanner.ui.theme.QRCodeScannerTheme


class MainActivity : ComponentActivity() {
    private lateinit var mCameraEnhancer: CameraEnhancer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QRCodeScannerTheme {
                val context = LocalContext.current
                var hasCameraPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { granted ->
                        hasCameraPermission = granted
                        if (granted == true) {
                            mCameraEnhancer.open()
                        }
                    }
                )
                LaunchedEffect(key1 = true){
                  launcher.launch(Manifest.permission.CAMERA)
                }
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ){
                        AndroidView(factory = {context ->
                            mCameraEnhancer = CameraEnhancer(context.findActivity())
                            val mCameraView: DCECameraView
                            mCameraView = DCECameraView(context)
                            mCameraEnhancer.cameraView = mCameraView
                            mCameraView
                        })
                    }
                }
            }
        }
    }
}

//https://stackoverflow.com/a/74696154/17238341
fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}