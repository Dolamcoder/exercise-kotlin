 package com.example.crud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.crud.databinding.ActivityMainBinding
import com.example.crud.ui.view.ListTodoActivity

 class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Khởi tạo View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Xử lý nút Bắt đầu
        binding.start.setOnClickListener {
            Log.d("MainActivity", "Button clicked!")
            startActivity(Intent(this, ListTodoActivity::class.java))
        }
    }
}