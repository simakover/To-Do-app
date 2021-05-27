package com.sedavnyh.todo.data.repository

import androidx.lifecycle.LiveData
import com.sedavnyh.todo.data.ToDoDao
import com.sedavnyh.todo.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()
    val searchByHighPriority: LiveData<List<ToDoData>> = toDoDao.searchByHighPriority()
    val searchByLowPriority: LiveData<List<ToDoData>> = toDoDao.searchByLowPriority()

    suspend fun insertData(toDoData: ToDoData){
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData){
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteData(toDoData: ToDoData){
        toDoDao.deleteData(toDoData)
    }

    suspend fun deleteAllData(){
        toDoDao.deleteAllData()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<ToDoData>>{
        return toDoDao.searchDatabase(searchQuery)
    }

}