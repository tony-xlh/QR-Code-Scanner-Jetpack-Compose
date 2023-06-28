package com.dynamsoft.qrcodescanner

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import com.dynamsoft.dbr.BarcodeReader
import com.dynamsoft.dbr.BarcodeReaderException
import com.dynamsoft.dce.CameraEnhancer
import com.dynamsoft.dce.DCECameraView
import com.dynamsoft.qrcodescanner.ui.theme.QRCodeScannerTheme


class MainActivity : ComponentActivity() {
    private lateinit var mCameraEnhancer: CameraEnhancer
    private lateinit var mBarcodeReader: BarcodeReader
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
                            startScanning()
                            mCameraEnhancer.open()
                        }
                    }
                )
                LaunchedEffect(key1 = true){
                    initLicense()
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
                            mCameraView.overlayVisible = true
                            mCameraEnhancer.cameraView = mCameraView
                            mCameraView
                        })
                    }
                }
            }
        }
    }

    private fun initLicense(){
        BarcodeReader.initLicense(
            "DLS2eyJoYW5kc2hha2VDb2RlIjoiMjAwMDAxLTE2NDk4Mjk3OTI2MzUiLCJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSIsInNlc3Npb25QYXNzd29yZCI6IndTcGR6Vm05WDJrcEQ5YUoifQ=="
        ) { isSuccess, error ->
            if (!isSuccess) {
                error.printStackTrace()
            }else{
                Log.d("DBR","license initialized")
            }
        }
    }

    private fun startScanning(){
        if (mBarcodeReader == null) {
            try {
                Log.d("DBR", "new instance of DBR")
                // Create an instance of Dynamsoft Barcode Reader.
                mBarcodeReader = BarcodeReader()
                // Bind the Camera Enhancer instance to the Barcode Reader instance to get frames from camera.
                mBarcodeReader.setCameraEnhancer(mCameraEnhancer)
                mBarcodeReader.setTextResultListener { id, imageData, textResults ->
                    Log.d("DBR", textResults.size.toString())
                }

            } catch (e: BarcodeReaderException) {
                e.printStackTrace()
            }
        }
        mBarcodeReader.startScanning()
    }
}

//https://stackoverflow.com/a/74696154/17238341
fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
