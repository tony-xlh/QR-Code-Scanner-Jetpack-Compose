package com.dynamsoft.qrcodescanner

import android.R
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.dynamsoft.dce.CameraEnhancer
import com.dynamsoft.dce.DCECameraView
import com.dynamsoft.qrcodescanner.ui.theme.QRCodeScannerTheme


class MainActivity : ComponentActivity() {
    private lateinit var mCameraEnhancer: CameraEnhancer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRCodeScannerTheme {
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
                            mCameraEnhancer.open()
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