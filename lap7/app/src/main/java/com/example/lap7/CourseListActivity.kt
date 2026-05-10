package com.example.lap7

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.lap7.Course
import com.example.lap7.ui.theme.Lap7Theme
import com.google.firebase.firestore.FirebaseFirestore

class CourseListActivity : ComponentActivity() {
    private val firestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lap7Theme {
                CourseListScreen(firestore = firestore)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListScreen(firestore: FirebaseFirestore) {
    var courses by remember { mutableStateOf<List<Course>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load courses when screen appears
    LaunchedEffect(Unit) {
        loadCoursesFromFirestore(firestore) { loadedCourses ->
            courses = loadedCourses
            isLoading = false
        }
    }

    if (showEditDialog && selectedCourse != null) {
        EditCourseDialog(
            course = selectedCourse!!,
            firestore = firestore,
            onDismiss = { showEditDialog = false },
            onCourseUpdated = { updatedCourse ->
                showEditDialog = false
                selectedCourse = null
                // Reload courses
                loadCoursesFromFirestore(firestore) { loadedCourses ->
                    courses = loadedCourses
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách khóa học") },
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
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (courses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Chưa có khóa học nào",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Vui lòng thêm khóa học mới",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                Text(
                    "Tổng: ${courses.size} khóa học",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.outline
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(courses) { course ->
                        CourseCard(
                            course = course,
                            onEdit = {
                                selectedCourse = course
                                showEditDialog = true
                            },
                            onDelete = {
                                deleteCourseFromFirestore(
                                    course.id,
                                    firestore,
                                    context
                                ) { success ->
                                    if (success) {
                                        courses = courses.filter { it.id != course.id }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CourseCard(
    course: Course,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        text = course.courseName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Thời gian: ${course.studyDuration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (course.description.isNotEmpty()) {
                        Text(
                            text = course.description,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text("Chỉnh sửa")
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text("Xóa")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCourseDialog(
    course: Course,
    firestore: FirebaseFirestore,
    onDismiss: () -> Unit,
    onCourseUpdated: (Course) -> Unit
) {
    var courseName by remember { mutableStateOf(TextFieldValue(course.courseName)) }
    var description by remember { mutableStateOf(TextFieldValue(course.description)) }
    var studyDuration by remember { mutableStateOf(TextFieldValue(course.studyDuration)) }
    var isUpdating by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa khóa học") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Tên khóa học") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = studyDuration,
                    onValueChange = { studyDuration = it },
                    label = { Text("Thời gian học") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (courseName.text.isNotEmpty()) {
                        isUpdating = true
                        updateCourseInFirestore(
                            course.id,
                            courseName.text,
                            studyDuration.text,
                            description.text,
                            firestore,
                            context
                        ) { success ->
                            isUpdating = false
                            if (success) {
                                onCourseUpdated(
                                    course.copy(
                                        courseName = courseName.text,
                                        description = description.text,
                                        studyDuration = studyDuration.text
                                    )
                                )
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Vui lòng nhập tên khóa học",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = !isUpdating
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Lưu")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss, enabled = !isUpdating) {
                Text("Hủy")
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun loadCoursesFromFirestore(
    firestore: FirebaseFirestore,
    onResult: (List<Course>) -> Unit
) {
    firestore.collection("courses")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val coursesList = querySnapshot.documents.map { doc ->
                Course(
                    id = doc.id,
                    courseName = doc.getString("courseName") ?: "",
                    studyDuration = doc.getString("studyDuration") ?: "",
                    description = doc.getString("description") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }.sortedByDescending { it.timestamp }
            android.util.Log.d("Firestore", "✓ Loaded ${coursesList.size} courses")
            onResult(coursesList)
        }
        .addOnFailureListener { e ->
            android.util.Log.e("Firestore", "✗ Error loading courses: ${e.message}", e)
            android.util.Log.e("Firestore", "Exception: ", e)
            onResult(emptyList())
        }
}

fun updateCourseInFirestore(
    courseId: String,
    courseName: String,
    studyDuration: String,
    description: String,
    firestore: FirebaseFirestore,
    context: android.content.Context,
    onResult: (Boolean) -> Unit
) {
    android.util.Log.d("Firestore", "Updating course: $courseId")

    val updateData = mapOf(
        "courseName" to courseName,
        "studyDuration" to studyDuration,
        "description" to description
    )

    firestore.collection("courses")
        .document(courseId)
        .update(updateData)
        .addOnSuccessListener {
            android.util.Log.d("Firestore", "✓ Course updated successfully: $courseId")
            Toast.makeText(
                context,
                "Khóa học được cập nhật thành công!",
                Toast.LENGTH_SHORT
            ).show()
            onResult(true)
        }
        .addOnFailureListener { e ->
            android.util.Log.e("Firestore", "✗ Error updating course: ${e.message}", e)
            Toast.makeText(
                context,
                "Lỗi: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            onResult(false)
        }
}

fun deleteCourseFromFirestore(
    courseId: String,
    firestore: FirebaseFirestore,
    context: android.content.Context,
    onResult: (Boolean) -> Unit
) {
    android.util.Log.d("Firestore", "Deleting course: $courseId")

    firestore.collection("courses")
        .document(courseId)
        .delete()
        .addOnSuccessListener {
            android.util.Log.d("Firestore", "✓ Course deleted successfully: $courseId")
            Toast.makeText(
                context,
                "Khóa học được xóa thành công!",
                Toast.LENGTH_SHORT
            ).show()
            onResult(true)
        }
        .addOnFailureListener { e ->
            android.util.Log.e("Firestore", "✗ Error deleting course: ${e.message}", e)
            Toast.makeText(
                context,
                "Lỗi: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            onResult(false)
        }
}

