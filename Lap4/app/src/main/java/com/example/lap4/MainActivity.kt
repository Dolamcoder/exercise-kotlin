package com.example.lap4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lap4.ui.theme.Lap4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lap4Theme {
                TipCalculatorScreen()
            }
        }
    }
}

@Composable
fun TipCalculatorScreen() {
    var billAmount by remember { mutableStateOf("") }
    var tipPercentage by remember { mutableStateOf("") }
    var roundUp by remember { mutableStateOf(false) }

    val billValue = billAmount.toDoubleOrNull() ?: 0.0
    val tipValue = tipPercentage.toDoubleOrNull() ?: 0.0

    val tipAmount = billValue * tipValue / 100
    val totalTipAmount = if (roundUp) kotlin.math.ceil(tipAmount) else tipAmount

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Text(
                text = "Calculate Tip",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Bill Amount Input
            OutlinedTextField(
                value = billAmount,
                onValueChange = { billAmount = it },
                label = { Text("Bill Amount") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Bill Amount"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5D3DB), shape = MaterialTheme.shapes.medium),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tip Percentage Input
            OutlinedTextField(
                value = tipPercentage,
                onValueChange = { tipPercentage = it },
                label = { Text("Tip Percentage") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Tip Percentage"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5D3DB), shape = MaterialTheme.shapes.medium),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Round up toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Round up tip?",
                    fontSize = 16.sp
                )
                Switch(
                    checked = roundUp,
                    onCheckedChange = { roundUp = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tip Amount Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tip Amount: $${String.format("%.2f", totalTipAmount)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}