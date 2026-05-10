package com.example.lab3

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun DiceRollerApp() {
    var diceValue by remember { mutableIntStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    // Animation cho việc xoay xúc xắc
    val rotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 500),
        finishedListener = {
            isRolling = false
        },
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎲 Xúc Xắc",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Xúc xắc
        DiceFace(
            value = diceValue,
            modifier = Modifier
                .size(150.dp)
                .rotate(rotation)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Hiển thị giá trị
        Text(
            text = "Giá trị: $diceValue",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Nút lắc xúc xắc
        Button(
            onClick = {
                if (!isRolling) {
                    isRolling = true
                    rotationAngle += 360f
                    diceValue = Random.nextInt(1, 7)
                }
            },
            modifier = Modifier
                .width(200.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            enabled = !isRolling
        ) {
            Text(
                text = if (isRolling) "Đang lắc..." else "🎲 Lắc Xúc Xắc",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DiceFace(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            val dotRadius = size.minDimension / 10
            val dotColor = Color.Black

            // Vị trí các chấm trên xúc xắc
            val center = Offset(size.width / 2, size.height / 2)
            val topLeft = Offset(size.width * 0.25f, size.height * 0.25f)
            val topRight = Offset(size.width * 0.75f, size.height * 0.25f)
            val middleLeft = Offset(size.width * 0.25f, size.height * 0.5f)
            val middleRight = Offset(size.width * 0.75f, size.height * 0.5f)
            val bottomLeft = Offset(size.width * 0.25f, size.height * 0.75f)
            val bottomRight = Offset(size.width * 0.75f, size.height * 0.75f)

            // Vẽ các chấm tương ứng với giá trị
            when (value) {
                1 -> {
                    drawCircle(dotColor, dotRadius, center)
                }
                2 -> {
                    drawCircle(dotColor, dotRadius, topRight)
                    drawCircle(dotColor, dotRadius, bottomLeft)
                }
                3 -> {
                    drawCircle(dotColor, dotRadius, topRight)
                    drawCircle(dotColor, dotRadius, center)
                    drawCircle(dotColor, dotRadius, bottomLeft)
                }
                4 -> {
                    drawCircle(dotColor, dotRadius, topLeft)
                    drawCircle(dotColor, dotRadius, topRight)
                    drawCircle(dotColor, dotRadius, bottomLeft)
                    drawCircle(dotColor, dotRadius, bottomRight)
                }
                5 -> {
                    drawCircle(dotColor, dotRadius, topLeft)
                    drawCircle(dotColor, dotRadius, topRight)
                    drawCircle(dotColor, dotRadius, center)
                    drawCircle(dotColor, dotRadius, bottomLeft)
                    drawCircle(dotColor, dotRadius, bottomRight)
                }
                6 -> {
                    drawCircle(dotColor, dotRadius, topLeft)
                    drawCircle(dotColor, dotRadius, topRight)
                    drawCircle(dotColor, dotRadius, middleLeft)
                    drawCircle(dotColor, dotRadius, middleRight)
                    drawCircle(dotColor, dotRadius, bottomLeft)
                    drawCircle(dotColor, dotRadius, bottomRight)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DiceRollerAppPreview() {
    MaterialTheme {
        DiceRollerApp()
    }
}