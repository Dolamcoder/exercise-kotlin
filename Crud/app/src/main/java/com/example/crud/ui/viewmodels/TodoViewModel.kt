package com.example.crud.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.crud.data.model.Todo
import com.example.crud.data.repository.TodoRepository

class TodoViewModel: ViewModel() {
    private val repo= TodoRepository();
    val todoResult= MutableLiveData<Boolean>()
    val updateStatusResutl= MutableLiveData<Boolean>()
    val todos=MutableLiveData<List<Todo>>()
    val errorMessage= MutableLiveData<String>()
    val todoDetail= MutableLiveData<Todo>()
    fun getTodos(){
        repo.getTodos {list->
            todos.postValue(list)
        }
    }
    fun createTodo(todo: Todo){
        repo.createTodo(todo){success, error->
            if(success) todoResult.postValue(true)
            else errorMessage.postValue(error)
        }
    }
    fun deleteTodo(todoId:String){
        repo.deleteTodo(todoId){success, error->
            if(success) todoResult.postValue(true)
            else errorMessage.postValue(error)
        }
    }
    fun getTodoById(todoId: String){
        repo.getTodoById(todoId){todo, error->
            if(todo!=null) todoDetail.postValue(todo)
            else errorMessage.postValue(error)
        }
    }
    fun updateTodoStatus(todoId: String, newStatus: String){
        repo.getTodoById(todoId){todo, error->
            if(todo!=null){
                val updatedTodo=todo.copy(status = newStatus)
                repo.updateTodo(updatedTodo){success, error->
                    if(success) updateStatusResutl.postValue(true)
                    else errorMessage.postValue(error)
                }
            }
            else errorMessage.postValue(error)
        }
    }
    fun updateTodo(todo: Todo){
        repo.updateTodo(todo){success, error->
            if(success) todoResult.postValue(true)
            else errorMessage.postValue(error)
        }
    }
}