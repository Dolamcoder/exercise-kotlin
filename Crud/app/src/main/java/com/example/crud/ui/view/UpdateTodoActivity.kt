package com.example.crud.ui.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.crud.data.model.Todo
import com.example.crud.databinding.ActivityUpdateTodoBinding
import com.example.crud.ui.viewmodels.TodoViewModel
import java.util.Calendar
import java.util.Locale

class UpdateTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateTodoBinding
    private val viewModel: TodoViewModel by viewModels()
    private var selectedDateTime = ""
    private lateinit var todo: Todo
    private val statuses = arrayOf("pending", "completed", "hết_hạn")
    private val statusTexts = arrayOf("Chưa hoàn thành", "Hoàn thành", "Hết hạn")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy todo từ Intent
        todo = intent.getParcelableExtra("todo") ?: return

        setupSpinner()
        loadTodoData()
        setupButtons()
        observeViewModel()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusTexts)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStatus.adapter = adapter
    }

    private fun loadTodoData() {
        binding.editTextName.setText(todo.title)
        binding.editTextDescription.setText(todo.description)
        binding.editTextDeadline.text = todo.deadline.ifEmpty { "Chọn hạn chót" }
        selectedDateTime = todo.deadline

        // Đặt trạng thái trong Spinner
        val statusIndex = statuses.indexOf(todo.status)
        if (statusIndex >= 0) {
            binding.spinnerStatus.setSelection(statusIndex)
        }
    }

    private fun setupButtons() {
        // Xử lý nút chọn hạn chót
        binding.editTextDeadline.setOnClickListener {
            openDateTimePicker()
        }

        // Xử lý nút hủy
        binding.buttonCancel.setOnClickListener {
            finish()
        }

        // Xử lý nút cập nhật
        binding.buttonUpdate.setOnClickListener {
            handleUpdate()
        }
    }

    private fun openDateTimePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        selectedDateTime = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d %02d:%02d",
                            dayOfMonth,
                            month + 1,
                            year,
                            hourOfDay,
                            minute
                        )
                        binding.editTextDeadline.text = selectedDateTime
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun handleUpdate() {
        val taskName = binding.editTextName.text.toString().trim()
        val taskDescription = binding.editTextDescription.text.toString().trim()
        val taskDeadline = selectedDateTime
        val statusIndex = binding.spinnerStatus.selectedItemPosition
        val taskStatus = statuses[statusIndex]

        if (taskName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên công việc", Toast.LENGTH_SHORT).show()
            return
        }

        // Cập nhật todo object
        val updatedTodo = todo.copy(
            title = taskName,
            description = taskDescription,
            deadline = taskDeadline,
            status = taskStatus
        )

        // Gọi viewModel để cập nhật
        viewModel.updateTodo(updatedTodo)
    }

    private fun observeViewModel() {
        viewModel.todoResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Cập nhật công việc thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, "Lỗi: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

