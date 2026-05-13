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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
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
    var todos by remember { mutableStateOf<List<Todo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTodo by remember { mutableStateOf<Todo?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Load todos when screen appears
    LaunchedEffect(Unit) {
        loadTodosFromFirestore(firestore) { loadedTodos ->
            todos = loadedTodos
            isLoading = false
        }
    }

    if (showEditDialog && selectedTodo != null) {
        EditTodoDialog(
            todo = selectedTodo!!,
            firestore = firestore,
            onDismiss = { showEditDialog = false },
            onTodoUpdated = { updatedTodo ->
                showEditDialog = false
                selectedTodo = null
                // Reload todos
                loadTodosFromFirestore(firestore) { loadedTodos ->
                    todos = loadedTodos
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Danh sách nhiệm vụ") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = android.content.Intent(context, TodoDetailsActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
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
            } else if (todos.isEmpty()) {
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
                            "Chưa có nhiệm vụ nào",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "Vui lòng thêm nhiệm vụ mới",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                Text(
                    "Tổng: ${todos.size} nhiệm vụ",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 12.dp),
                    color = MaterialTheme.colorScheme.outline
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(todos) { todo ->
                        TodoCard(
                            todo = todo,
                            onEdit = {
                                selectedTodo = todo
                                showEditDialog = true
                            },
                            onDelete = {
                                deleteTodoFromFirestore(
                                    todo.id,
                                    firestore,
                                    context
                                ) { success ->
                                    if (success) {
                                        todos = todos.filter { it.id != todo.id }
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
fun TodoCard(
    todo: Todo,
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
                        text = todo.taskName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Thời gian: ${todo.studyDuration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (todo.description.isNotEmpty()) {
                        Text(
                            text = todo.description,
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
fun EditTodoDialog(
    todo: Todo,
    firestore: FirebaseFirestore,
    onDismiss: () -> Unit,
    onTodoUpdated: (Todo) -> Unit
) {
    var taskName by remember { mutableStateOf(TextFieldValue(todo.taskName)) }
    var description by remember { mutableStateOf(TextFieldValue(todo.description)) }
    var studyDuration by remember { mutableStateOf(TextFieldValue(todo.studyDuration)) }
    var isUpdating by remember { mutableStateOf(false) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa nhiệm vụ") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Tên nhiệm vụ") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = studyDuration,
                    onValueChange = { studyDuration = it },
                    label = { Text("Thời gian") },
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
                    if (taskName.text.isNotEmpty()) {
                        isUpdating = true
                        updateTodoInFirestore(
                            todo.id,
                            taskName.text,
                            studyDuration.text,
                            description.text,
                            firestore,
                            context
                        ) { success ->
                            isUpdating = false
                            if (success) {
                                onTodoUpdated(
                                    todo.copy(
                                        taskName = taskName.text,
                                        description = description.text,
                                        studyDuration = studyDuration.text
                                    )
                                )
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Vui lòng nhập tên nhiệm vụ",
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
fun loadTodosFromFirestore(
    firestore: FirebaseFirestore,
    onResult: (List<Todo>) -> Unit
) {
    firestore.collection("todos")
        .get()
        .addOnSuccessListener { querySnapshot ->
            val todosList = querySnapshot.documents.map { doc ->
                Todo(
                    id = doc.id,
                    taskName = doc.getString("taskName") ?: "",
                    studyDuration = doc.getString("studyDuration") ?: "",
                    description = doc.getString("description") ?: "",
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }.sortedByDescending { it.timestamp }
            android.util.Log.d("Firestore", "✓ Loaded ${todosList.size} todos")
            onResult(todosList)
        }
        .addOnFailureListener { e ->
            android.util.Log.e("Firestore", "✗ Error loading todos: ${e.message}", e)
            android.util.Log.e("Firestore", "Exception: ", e)
            onResult(emptyList())
        }
}

fun updateTodoInFirestore(
    todoId: String,
    taskName: String,
    studyDuration: String,
    description: String,
    firestore: FirebaseFirestore,
    context: android.content.Context,
    onResult: (Boolean) -> Unit
) {
    android.util.Log.d("Firestore", "Updating todo: $todoId")

    val updateData = mapOf(
        "taskName" to taskName,
        "studyDuration" to studyDuration,
        "description" to description
    )

    firestore.collection("todos")
        .document(todoId)
        .update(updateData)
        .addOnSuccessListener {
            android.util.Log.d("Firestore", "✓ Todo updated successfully: $todoId")
            Toast.makeText(
                context,
                "Nhiệm vụ được cập nhật thành công!",
                Toast.LENGTH_SHORT
            ).show()
            onResult(true)
        }
        .addOnFailureListener { e ->
            android.util.Log.e("Firestore", "✗ Error updating todo: ${e.message}", e)
            Toast.makeText(
                context,
                "Lỗi: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            onResult(false)
        }
}

fun deleteTodoFromFirestore(
    todoId: String,
    firestore: FirebaseFirestore,
    context: android.content.Context,
    onResult: (Boolean) -> Unit
) {
    android.util.Log.d("Firestore", "Deleting todo: $todoId")

    firestore.collection("todos")
        .document(todoId)
        .delete()
        .addOnSuccessListener {
            android.util.Log.d("Firestore", "✓ Todo deleted successfully: $todoId")
            Toast.makeText(
                context,
                "Nhiệm vụ được xóa thành công!",
                Toast.LENGTH_SHORT
            ).show()
            onResult(true)
        }
        .addOnFailureListener { e ->
            android.util.Log.e("Firestore", "✗ Error deleting todo: ${e.message}", e)
            Toast.makeText(
                context,
                "Lỗi: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            onResult(false)
        }
}

