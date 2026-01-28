package com.example.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debug.ui.theme.DebugTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DebugTheme {
                BusinessCardScreen()
            }
        }
    }
}

@Composable
fun BusinessCardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDCEFE3)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))

        // Logo Android
        Image(
            painter = painterResource(id = R.drawable.ic_android_black_24dp),
            contentDescription = "Android Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Đỗ Lam",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light
        )

        Text(
            text = "Học lập triình android ",
            fontSize = 14.sp,
            color = Color(0xFF0F6D4C),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        ContactItem(Icons.Default.Phone, "+81 0868299526")
        ContactItem(Icons.Default.Share, "@AndroidDev")
        ContactItem(Icons.Default.Email, "dolam261106.com")

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ContactItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF0F6D4C)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBusinessCard() {
    DebugTheme {
        BusinessCardScreen()
    }
}
