package com.example.crud.ui.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.crud.R
import com.example.crud.data.model.Todo
import com.example.crud.databinding.ActivityAddTodoBinding
import com.example.crud.ui.viewmodels.TodoViewModel
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddTodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTodoBinding
    private var selectedDateTime = ""
    private val viewModel: TodoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // Khởi tạo View Binding
        binding = ActivityAddTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtons()
        observeViewModel()
    }

    private fun setupButtons() {
        // Xử lý nút chọn hạn chót
        binding.editTextDeadline.setOnClickListener {
            openDateTimePicker()
        }

        // Xử lý nút quay lại
        binding.buttonCancel.setOnClickListener {
            finish()
        }

        // Xử lý nút lưu
        binding.buttonSave.setOnClickListener {
            handleSave()
        }
    }

    private fun openDateTimePicker() {
        val calendar = Calendar.getInstance()

        // DatePickerDialog
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Sau khi chọn ngày, mở TimePickerDialog
                TimePickerDialog(this, { _, hourOfDay, minute ->
                    selectedDateTime = String.Companion.format(
                        Locale.getDefault(), "%02d/%02d/%04d %02d:%02d",
                        dayOfMonth, month + 1, year, hourOfDay, minute
                    )

                    binding.editTextDeadline.text = selectedDateTime
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun handleSave() {
        val taskName = binding.editTextName.text.toString().trim()
        val taskDescription = binding.editTextDescription.text.toString().trim()
        val taskDeadline = selectedDateTime

        if (taskName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên công việc", Toast.LENGTH_SHORT).show()
            return
        }
        // Tạo Todo object
        val todoData = Todo(
            title = taskName,
            description = taskDescription,
            status = "pending",
            deadline = taskDeadline
        )
        // Lưu vào Firebase
        viewModel.createTodo(todoData)
    }
    private fun clearForm() {
        binding.editTextName.text?.clear()
        binding.editTextDescription.text?.clear()
        binding.editTextDeadline.text = getString(R.string.select_deadline)
        selectedDateTime = ""
    }
    private fun observeViewModel(){
        viewModel.todoResult.observe(this){
            Toast.makeText(this, "Thêm công việc thành công", Toast.LENGTH_SHORT).show()
            clearForm()
            startActivity(Intent(this, ListTodoActivity::class.java))
        }
        viewModel.errorMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}