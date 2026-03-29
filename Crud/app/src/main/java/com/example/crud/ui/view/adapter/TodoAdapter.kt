package com.example.crud.ui.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.crud.data.model.StatusUI
import com.example.crud.data.model.Todo
import com.example.crud.databinding.ItemTodoBinding
import com.google.firebase.database.FirebaseDatabase

class TodoAdapter(
    private var todos:List<Todo>,
    private val onDeleteClick: (String) -> Unit,
    private val onUpdateTodoStatus: (String, String)->Unit,
    private val onEditClick: (Todo)->Unit

) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(private val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            // Đặt dữ liệu vào UI
            binding.textViewName.text = todo.title
            binding.textViewDescription.text = todo.description
            binding.textViewDeadline.text = ("Hạn chót: " + todo.deadline)

            // Đặt trạng thái công việc
            val status = todo.status
            val statusUI=convertStatus(status)
            binding.textViewStatus.text = statusUI.text
            binding.textViewStatus.setTextColor(statusUI.color)
            val todoId=todo.id
            // Xử lý nút xóa
            binding.buttonDelete.setOnClickListener {
                onDeleteClick(todoId)
            }

            // Xử lý nút chỉnh sửa (tính năng mở rộng)
            binding.buttonEdit.setOnClickListener {
                onEditClick(todo)
            }
            // Tùy chọn: Ẩn nút hoàn thành nếu đã hoàn thành
            binding.buttonComplete.setOnClickListener {
                val newStatus = if (status == "completed") "pending" else "completed"
                onUpdateTodoStatus(todoId, newStatus)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int = todos.size

    fun updateTodos(newTodos: List<Todo>) {
        todos = newTodos
        notifyDataSetChanged()
    }

    fun convertStatus(status: String): StatusUI {
        return when (status) {
            "completed" -> StatusUI("Hoàn thành", Color.GREEN)
            "hết_hạn" -> StatusUI("✗ Hết hạn", Color.RED)
            else -> StatusUI("Chưa hoàn thành", Color.GRAY)
        }
    }
}