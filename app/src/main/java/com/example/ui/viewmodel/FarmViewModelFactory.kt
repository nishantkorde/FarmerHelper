package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.database.FarmHelperDatabase
import com.example.data.repository.FarmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class FarmViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FarmViewModel::class.java)) {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
            val database = FarmHelperDatabase.getDatabase(context, scope)
            val repository = FarmRepository(database.farmHelperDao())
            return FarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
