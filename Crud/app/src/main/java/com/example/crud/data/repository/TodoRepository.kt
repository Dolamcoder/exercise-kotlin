package com.example.crud.data.repository

import com.example.crud.data.model.Todo
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TodoRepository {
    private val database= FirebaseDatabase.getInstance();
    private val todosRef=database.getReference("todos")
    fun getTodos(callback: (List<Todo>)-> Unit){
        todosRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list=mutableListOf<Todo>()
                snapshot.children.forEach {
                    val todo=it.getValue(Todo::class.java)
                    if(todo!=null){
                        list.add(todo)
                    }
                }
                callback(list)
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
    fun createTodo(todo: Todo, result: (Boolean, String?)-> Unit){
        val id= todosRef.push().key ?: ""
        todo.id=id
        todosRef.child(todo.id).setValue(todo)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    result(true, null)
                }
                else{
                    result(false, it.exception?.message)
                }
            }
    }
    fun deleteTodo(todoId: String, result:(Boolean, String?)->Unit){
        todosRef.child(todoId).removeValue()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    result(true, null)
                }
                else{
                    result(false, it.exception?.message)
                }
            }
    }
    fun getTodoById(todoId: String, result: (Todo?, String?)-> Unit){
        todosRef.child(todoId).get()
            .addOnSuccessListener { snapshot ->
                val todo=snapshot.getValue(Todo::class.java)
                result(todo, null)
            }
            .addOnFailureListener { exception ->
                result(null, exception.message)
            }
    }
    fun updateTodo(todo:Todo, result:(Boolean, String?)->Unit){
        todosRef.child(todo.id).setValue(todo)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    result(true, null)
                }
                else{
                    result(false, it.exception?.message)
                }
            }
    }

}