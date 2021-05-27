package com.sedavnyh.todo.fragments

import android.app.Application
import android.text.TextUtils
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sedavnyh.todo.R
import com.sedavnyh.todo.data.models.Priority
import com.sedavnyh.todo.data.models.ToDoData

class SharedViewModel(application: Application): AndroidViewModel(application) {

    /*======================================== List Fragment ========================================*/

    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(false)

    fun checkIfDatabaseIsEmpty(toDoData: List<ToDoData>) {
        emptyDatabase.value = toDoData.isEmpty()
    }

    /*======================================== Update Fragment ========================================*/

    val listener: AdapterView.OnItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long) {
            when(position){
                0 ->{(parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.red))}
                1 ->{(parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.yellow))}
                2 ->{(parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application, R.color.green ))}
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) { }
    }

    fun verifyData(title: String, description: String): Boolean{
        return !(title.isEmpty() || description.isEmpty())

    }

    fun parsePriority(priority: String) : Priority {
        return when(priority){
            "High Priority" -> Priority.HIGHT
            "Medium Priority" -> Priority.MEDIUM
            "Low Priority" -> Priority.LOW
            else -> Priority.LOW
        }
    }

    fun parsePriorityToInt(priority: Priority): Int{
        return when(priority){
            Priority.HIGHT -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }
}