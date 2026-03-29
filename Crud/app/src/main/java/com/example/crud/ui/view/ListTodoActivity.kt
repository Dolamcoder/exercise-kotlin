package com.example.crud.ui.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crud.data.model.Todo
import com.example.crud.ui.view.adapter.TodoAdapter
import com.example.crud.databinding.ActivityListTodoBinding
import com.example.crud.ui.viewmodels.TodoViewModel
import com.google.firebase.database.FirebaseDatabase

class ListTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListTodoBinding
    private val viewModel: TodoViewModel by viewModels()
    private var todos = listOf<Todo>()
    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Khởi tạo View Binding
        binding = ActivityListTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        setupRecyclerView()
        setupButtons()
        viewModel.getTodos()
    }

    private fun observeViewModel() {
        viewModel.todos.observe(this){ list->
            adapter.updateTodos(list)
        }
        viewModel.todoResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Xóa công việc thành công", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.updateStatusResutl.observe(this){success->
            if(success){
                Toast.makeText(this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = TodoAdapter(
            todos = todos,

            // DELETE
            onDeleteClick = { todoId ->
                deleteTodo(todoId)
            },

            // UPDATE STATUS
            onUpdateTodoStatus = { todoId, status ->
                viewModel.updateTodoStatus(todoId, status)            },
            // EDIT (tính năng mở rộng)
            onEditClick={todo->
                editTodo(todo)
            },
        )
        binding.recyclerViewTodo.apply {
            layoutManager = LinearLayoutManager(this@ListTodoActivity)
            adapter = this@ListTodoActivity.adapter
        }
    }
    private fun setupButtons() {
        // Xử lý nút Thêm công việc
        binding.buttonAdd.setOnClickListener {
            startActivity(Intent(this, AddTodoActivity::class.java))
        }
    }
    private fun deleteTodo(todoId: String) {
        // Hiển thị xác nhận trước khi xóa
        AlertDialog.Builder(this)
            .setTitle("Xóa công việc")
            .setMessage("Bạn có chắc chắn muốn xóa công việc này?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteTodo(todoId)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
    fun editTodo(todo: Todo){
        val intent = Intent(this, UpdateTodoActivity::class.java)
        intent.putExtra("todo", todo)
        startActivity(intent)
    }
}