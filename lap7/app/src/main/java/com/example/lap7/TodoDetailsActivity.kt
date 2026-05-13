package com.example.lap7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lap7.ui.theme.Lap7Theme
import com.google.firebase.firestore.FirebaseFirestore

class TodoDetailsActivity : ComponentActivity() {
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Enable Firestore persistence for offline support
        try {
            firestore.firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error setting persistence: ${e.message}")
        }
        
        setContent {
            Lap7Theme {
                TodoDetailsScreen(
                    firestore = firestore
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailsScreen(
    firestore: FirebaseFirestore
) {
    var taskName by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var studyDuration by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý nhiệm vụ") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Header
            Text(
                text = "GFG",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Task Name Input
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Tên nhiệm vụ") },
                placeholder = { Text("CS - Công ghệ Thông tin ứng dụng") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )

            // Study Duration Input
            OutlinedTextField(
                value = studyDuration,
                onValueChange = { studyDuration = it },
                label = { Text("Thời gian") },
                placeholder = { Text("3 tháng") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )

            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                placeholder = { Text("Công nghệ thông tin ứng dụng") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                minLines = 3,
                maxLines = 5
            )

            // Add Data Button
            Button(
                onClick = {
                    if (taskName.text.isNotEmpty() && studyDuration.text.isNotEmpty()) {
                        addTodoToFirestore(
                            taskName.text,
                            studyDuration.text,
                            description.text,
                            firestore,
                            context
                        )
                        // Reset fields
                        taskName = TextFieldValue("")
                        studyDuration = TextFieldValue("")
                        description = TextFieldValue("")
                    } else {
                        Toast.makeText(
                            context,
                            "Vui lòng nhập tên nhiệm vụ và thời gian",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "Add Data",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // View Todos Button
            Button(
                onClick = {
                    val intent = android.content.Intent(context, CourseListActivity::class.java)
                    context.startActivity(intent)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "View Todos",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun addTodoToFirestore(
    taskName: String,
    studyDuration: String,
    description: String,
    firestore: FirebaseFirestore,
    context: android.content.Context
) {
    android.util.Log.d("Firestore", "Starting to add todo: $taskName")
    
    val todoData = mapOf(
        "taskName" to taskName,
        "studyDuration" to studyDuration,
        "description" to description,
        "timestamp" to System.currentTimeMillis()
    )

    android.util.Log.d("Firestore", "Todo data: $todoData")

    firestore.collection("todos")
        .add(todoData)
        .addOnSuccessListener { documentReference ->
            android.util.Log.d("Firestore", "✓ Todo added successfully with ID: ${documentReference.id}")
            Toast.makeText(
                context, 
                "Nhiệm vụ được thêm thành công! (ID: ${documentReference.id})",
                Toast.LENGTH_SHORT
            ).show()
        }
        .addOnFailureListener { e ->
            android.util.Log.e("Firestore", "✗ Error adding todo: ${e.message}", e)
            val errorMessage = when {
                e.message?.contains("PERMISSION_DENIED") == true -> 
                    "Lỗi quyền: Vui lòng kiểm tra cấu hình Firestore Rules"
                e.message?.contains("network") == true -> 
                    "Lỗi mạng: Vui lòng kiểm tra kết nối internet"
                else -> "Lỗi: ${e.message}"
            }
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
}

@Preview(showBackground = true)
@Composable
fun TodoDetailsScreenPreview() {
    Lap7Theme {
        val firestore = FirebaseFirestore.getInstance()
        TodoDetailsScreen(
            firestore = firestore
        )
    }
}


