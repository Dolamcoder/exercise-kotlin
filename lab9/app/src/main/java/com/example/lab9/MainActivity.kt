package com.example.lab9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.lab9.ui.BluromaticScreen
import com.example.lab9.ui.theme.BluromaticTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BluromaticTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    BluromaticScreen()
                }
            }
        }
    }
}
